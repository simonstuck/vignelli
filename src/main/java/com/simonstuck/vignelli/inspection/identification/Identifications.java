package com.simonstuck.vignelli.inspection.identification;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Identifications<T extends Identification> implements Iterable<T> {

    private Set<T> identifications = new HashSet<T>();

    public Identifications(Set<T> identifications) {
        this.identifications.addAll(identifications);
    }

    public Identifications() {
        this(new HashSet<T>());
    }

    public int size() {
        return identifications.size();
    }

    public void add(T identification) {
        identifications.add(identification);
    }

    @Override
    public Iterator<T> iterator() {
        return identifications.iterator();
    }
}
