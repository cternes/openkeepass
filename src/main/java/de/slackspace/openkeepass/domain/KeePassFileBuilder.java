package de.slackspace.openkeepass.domain;

import de.slackspace.openkeepass.domain.zipper.GroupZipper;

/**
 * A builder to create {@link KeePassFile}s.
 * <p>
 * Can be used to create a completely new {@link KeePassFile} or to modify an
 * existing one.
 * <p>
 * To modify an existing one use the {@link GroupZipper}.
 *
 */
public class KeePassFileBuilder {

    Meta meta;
    Group root;
    private GroupBuilder rootBuilder = new GroupBuilder();
    private GroupBuilder topGroupBuilder = new GroupBuilder();
    private KeePassFile keePassFile;

    /**
     * Creates a builder and initializes it with the structure from the given
     * KeePass file.
     *
     * @param keePassFile
     *            the KeePass file which will be used to initialize the builder
     */
    public KeePassFileBuilder(KeePassFile keePassFile) {
        this.keePassFile = keePassFile;
        this.meta = keePassFile.getMeta();

        rootBuilder = new GroupBuilder(keePassFile.getRoot());
    }

    /**
     * Creates a builder with the given databasename.
     *
     * @param databaseName
     *            the name of the database
     */
    public KeePassFileBuilder(String databaseName) {
        meta = new MetaBuilder(databaseName).historyMaxItems(10).build();
    }

    /**
     * Creates a builder with the given meta object.
     *
     * @param meta
     *            the meta object to initialize the builder meta
     */
    public KeePassFileBuilder(Meta meta) {
        this.meta = meta;
    }

    public KeePassFileBuilder withMeta(Meta meta) {
        this.meta = meta;
        return this;
    }

    /**
     * Adds the given groups right under the root node.
     *
     * @param groups
     *            the groups which should be added
     * @return the builder with added groups
     */
    public KeePassFileBuilder addTopGroups(Group... groups) {
        for (Group group : groups) {
            rootBuilder.addGroup(group);
        }

        return this;
    }

    /**
     * Add the given entries right under the root node.
     *
     * @param entries
     *            the entries which should be added
     * @return the builder with added entries
     */
    public KeePassFileBuilder addTopEntries(Entry... entries) {
        for (Entry entry : entries) {
            topGroupBuilder.addEntry(entry);
        }

        return this;
    }

    /**
     * Builds a new KeePass file.
     *
     * @return a new KeePass file
     * @see KeePassFile
     */
    public KeePassFile build() {
        setTopGroupNameIfNotExisting();

        root = rootBuilder.build();

        return new KeePassFile(this);
    }

    /**
     * Returns a {@link GroupZipper} with the structure of the builders
     * {@link KeePassFile} as underlying data.
     * <p>
     * A GroupZipper can be used to easily modify existing KeePass files.
     *
     * @return a new group zipper
     * @deprecated use {@link GroupZipper} directly instead
     * @see GroupZipper
     */
    @Deprecated
    public GroupZipper getZipper() {
        return new GroupZipper(keePassFile);
    }

    private void setTopGroupNameIfNotExisting() {
        if (rootBuilder.groups.isEmpty()) {
            rootBuilder.addGroup(topGroupBuilder.name(meta.getDatabaseName()).build());
        }
    }
}
