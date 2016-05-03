package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class History implements Cloneable{

    @XmlElement(name = "Entry")
    private List<Entry> entries = new ArrayList<Entry>();

    public List<Entry> getHistoricEntries() {
        return entries;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entries == null) ? 0 : entries.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof History))
            return false;
        History other = (History) obj;
        if (entries == null) {
            if (other.entries != null)
                return false;
        } else if (!entries.equals(other.entries))
            return false;
        return true;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
    	History ret = new History();
    	for(Entry entry:entries){
	    	ret.entries.add((Entry) entry.clone());
		}
    	return ret;
    }

}
