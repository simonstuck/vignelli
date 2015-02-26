package com.simonstuck.vignelli.providers;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionOfMocksProvider {
    public static <T> Collection<T> collectionOfSize(int numberOfIdentifications, Class<T> clazz) {
        Collection<T> problemIdentifications = new ArrayList<T>();

        for (int i = 0; i < numberOfIdentifications; i++) {
            problemIdentifications.add(mock(clazz));
        }

        return problemIdentifications;
    }
}
