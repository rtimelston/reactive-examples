package guru.springframework.reactiveexamples.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class PersonRepositoryImpl implements PersonRepository {

    private final Person michael = new Person(1, "Michael", "Weston");
    private final Person fiona = new Person(2, "Fiona", "Glenanne");
    private final Person sam = new Person(3, "Sam", "Axe");
    private final Person jesse = new Person(4, "Jesse", "Porter");

    private final Map<Integer, Person> persons = Map.of(
            michael.getId(), michael,
            fiona.getId(), fiona,
            sam.getId(), sam,
            jesse.getId(), jesse
    );

    @Override
    public Mono<Person> getById(Integer id) {
        final Person person = persons.get(id);
        return person != null ? Mono.just(person) : null;
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(michael, fiona, sam, jesse);
    }

}
