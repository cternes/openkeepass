package de.slackspace.openkeepass.domain;

public enum CrsAlgorithm {

    Null, ArcFourVariant, Salsa20;

    public static CrsAlgorithm parseValue(int value) {
        switch (value) {
        case 0:
            return Null;
        case 1:
            return ArcFourVariant;
        case 2:
            return Salsa20;
        default:
            throw new IllegalArgumentException(String.format("Value %d is not a valid CrsAlgorithm", value));
        }
    }

    public static int getIntValue(CrsAlgorithm algorithm) {
        switch (algorithm) {
        case Null:
            return 0;
        case ArcFourVariant:
            return 1;
        case Salsa20:
            return 2;
        default:
            throw new IllegalArgumentException(String.format("Value %s is not a valid CrsAlgorithm", algorithm));
        }
    }
}
