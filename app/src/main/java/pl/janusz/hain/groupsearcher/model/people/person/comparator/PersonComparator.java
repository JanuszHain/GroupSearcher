package pl.janusz.hain.groupsearcher.model.people.person.comparator;

import java.util.Comparator;

import pl.janusz.hain.groupsearcher.model.people.person.Person;

public class PersonComparator implements Comparator<Person> {
    @Override
    public int compare(Person lPerson, Person rPerson) {
        return lPerson.getName().compareTo(rPerson.getName());
    }
}
