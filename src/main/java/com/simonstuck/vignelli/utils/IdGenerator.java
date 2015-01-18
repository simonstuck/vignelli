package com.simonstuck.vignelli.utils;

public interface IdGenerator<T extends Comparable<T>> {
    /**
     * Generates a new unique ID in the current namespace.
     * @return a new id of the given type.
     */
    public T generateId();
}
