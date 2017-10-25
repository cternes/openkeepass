package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Represents a list of binary items in the metadata of a KeePass file.
 *
 */
@Root(name = "Binaries", strict = false)
public class Binaries {

    @ElementList(name = "Binary", inline = true, required = false)
    private List<Binary> binaryList = new ArrayList<Binary>();

    Binaries() {
    }

    public Binaries(BinariesContract binariesContract) {
        this.binaryList = binariesContract.getBinaries();
    }

    /**
     * Returns all binary items found inside the database.
     *
     * @return a list of attachments
     */
    public List<Binary> getBinaries() {
        return binaryList;
    }

    /**
     * Retrieves a binary item based on its id.
     *
     * @param id the id which should be searched
     * @return the binary item if found, null otherwise
     */
    public Binary getBinaryById(int id) {
        for (Binary binary : binaryList) {
            if (binary.getId() == id) {
                return binary;
            }
        }

        return null;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((binaryList == null) ? 0 : binaryList.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Binaries))
            return false;
        Binaries other = (Binaries) obj;
        if (binaryList == null) {
            if (other.binaryList != null)
                return false;
        } else if (!binaryList.equals(other.binaryList))
            return false;
        return true;
    }

}
