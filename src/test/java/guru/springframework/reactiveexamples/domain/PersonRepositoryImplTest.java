package guru.springframework.reactiveexamples.domain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PersonRepositoryImplTest {

    PersonRepository personRepository;
    Person expectedPerson;

    @BeforeEach
    void setup() {
        personRepository = new PersonRepositoryImpl();
        expectedPerson = new Person(1, "Michael", "Weston");
    }

    @Test
    @DisplayName("getById - block gets existing person by id")
    void getById_BlockReturnsExistingPerson() {
        Mono<Person> actualPerson = personRepository.getById(1);
        assertEquals(expectedPerson, actualPerson.block());
    }

    @Test
    @DisplayName("getById - Subscribe gets existing person by id")
    void getById_SubscribeReturnsExistingPerson() {
        Mono<Person> actualPerson = personRepository.getById(1);
        actualPerson.subscribe(person -> {
            assertEquals(expectedPerson, actualPerson.block());
        });
    }

    @Test
    @DisplayName("getById - Subscribe gets existing person by id")
    void getById_SubscribeReturnsFirstNameFromMap() {
        Mono<Person> actualPerson = personRepository.getById(1);
        actualPerson
                .map(person -> person.getFirstName())
                .subscribe(firstName -> {
                    assertEquals(expectedPerson.getFirstName(), firstName);
                });
    }

    @Test
    @DisplayName("getById - returns null for non-existing person")
    void getById_ReturnsNullForNonExistingPerson() {
        Mono<Person> actualPerson = personRepository.getById(-1);
        actualPerson
                .doOnError(e -> {
                    System.out.println(e);
                })
                .onErrorReturn(null)
                .subscribe(Assertions::assertNull);
    }

    @Test
    @DisplayName("findAll - blockFirst returns first person")
    void findAll_BlockFirstReturnsFirstPerson() {
        Flux<Person> personFlux = personRepository.findAll();
        Person personActual = personFlux.blockFirst();
        assertEquals(expectedPerson, personActual);
    }

    @Test
    @DisplayName("findAll - subscribe returns all persons")
    void findAll_SubscribeReturnsAllPersons() {
        Map<Integer, Person> personIds = new HashMap<>();
        Flux<Person> personFlux = personRepository.findAll();
        personFlux.subscribe(person -> {
            personIds.put(person.getId(), person);
        });
        assertEquals(4, personIds.size());
        assert(personIds.containsKey(1));
        assert(personIds.containsKey(2));
        assert(personIds.containsKey(3));
        assert(personIds.containsKey(4));
        assert(!personIds.containsKey(5));
    }

    @Test
    @DisplayName("findAll - collectList returns all persons")
    void findAll_CollectListReturnsAllPersons() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<List<Person>> personListMono = personFlux.collectList();
        personListMono.subscribe(list -> {
            assertEquals(4, list.size());
            assert(list.contains(new Person(1, "", "")));
            assert(list.contains(new Person(2, "", "")));
            assert(list.contains(new Person(3, "", "")));
            assert(list.contains(new Person(4, "", "")));
            assert(!list.contains(new Person(5, "", "")));
        });
    }

    @Test
    @DisplayName("findAll - find one by id filter")
    void findAll_FindsOneByIdFilter() {
        final Integer id = 3;
        final Flux<Person> personFlux = personRepository.findAll();
        final Mono<Person> personMono = personFlux
                .filter(person -> person.getId().equals(id))
                .next();
        personMono.subscribe(person -> assertEquals(id, person.getId()));
    }

    @Test
    @DisplayName("findAll - finds none by id filter")
    void findAll_FindsNoneByIdFilter() {
        final Integer id = 5;
        final Flux<Person> personFlux = personRepository.findAll();
        final Mono<Person> personMono = personFlux
                .filter(person -> person.getId().equals(id))
                .next();
        personMono.subscribe(person -> assertNull(person));
    }

    @Test
    @DisplayName("findAll - Finding none with single throws exception")
    void findAll_FindingNoneByIdFilterWithSingleThrowsException() {
        final Integer id = 5;
        final Flux<Person> personFlux = personRepository.findAll();
        final Mono<Person> personMono = personFlux
                .filter(person -> person.getId().equals(id))
                .single();
        personMono
                .doOnError(System.out::println)
                .onErrorReturn(Person.builder().id(id).build())
                .subscribe(person -> assertEquals(id, person.getId()));
    }
}
