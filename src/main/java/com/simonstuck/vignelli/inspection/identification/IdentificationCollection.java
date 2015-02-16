package com.simonstuck.vignelli.inspection.identification;

import com.simonstuck.vignelli.utils.Filterable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class IdentificationCollection<T extends Identification> implements Iterable<T>, Filterable<IdentificationCollection<T>, T> {

    protected Set<T> identifications = new HashSet<T>();

    public IdentificationCollection(Set<T> identifications) {
        this.identifications.addAll(identifications);
    }

    public IdentificationCollection() {
        this(new HashSet<T>());
    }

    public int size() {
        return identifications.size();
    }

    public boolean contains(T element) {
        return identifications.contains(element);
    }

    public void add(T identification) {
        identifications.add(identification);
    }

    public void addAll(Collection<? extends T> os) {
        identifications.addAll(os);
    }

    public void remove(T identification) {
        identifications.remove(identification);
    }

    @Override
    public IdentificationCollection<T> filter(Predicate<T> predicate) {
        @SuppressWarnings("unchecked") IdentificationCollection<T> result = filter(predicate, getClass());
        return result;
    }

    public IdentificationCollection<T> filterIdentifications(final IdentificationCollection<T> toRemove) {
        @SuppressWarnings("unchecked") IdentificationCollection<T> result = filterIdentifications(toRemove, getClass());
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return identifications.iterator();
    }

    protected <R extends IdentificationCollection<T>> R filter(Predicate<T> predicate, Class<R> clazz) {
        try {
            R filtered = clazz.newInstance();
            for (T identification : identifications) {
                if (predicate.test(identification)) {
                    filtered.add(identification);
                }
            }
            return filtered;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected <R extends IdentificationCollection<T>> R filterIdentifications(final IdentificationCollection<T> toRemove, Class<R> clazz) {
        return filter(new Predicate<T>() {
            @Override
            public boolean test(T identification) {
                return !toRemove.contains(identification);
            }
        }, clazz);
    }
}
