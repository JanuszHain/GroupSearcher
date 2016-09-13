package pl.janusz.hain.groupsearcher.model.people.relation;

import pl.janusz.hain.groupsearcher.model.people.person.Person;

public final class Relation implements RelationInterface {
    private Person personA;
    private Person personB;

    public Relation(Person lPerson, Person rPerson) {
        this.personA = lPerson;
        this.personB = rPerson;
    }

    public Person getPersonA() {
        return personA;
    }

    public Person getPersonB() {
        return personB;
    }

    @Override
    public boolean containsPerson(Person person) {
        if (personA.equals(person) || personB.equals(person)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsPersons(Person lPerson, Person pPerson) {
        if (personA.equals(lPerson) || personB.equals(lPerson)) {
            if (personA.equals(pPerson) || personB.equals(pPerson)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Person getAnotherPerson(Person person) {
        if (person.equals(personA)) {
            return personB;
        } else {
            return personA;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;

        if (personA.equals(relation.personA) || personA.equals(relation.personB)) {
            if (personB.equals(relation.personA) || personB.equals(relation.personB)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    @Override
    public int hashCode() {
        int result = personA.hashCode();
        result = result + personB.hashCode();
        return result;
    }
}
