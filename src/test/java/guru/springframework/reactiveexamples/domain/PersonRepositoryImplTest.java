package guru.springframework.reactiveexamples.domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PersonRepositoryImplTest {

    @Test
    @DisplayName("getById gets existing person by id")
    void test_getById_returns_existing_person() {
        PersonRepository repo = new PersonRepositoryImpl();
        Person expected = new Person(1, "Michael", "Weston");
        Mono<Person> actual = repo.getById(1);
        assertEquals(expected, actual.block());
    }

    @Test
    @DisplayName("getById returns null for non-existing person")
    void testGetById() {
        PersonRepository repo = new PersonRepositoryImpl();
        Mono<Person> actual = repo.getById(-1);
        assertNull(actual);
    }
}
