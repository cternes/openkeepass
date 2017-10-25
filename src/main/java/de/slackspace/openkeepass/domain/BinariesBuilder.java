package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder to create {@link Binaries} objects.
 *
 */
public class BinariesBuilder implements BinariesContract {

    List<Binary> binaries = new ArrayList<Binary>();

    public BinariesBuilder() {
        // default no-args constructor
    }

    public BinariesBuilder(Binaries binaries) {
        this.binaries = binaries.getBinaries();
    }

    public BinariesBuilder binaries(List<Binary> binaries) {
        this.binaries = binaries;
        return this;
    }

    public BinariesBuilder addBinary(Binary binary) {
        binaries.add(binary);
        return this;
    }

    /**
     * Builds a new binary item list with the values from the builder.
     *
     * @return a new Binaries object
     */
    public Binaries build() {
        return new Binaries(this);
    }

    @Override
    public List<Binary> getBinaries() {
        return binaries;
    }
}
