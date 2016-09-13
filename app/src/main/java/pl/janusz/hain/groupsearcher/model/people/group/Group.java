package pl.janusz.hain.groupsearcher.model.people.group;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import pl.janusz.hain.groupsearcher.model.people.person.Person;
import pl.janusz.hain.groupsearcher.model.people.person.comparator.PersonComparator;
import pl.janusz.hain.groupsearcher.model.people.relation.Relation;

public final class Group implements GroupInterface {
    private TreeSet<Person> peopleInGroup;
    private TreeSet<Person> knownPeopleOutsideGroup;
    private Queue<Person> personsToBeChecked;
    private boolean generateMore = true;

    public Group(Relation relation) {
        initializeCollections();
        setPeopleToGroupFromRelation(relation);
        setInitialPersosnsToBeChecked();
    }

    private void initializeCollections() {
        PersonComparator personComparator = new PersonComparator();
        peopleInGroup = new TreeSet<>(personComparator);
        knownPeopleOutsideGroup = new TreeSet<>(personComparator);
        personsToBeChecked = new LinkedList<>();
    }

    private void setPeopleToGroupFromRelation(Relation relation) {
        Person personA = relation.getPersonA();
        Person personB = relation.getPersonB();
        peopleInGroup.add(personA);
        peopleInGroup.add(personB);
    }

    private void setInitialPersosnsToBeChecked() {
        for (Person groupMember : peopleInGroup) {
            addMembersKnownPersonsToQueueToBeChecked(groupMember);
        }
    }

    /*
    @Override
    public void generateGroup() {
        while (!personsToBeChecked.isEmpty()) {
            Person personBeingChecked = personsToBeChecked.remove();
            if (personKnownToAllGroupMembers(personBeingChecked)) {
                addPersonToGroup(personBeingChecked);
                addMembersKnownPersonsToQueueToBeChecked(personBeingChecked);
            } else {
                addPersonToKnownPeopleOutsideGroup(personBeingChecked);
            }
        }
    }
    */

    @Override
    public void generateGroup() {
        if(!personsToBeChecked.isEmpty()) {
            Person personBeingChecked = personsToBeChecked.remove();
            if (personKnownToAllGroupMembers(personBeingChecked)) {
                addPersonToGroup(personBeingChecked);
                addMembersKnownPersonsToQueueToBeChecked(personBeingChecked);
            } else {
                addPersonToKnownPeopleOutsideGroup(personBeingChecked);
            }
        }
        if(personsToBeChecked.isEmpty()){
            generateMore = false;
        }
    }

    private void addPersonToGroup(Person person) {
        peopleInGroup.add(person);
    }

    private void addPersonToKnownPeopleOutsideGroup(Person person) {
        knownPeopleOutsideGroup.add(person);
    }

    private void addMembersKnownPersonsToQueueToBeChecked(Person groupMember) {
        for (Relation relation : groupMember.getRelations()) {
            Person personFromRelation = relation.getAnotherPerson(groupMember);

            if (!personAlreadyInGroup(personFromRelation) && !personInKnownPersonsOutsideGroup(personFromRelation)) {
                personsToBeChecked.add(personFromRelation);
            }
        }
    }

    private boolean personAlreadyInGroup(Person person) {
        return peopleInGroup.contains(person);
    }

    private boolean personInKnownPersonsOutsideGroup(Person person) {
        return knownPeopleOutsideGroup.contains(person);
    }

    private boolean personKnownToAllGroupMembers(Person person) {
        for (Person groupMember : peopleInGroup) {
            Relation tmpRelation = new Relation(groupMember, person);
            HashSet<Relation> groupMemberRelations = groupMember.getRelations();
            if (!groupMemberRelations.contains(tmpRelation)) {
                return false;
            }
        }
        return true;
    }

    public int getSize() {
        return peopleInGroup.size();
    }

    public int getSizePeopleOutOfGroup() {
        return knownPeopleOutsideGroup.size();
    }

    public TreeSet<Person> getPeopleInGroup() {
        return peopleInGroup;
    }

    @Override
    public String toString() {
        String groupString = peopleInGroup.size() + "\n";
        for(Person person : peopleInGroup){
            groupString+=person+" ";
        }
        return groupString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return groupContainsAllPersons(group.peopleInGroup);
    }

    private boolean groupContainsAllPersons(TreeSet<Person> persons) {
        if (!treeSetHasSameSize(persons)) {
            return false;
        }

        for (Person person : persons) {
            if (!peopleInGroup.contains(person)) {
                return false;
            }
        }
        return true;
    }

    private boolean treeSetHasSameSize(TreeSet<Person> persons) {
        return persons.size() == peopleInGroup.size();
    }

    @Override
    public int hashCode() {
        return peopleInGroup.hashCode();
    }

    public boolean generateMore() {
        return generateMore;
    }
}
