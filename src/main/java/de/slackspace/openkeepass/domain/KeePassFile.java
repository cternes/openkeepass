package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.slackspace.openkeepass.domain.filter.Filter;
import de.slackspace.openkeepass.domain.filter.ListFilter;

/**
 * A KeePassFile represents the structure of a KeePass database. This is the
 * central entry point to read data from the KeePass database.
 */
@Root(strict = false, name = "KeePassFile")
public class KeePassFile implements KeePassFileElement {

    @Element(name = "Meta")
    private Meta meta;

    @Element(name = "Root")
    private Group root;

    KeePassFile() {
    }

    public KeePassFile(KeePassFileContract keePassFileContract) {
        this.meta = keePassFileContract.getMeta();
        this.root = keePassFileContract.getRoot();
    }

    /**
     * Retrieves the meta section of a KeePass database.
     *
     * @return the meta section of the database
     * @see Meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Retrieves the root group of a KeePass database.
     *
     * @return the root group
     * @see Group
     */
    public Group getRoot() {
        return root;
    }

    /**
     * Retrieves all groups at the root level of a KeePass database.
     *
     * @return a list of root level groups
     * @see Group
     */
    public List<Group> getTopGroups() {
        if (root != null && root.getGroups() != null && root.getGroups().size() == 1) {
            return root.getGroups().get(0).getGroups();
        }
        return new ArrayList<Group>();
    }

    /**
     * Retrieves all entries at the root level of a KeePass database.
     *
     * @return a list of root level entries
     * @see Entry
     */
    public List<Entry> getTopEntries() {
        if (root != null && root.getGroups() != null && root.getGroups().size() == 1) {
            return root.getGroups().get(0).getEntries();
        }
        return new ArrayList<Entry>();
    }

    /**
     * Retrieves a single entry with an exactly matching title.
     * <p>
     * If there are multiple entries with the same title, the first one found
     * will be returned.
     *
     * @param title
     *            the title which should be searched
     * @return an entry with a matching title
     * @see Entry
     */
    public Entry getEntryByTitle(String title) {
        List<Entry> entries = getEntriesByTitle(title, true);

        if (!entries.isEmpty()) {
            return entries.get(0);
        }

        return null;
    }

    /**
     * Retrieves a list of entries with matching titles.
     * <p>
     * If the <tt>matchExactly</tt> flag is true, only entries which have an
     * exactly matching title will be returned, otherwise all entries which
     * contain the given title will be returned.
     *
     * @param title
     *            the title which should be searched
     * @param matchExactly
     *            if true only entries which have an exactly matching title will
     *            be returned
     * @return a list of entries with matching titles
     * @see Entry
     */
    public List<Entry> getEntriesByTitle(final String title, final boolean matchExactly) {
        List<Entry> allEntries = new ArrayList<Entry>();

        if (root != null) {
            getEntries(root, allEntries);
        }

        return ListFilter.filter(allEntries, new Filter<Entry>() {

            @Override
            public boolean matches(Entry item) {
                if (matchExactly) {
                    if (item.getTitle() != null && item.getTitle().equalsIgnoreCase(title)) {
                        return true;
                    }
                } else {
                    if (item.getTitle() != null && item.getTitle().toLowerCase().contains(title.toLowerCase())) {
                        return true;
                    }
                }

                return false;
            }

        });
    }

    /**
     * Retrieves a list of group with matching names.
     * <p>
     * If the <tt>matchExactly</tt> flag is true, only groups which have an
     * exactly matching name will be returned, otherwise all groups which
     * contain the given name will be returned.
     *
     * @param name
     *            the name which should be searched
     * @param matchExactly
     *            if true only groups which have an exactly matching name will
     *            be returned
     * @return a list of entries with matching names
     * @see Group
     */
    public List<Group> getGroupsByName(final String name, final boolean matchExactly) {
        List<Group> allGroups = new ArrayList<Group>();

        if (root != null) {
            getGroups(root, allGroups);
        }

        return ListFilter.filter(allGroups, new Filter<Group>() {

            @Override
            public boolean matches(Group item) {
                if (matchExactly) {
                    if (item.getName() != null && item.getName().equalsIgnoreCase(name)) {
                        return true;
                    }
                } else {
                    if (item.getName() != null && item.getName().toLowerCase().contains(name.toLowerCase())) {
                        return true;
                    }
                }

                return false;
            }

        });
    }

    /**
     * Retrieves a list of all entries in the KeePass database.
     *
     * @return a list of all entries
     * @see Entry
     */
    public List<Entry> getEntries() {
        List<Entry> allEntries = new ArrayList<Entry>();

        if (root != null) {
            getEntries(root, allEntries);
        }

        return allEntries;
    }

    /**
     * Retrieves a list of all groups in the KeePass database.
     *
     * @return a list of all groups
     * @see Group
     */
    public List<Group> getGroups() {
        List<Group> allGroups = new ArrayList<Group>();

        if (root != null) {
            getGroups(root, allGroups);
        }

        return allGroups;
    }

    /**
     * Retrieves a single group with an exactly matching name.
     * <p>
     * If there are multiple groups with the same name, the first one found will
     * be returned.
     *
     * @param name
     *            the name which should be searched
     * @return a group with a matching name
     * @see Group
     */
    public Group getGroupByName(String name) {
        List<Group> groups = getGroupsByName(name, true);

        if (!groups.isEmpty()) {
            return groups.get(0);
        }

        return null;
    }

    private void getEntries(Group parentGroup, List<Entry> entries) {
        List<Group> groups = parentGroup.getGroups();
        entries.addAll(parentGroup.getEntries());

        if (!groups.isEmpty()) {
            for (Group group : groups) {
                getEntries(group, entries);
            }
        }

        return;
    }

    private void getGroups(Group parentGroup, List<Group> groups) {
        List<Group> parentGroups = parentGroup.getGroups();
        groups.addAll(parentGroups);

        if (!parentGroups.isEmpty()) {
            for (Group group : parentGroups) {
                getGroups(group, groups);
            }
        }

        return;
    }

    /**
     * Retrieves an entry based on its UUID.
     *
     * @param UUID
     *            the uuid which should be searched
     * @return the found entry or null
     */
    public Entry getEntryByUUID(final UUID UUID) {
        List<Entry> allEntries = getEntries();

        List<Entry> entries = ListFilter.filter(allEntries, new Filter<Entry>() {

            @Override
            public boolean matches(Entry item) {

                if (item.getUuid() != null && item.getUuid().compareTo(UUID) == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (entries.size() == 1) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    /**
     * Retrieves a group based on its UUID.
     *
     * @param UUID
     *            the uuid which should be searched
     * @return the found group or null
     */
    public Group getGroupByUUID(final UUID UUID) {
        List<Group> allGroups = getGroups();

        List<Group> groups = ListFilter.filter(allGroups, new Filter<Group>() {

            @Override
            public boolean matches(Group item) {

                if (item.getUuid() != null && item.getUuid().compareTo(UUID) == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (groups.size() == 1) {
            return groups.get(0);
        } else {
            return null;
        }
    }
}
