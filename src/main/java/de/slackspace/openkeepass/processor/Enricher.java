package de.slackspace.openkeepass.processor;

import de.slackspace.openkeepass.domain.KeePassFile;

public class Enricher {

    private KeePassFile keepassFile;

    public Enricher(KeePassFile keepassFile) {
        this.keepassFile = keepassFile;
    }

    public Enricher enrichIcons() {
        keepassFile = new IconEnricher().enrichNodesWithIconData(keepassFile);
        return this;
    }

    public Enricher enrichAttachments() {
        keepassFile = new BinaryEnricher().enrichNodesWithBinaryData(keepassFile);
        return this;
    }

    public Enricher enrichReferences() {
        keepassFile = new ReferencesEnricher().enrichNodesWithReferences(keepassFile);
        return this;
    }

    public KeePassFile process() {
        return keepassFile;
    }
}
