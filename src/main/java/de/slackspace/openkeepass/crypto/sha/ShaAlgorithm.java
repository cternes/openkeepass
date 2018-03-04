package de.slackspace.openkeepass.crypto.sha;

public enum ShaAlgorithm {

    SHA_256("SHA-256"),
    SHA_512("SHA-512");
    
    private String name;

    ShaAlgorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
