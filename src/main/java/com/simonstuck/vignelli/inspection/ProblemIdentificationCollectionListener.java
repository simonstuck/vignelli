package com.simonstuck.vignelli.inspection;

import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;

import java.util.Collection;
import java.util.function.Consumer;

public interface ProblemIdentificationCollectionListener extends Consumer<Collection<ProblemIdentification>> {
}
