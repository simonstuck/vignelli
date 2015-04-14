package com.simonstuck.vignelli.inspection;

import com.intellij.util.Consumer;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import java.util.Collection;

public interface ProblemIdentificationCollectionListener extends Consumer<Collection<ProblemIdentification>> {
}
