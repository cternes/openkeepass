package de.slackspace.openkeepass.domain.zipper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.EntryBuilder;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.GroupBuilder;
import de.slackspace.openkeepass.domain.KeePassFile;
import de.slackspace.openkeepass.domain.KeePassFileBuilder;
import de.slackspace.openkeepass.domain.Meta;

/**
 * A zipper is used to navigate through a tree structure of {@link Group}
 * objects.
 * <p>
 * It is kind of an iterator with a pointer to the current element. The current
 * element can be found with {@link #getNode()} method.
 * <p>
 * Navigation through the tree is possible with the methods {@link #down()}
 * {@link #up()} {@link #left()} {@link #right()}.
 * <p>
 * The tree can also be modified by using the {@link #replace(Group)} method to
 * replace a node with another one.
 *
 */
public class GroupZipper {

    private Meta meta;
    private int index = 0;
    private Group node;
    private GroupZipper parent;

    /**
     * Create a zipper with the tree structure of the given KeePass file.
     *
     * @param keePassFile
     *            the underlying data structure
     */
    public GroupZipper(KeePassFile keePassFile) {
        this.meta = keePassFile.getMeta();
        this.node = keePassFile.getRoot().getGroups().get(0);
    }

    private GroupZipper(GroupZipper parent, Group group, int index) {
        this.parent = parent;
        this.node = group;
        this.index = index;
    }

