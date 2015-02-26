package com.simonstuck.vignelli.inspection.identification;

import com.simonstuck.vignelli.utils.Filterable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class IdentificationCollection<T extends Identification> implements Iterable<T>, Filterable<IdentificationCollection<T>, T> {

    private final Set<T> identifications = new HashSet<T>();

    private IdentificationCollection(Set<T> identifications) {
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

    @Override
    public IdentificationCollection<T> filter(Predicate<T> predicate) {
        @SuppressWarnings("unchecked") IdentificationCollection<T> result = filterWithReturnType(predicate, getClass());
        return result;
    }

    public IdentificationCollection<T> filterIdentifications(final IdentificationCollection<T> toRemove) {
        @SuppressWarnings("unchecked") IdentificationCollection<T> result = filterIdentificationsWithReturnType(toRemove, getClass());
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return identifications.iterator();
    }

    <R extends IdentificationCollection<T>> R filterWithReturnType(Predicate<T> predicate, Class<R> clazz) {
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


    <R extends IdentificationCollection<T>> R filterIdentificationsWithReturnType(
            final IdentificationCollection<T> toRemove, Class<R> clazz) {
        return filterWithReturnType(new Predicate<T>() {
            @Override
            public boolean test(T identification) {
                return !toRemove.contains(identification);
            }
        }, clazz);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        IdentificationCollection that = (IdentificationCollection) other;
        return identifications.equals(that.identifications);
    }

    @Override
    public int hashCode() {
        return identifications.hashCode();
    }
}
