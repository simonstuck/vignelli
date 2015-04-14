package com.simonstuck.vignelli.refactoring;

import com.intellij.util.Consumer;

import java.util.Collection;

public interface ActiveRefactoringCollectionListener extends Consumer<Collection<Refactoring>> {
}
