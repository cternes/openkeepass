package de.slackspace.openkeepass.domain;

public enum CrsAlgorithm {

	Null,
	ArcFourVariant,
	Salsa20;

	public static CrsAlgorithm parseValue(int value) {
		switch (value) {
			case 0: return Null;
			case 1: return ArcFourVariant;
			case 2: return Salsa20;
			default: throw new IllegalArgumentException(String.format("Value %i is not a valid CrsAlgorithm", value));
		}
	}
}
