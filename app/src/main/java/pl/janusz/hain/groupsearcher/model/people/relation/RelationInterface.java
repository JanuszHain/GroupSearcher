package pl.janusz.hain.groupsearcher.model.people.relation;

import pl.janusz.hain.groupsearcher.model.people.person.Person;

public interface RelationInterface {
    boolean containsPerson(Person person);
    boolean containsPersons(Person lPerson, Person rPerson);
    Person getAnotherPerson(Person person);
}
