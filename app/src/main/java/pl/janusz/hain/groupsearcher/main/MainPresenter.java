package pl.janusz.hain.groupsearcher.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

import pl.janusz.hain.groupsearcher.exception.WrongInputFormatException;
import pl.janusz.hain.groupsearcher.model.people.group.Group;
import pl.janusz.hain.groupsearcher.model.people.group.comparator.GroupComparator;
import pl.janusz.hain.groupsearcher.model.people.person.Person;
import pl.janusz.hain.groupsearcher.model.people.relation.Relation;
import pl.janusz.hain.groupsearcher.util.MyScheduler;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*

TODO Poprawki/optymalizacje:
- cancelowanie subskrypcji po wyjściu z aktywności
- przechowywać tablicę z referencjami obiektów Person tylko u pierwszej osoby w relacji, ale w tym przypadku zysk byłby niewielki.


 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mMainView;
    private ArrayList<Person> persons;
    private ArrayList<Relation> relations;
    private ArrayList<Group> groups;
    private HashSet<Group> groupsToBeGeneratedMore;
    private Observable<Group> mergedGroupGeneration;

    public MainPresenter(MainContract.View mainView) {
        mMainView = mainView;
    }

    @Override
    public void start() {
    }

    @Override
    public void findBiggestSocialGroup(String inputString) {
        observableFindBiggestSocialGroup(inputString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(this::onGroupsGeneratedAndSorted)
                .subscribe(b -> {
                        },
                        throwable -> {
                            if (throwable instanceof Exception) {
                                onGroupCreationError((Exception) throwable);
                            }
                        });
    }

    private Observable<Boolean> observableFindBiggestSocialGroup(String inputString) {
        return Observable.create(subscriber -> {
            if (parseInputString(inputString)) {
                createAndGenerateGroups()
                        .doOnCompleted(() -> {
                            subscriber.onNext(true);
                            subscriber.onCompleted();
                        })
                        .subscribe();
            } else {
                subscriber.onError(new WrongInputFormatException());
                subscriber.onNext(false);
                subscriber.onCompleted();
            }
        });
    }

    private boolean parseInputString(String inputString) {
        Scanner scanner = new Scanner(inputString);
        if (scanner.hasNext()) {
            if (scanner.hasNextInt()) {
                int n = scanner.nextInt(); //liczba osób
                persons = new ArrayList<>(n);
            } else {
                scanner.close();
                return false;
            }
            if (scanner.hasNextInt()) {
                int m = scanner.nextInt(); //liczba relacji
                relations = new ArrayList<>(m);
            } else {
                scanner.close();
                return false;
            }

            skipCurrentLineAndGetNextLine(scanner);

            if (scanner.hasNextLine()) {
                while (scanner.hasNextLine()) {
                    String stringLine = scanner.nextLine();
                    if (!addPersonsAndRelations(stringLine)) {
                        scanner.close();
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        scanner.close();
        return false;
    }

    private void skipCurrentLineAndGetNextLine(Scanner scanner) {
        scanner.nextLine();
    }

    private boolean addPersonsAndRelations(String stringLine) {
        StringTokenizer stringTokenizer = new StringTokenizer(stringLine);
        Person lPerson;
        Person rPerson;

        if (stringTokenizer.hasMoreElements()) {
            String name = stringTokenizer.nextElement().toString();
            lPerson = new Person(name);
            lPerson = addPersonToArrayList(lPerson);

        } else {
            return false;
        }
        if (stringTokenizer.hasMoreElements()) {
            String name = stringTokenizer.nextElement().toString();
            rPerson = new Person(name);
            rPerson = addPersonToArrayList(rPerson);
        } else {
            return false;
        }

        if (lPerson.equals(rPerson)) {
            return false;
        }

        Relation relation = new Relation(lPerson, rPerson);
        replaceRelation(relation);
        addRelationToPersonInArrayList(lPerson, relation);
        addRelationToPersonInArrayList(rPerson, relation);
        return true;
    }

    private Person addPersonToArrayList(Person person) {
        if (!persons.contains(person)) {
            persons.add(person);
            return person;
        } else {
            return getPersonFromArrayList(person);
        }
    }

    private void replaceRelation(Relation relation) {
        if (relations.contains(relation)) {
            relations.remove(relation);
        }
        relations.add(relation);
    }

    private void addRelationToPersonInArrayList(Person person, Relation relation) {
        person.setRelation(relation);
    }

    private Person getPersonFromArrayList(Person person) {
        int personPosition = persons.indexOf(person);
        return persons.get(personPosition);
    }

    private Observable createAndGenerateGroups() {
        return Observable.create(subscriber -> {
            groups = new ArrayList<>();
            HashSet<Group> groupsToBeAdded = new HashSet<>(1);
            groupsToBeGeneratedMore = new HashSet<>();
            Observable<Group> mergedGroupCreation = mergedObservablesCreateGroup();
            mergedGroupCreation
                    .doOnCompleted(() -> {
                        groups.addAll(groupsToBeAdded);
                        Observable generateGroups = generateGroups();
                        generateGroups
                                .doOnCompleted(subscriber::onCompleted)
                                .subscribe();
                    })
                    .forEach(group ->
                            {
                                if (group.generateMore()) {
                                    groupsToBeGeneratedMore.add(group);
                                } else {
                                    groupsToBeAdded.add(group);
                                }
                            }
                    );
        });
    }

    private Observable<Group> mergedObservablesCreateGroup() {
        ArrayList<Observable<Group>> observables = new ArrayList<>(relations.size());
        for (Relation relation : relations) {
            Observable<Group> observable = observableCreateGroup(relation);
            observable = observable.subscribeOn(MyScheduler.getScheduler());
            observables.add(observable);
        }
        return Observable.merge(observables);
    }

    private Observable<Group> observableCreateGroup(final Relation relation) {
        return Observable.create(subscriber -> {
            subscriber.onNext(createGroup(relation));
            subscriber.onCompleted();
        });
    }

    private Group createGroup(Relation relation) {
        Group group = new Group(relation);
        group.generateGroup();
        return group;
    }

    private Observable generateGroups() {
        return Observable.create(this::createMergedGroupGeneration);
    }

    private void createMergedGroupGeneration(Subscriber subscriber) {
        HashSet<Group> groupsToBeAdded = new HashSet<>(1);
        mergedGroupGeneration = mergedObservablesGenerateGroups();
        groupsToBeGeneratedMore.clear();
        mergedGroupGeneration
                .doOnNext(group -> {
                    if (!group.generateMore()) {
                        groupsToBeAdded.add(group);
                    } else {
                        groupsToBeGeneratedMore.add(group);
                    }
                })
                .doOnCompleted(() -> {
                    groups.addAll(groupsToBeAdded);
                    if (!groupsToBeGeneratedMore.isEmpty()) {
                        createMergedGroupGeneration(subscriber);
                    } else {
                        sortGroups();
                        subscriber.onCompleted();
                    }
                })
                .subscribe();
    }

    private Observable<Group> mergedObservablesGenerateGroups() {
        HashSet<Group> tmpGroupsToBeGeneratedMore = new HashSet<>(groupsToBeGeneratedMore.size());
        tmpGroupsToBeGeneratedMore.addAll(groupsToBeGeneratedMore);
        ArrayList<Observable<Group>> observables = new ArrayList<>(groupsToBeGeneratedMore.size());

        for (Group group : tmpGroupsToBeGeneratedMore) {
            Observable<Group> observable = observableGenerateGroup(group);
            observable = observable.subscribeOn(MyScheduler.getScheduler());
            observables.add(observable);
        }

        return Observable.merge(observables);
    }

    private Observable<Group> observableGenerateGroup(Group group) {
        return Observable.create(subscriber -> {
            group.generateGroup();
            subscriber.onNext(group);
            subscriber.onCompleted();
        });
    }

    private void sortGroups() {
        Collections.sort(groups, new GroupComparator());
    }

    private void onGroupsGeneratedAndSorted() {
        showBiggestSocialGroup();
    }

    private void onGroupCreationError(Exception e) {
        if (e instanceof WrongInputFormatException) {
            sendToast("Niepoprawna forma wprowadzonych danych");
        }
    }

    @Override
    public void showBiggestSocialGroup() {
        if (!groups.isEmpty()) {
            Group firstGroup = groups.get(0);
            mMainView.showResults("" + firstGroup);
        } else {
            sendToast("Brak grup");
        }
    }

    private void sendToast(String message) {
        mMainView.makeToast(message);
    }
}