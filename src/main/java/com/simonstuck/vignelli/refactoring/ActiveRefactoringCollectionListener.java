package com.simonstuck.vignelli.refactoring;

import java.util.Collection;
import java.util.function.Consumer;

public interface ActiveRefactoringCollectionListener extends Consumer<Collection<Refactoring>> {
}
