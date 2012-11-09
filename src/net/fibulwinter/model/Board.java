package net.fibulwinter.model;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;

public class Board {
    private Collection<Checker> checkers = newArrayList();

    public Collection<Checker> getCheckers() {
        return Collections.unmodifiableCollection(checkers);
    }

    public void add(Checker checker){
        checkers.add(checker);
    }
}
