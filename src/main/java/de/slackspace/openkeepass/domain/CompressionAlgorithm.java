package de.slackspace.openkeepass.domain;

public enum CompressionAlgorithm {

    None, Gzip;

    public static CompressionAlgorithm parseValue(int value) {
        switch (value) {
        case 0:
            return None;
        case 1:
            return Gzip;
        default:
            throw new IllegalArgumentException(String.format("Value %d is not a valid CompressionAlgorithm", value));
        }
    }

    public static int getIntValue(CompressionAlgorithm algorithm) {
        switch (algorithm) {
        case None:
            return 0;
        case Gzip:
            return 1;
        default:
            throw new IllegalArgumentException(String.format("Value %s is not a valid CompressionAlgorithm", algorithm));
        }
    }
}
