package com.simonstuck.vignelli.inspection.identification;

public interface Identification {
    /**
     * A descriptive name of the identification.
     * @return A name of the identification
     */
    String name();

    /**
     * Returns a short description of the identification.
     * @return A short description
     */
    String shortDescription();

    /**
     * Returns a longer description of the identification.
     * @return A detailed description
     */
    String longDescription();
}