    /**
     * Returns true if it is possible to navigate down.
     *
     * @return true, if it is possible to navigate down
     */
    public boolean canDown() {
        if (node.getGroups() == null || node.getGroups().isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Navigates down the tree to the first child node of the current node.
     * <p>
     * If the current node has no childs an exception will be thrown.
     *
     * @return a new groupzipper which points to the first child node of the
     *         current node
     * @throws NoSuchElementException
     *             if the current node has no child nodes
     */
    public GroupZipper down() {
        if (!canDown()) {
            throw new NoSuchElementException("Could not move down because this group does not have any children");
        }

        parent = new GroupZipper(parent, node, index);
        index = 0;
        node = node.getGroups().get(0);

        return this;
    }

    /**
     * Returns true if it is possible to navigate up.
     *
     * @return true, if it is possible to navigate up
     */
    public boolean canUp() {
        if (parent == null) {
            return false;
        }

        return true;
    }

    /**
     * Navigates up the tree to the parent node of the current node.
     * <p>
     * If the current node has no parent an exception will be thrown.
     *
     * @return a new groupzipper which points to the parent node of the current
     *         node
     * @throws NoSuchElementException
     *             if the current node has no parent node
     */
    public GroupZipper up() {
        if (!canUp()) {
            throw new NoSuchElementException("Could not move up because this group does not have a parent");
        }

        this.index = parent.index;
        this.node = parent.node;
        this.parent = parent.parent;

        return this;
    }

    /**
     * Returns true if it is possible to navigate right.
     *
     * @return true, if it is possible to navigate right
     */
    public boolean canRight() {
        if (parent == null) {
            return false;
        }

        if (index + 1 >= parent.getNode().getGroups().size()) {
            return false;
        }

        return true;
    }

    /**
     * Navigates right the tree to the next node at the same level.
     *
     * @return a new groupzipper which points to next node at the same level
     * @throws NoSuchElementException
     *             if the last node at the current level has already been
     *             reached
     */
    public GroupZipper right() {
        if (!canRight()) {
            throw new NoSuchElementException("Could not move right because the last node at this level has already been reached");
        }

        index++;
        node = parent.getNode().getGroups().get(index);

        return this;
    }

    /**
     * Returns true if it is possible to navigate left.
     *
     * @return true, if it is possible to navigate left
     */
    public boolean canLeft() {
        if (index - 1 < 0) {
            return false;
        }

        return true;
    }

    /**
     * Navigates left the tree to the previous node at the same level.
     *
     * @return a new groupzipper which points to the previous node at the same
     *         level
     * @throws NoSuchElementException
     *             if the first node at the current level has already been
     *             reached
     */
    public GroupZipper left() {
        if (!canLeft()) {
            throw new NoSuchElementException("Could not move left because the first node at this level has already been reached");
        }

        index--;
        node = parent.getNode().getGroups().get(index);

        return this;
    }

    /**
     * Returns the current node. This can be seen as a pointer to the current
     * element in the tree.
     * <p>
     *
     * @return the current node
     */
    public Group getNode() {
        return node;
    }

    /**
     * Replaces the current node with the given one.
     * <p>
     * Can be used to modify the tree.
     *
     * @param group
     *            the replacement node
     * @return a new groupzipper with a replaced current node
     */
    public GroupZipper replace(Group group) {
        if (parent == null) {
            node = group;
        } else {
            parent.getNode().getGroups().set(index, group);
        }

        return this;
    }

    /**
     * Returns a new {@link KeePassFile} from the current tree structure.
     *
     * @return a new KeePass file
     */
    public KeePassFile close() {
        Group rootNode = getRoot();

        return new KeePassFileBuilder(meta).addTopGroups(rootNode).build();
    }

    /**
     * Creates a cloned {@link KeePassFile} from the current tree structure.
     *
     * @return a cloned KeePass file
     */
    public KeePassFile cloneKeePassFile() {
        Iterator<Group> iter = iterator();

        while(iter.hasNext()) {
            Group group = iter.next();

            Group clonedGroup = cloneGroup(group);
            replace(clonedGroup);
        }

        return close();
    }

    private Group cloneGroup(Group group) {
        GroupBuilder groupBuilder = new GroupBuilder(group);
        cloneEntriesInGroup(group, groupBuilder);
        Group clonedGroup = groupBuilder.build();

        return clonedGroup;
    }

    private void cloneEntriesInGroup(Group group, GroupBuilder groupBuilder) {
        List<Entry> removeList = new ArrayList<Entry>();
        List<Entry> addList = new ArrayList<Entry>();
        List<Entry> entries = group.getEntries();
        for (Entry entry : entries) {
            cloneEntry(entry, removeList, addList);
        }

        groupBuilder.removeEntries(removeList);
        groupBuilder.addEntries(addList);
    }

    private void cloneEntry(Entry entry, List<Entry> removeList, List<Entry> addList) {
        Entry clonedEntry = new EntryBuilder(entry).build();
        removeList.add(entry);
        addList.add(clonedEntry);
    }

    /**
     * Returns the root node of the tree.
     *
     * @return the root node of the tree
     */
    public Group getRoot() {
        if (parent == null) {
            return node;
        }

        return parent.getRoot();
    }

    /**
     * Replaces the meta with the given one.
     *
     * @param meta
     *            the given meta object
     * @return a new groupzipper with replaced meta
     */
    public GroupZipper replaceMeta(Meta meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public String toString() {
        return "GroupZipper [index=" + index + ", node=" + node + "]";
    }

    /**
     * Returns an iterator over the groups in this tree.
     *
     * @return an iterator to iterate through the tree
     */
    public Iterator<Group> iterator() {
        return new GroupIterator();
    }

    private class GroupIterator implements Iterator<Group> {

        boolean isFirst = true;

        /**
         * Checks if it is possible for any parent node to go right in the tree.
         *
         */
        private boolean canGoRightAtAnyLevel(GroupZipper parent) {
            if (parent == null) {
                return false;
            }

            if (parent.canRight()) {
                return true;
            } else {
                return canGoRightAtAnyLevel(parent.parent);
            }
        }

        private Group getNextRightNode(GroupZipper parent) {
            if (parent == null) {
                return null;
            }

            if (parent.canRight()) {
                return up().right().getNode();
            } else {
                return getNextRightNode(parent.up());
            }
        }

        @Override
        public boolean hasNext() {
            if (isFirst) {
                return true;
            }

            if (canDown() || canRight()) {
                return true;
            }

            return canGoRightAtAnyLevel(parent);
        }

        @Override
        public Group next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (isFirst) {
                isFirst = false;
                return getNode();
            }

            if (canDown()) {
                return down().getNode();
            }

            if (canRight()) {
                return right().getNode();
            }

            return getNextRightNode(parent);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported by GroupIterator");
        }
    }

}
