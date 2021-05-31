package guru.springframework.reactiveexamples.domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertNull(actualPerson);
    }
}
