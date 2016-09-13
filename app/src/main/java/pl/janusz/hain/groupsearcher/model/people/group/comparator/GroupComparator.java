package pl.janusz.hain.groupsearcher.model.people.group.comparator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import pl.janusz.hain.groupsearcher.model.people.group.Group;
import pl.janusz.hain.groupsearcher.model.people.person.Person;
import pl.janusz.hain.groupsearcher.model.people.person.comparator.PersonComparator;

public class GroupComparator implements Comparator<Group> {
    @Override
    public int compare(Group lGroup, Group rGroup) {

        if (compareGroupSizes(lGroup, rGroup) == 0) {
            if (compareOutOfGroupKnownPersonsSizes(lGroup, rGroup) == 0) {
                return compareNamesAlphabetically(lGroup, rGroup);
            } else {
                return compareOutOfGroupKnownPersonsSizes(lGroup, rGroup);
            }
        } else {
            return compareGroupSizes(lGroup, rGroup);
        }

    }

    private int compareGroupSizes(Group lGroup, Group rGroup) {
        if (lGroup.getSize() == rGroup.getSize()) {
            return 0;
        } else if (lGroup.getSize() > rGroup.getSize()) {
            return -1;
        } else {
            return 1;
        }
    }

    private int compareOutOfGroupKnownPersonsSizes(Group lGroup, Group rGroup) {
        if (lGroup.getSizePeopleOutOfGroup() == rGroup.getSizePeopleOutOfGroup()) {
            return 0;
        } else if (lGroup.getSizePeopleOutOfGroup() > rGroup.getSizePeopleOutOfGroup()) {
            return -1;
        } else {
            return 1;
        }
    }

    private int compareNamesAlphabetically(Group lGroup, Group rGroup) {
        PersonComparator personComparator = new PersonComparator();
        TreeSet<Person> lTreeSet = lGroup.getPeopleInGroup();
        TreeSet<Person> rTreeSet = rGroup.getPeopleInGroup();
        Iterator<Person> lIterator = lTreeSet.iterator();
        Iterator<Person> rIterator = rTreeSet.iterator();
        while (lIterator.hasNext() && rIterator.hasNext()) {
            Person lPerson = lIterator.next();
            Person rPerson = rIterator.next();
            int compared = personComparator.compare(lPerson, rPerson);
            if (compared != 0) {
                return compared;
            }
        }
        return 0;
    }
}
