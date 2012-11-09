package net.fibulwinter.model;

import net.fibulwinter.view.IModel;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;

public class Board implements IModel {
    private Collection<Checker> checkers = newArrayList();

    public Collection<Checker> getCheckers() {
        return Collections.unmodifiableCollection(checkers);
    }

    public void add(Checker checker){
        checkers.add(checker);
    }

    @Override
    public void simulate() {
        for(Checker checker:checkers){
            checker.move(1);
        }
    }
}
