package com.simonstuck.vignelli.utils;

import java.util.function.Predicate;

public interface Filterable<R, T> {
    R filter(Predicate<T> predicate);
}
