package pl.janusz.hain.groupsearcher.model.people.person;

import java.util.HashSet;

import pl.janusz.hain.groupsearcher.model.people.relation.Relation;

public final class Person {

    private String name;
    private  HashSet<Relation> relations;
    private boolean settersLocked = false;

    public Person(String name) {
        this.name = name;
        relations = new HashSet<>(1);
    }

    public String getName() {
        return name;
    }

    public void setRelation(Relation relation) {
        if (!settersLocked) {
            relations.add(relation);
        }
    }

    public HashSet<Relation> getRelations() {
        return relations;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return getName().equals(person.getName());

    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
