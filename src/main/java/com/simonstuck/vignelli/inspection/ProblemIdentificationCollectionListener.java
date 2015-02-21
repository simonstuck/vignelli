package com.simonstuck.vignelli.inspection;

import com.simonstuck.vignelli.inspection.identification.Identification;
import com.simonstuck.vignelli.inspection.identification.IdentificationCollection;

import java.util.function.Consumer;

public interface ProblemIdentificationCollectionListener extends Consumer<IdentificationCollection<? extends Identification>> {
}
