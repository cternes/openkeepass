package de.slackspace.openkeepass.xml;

import java.util.UUID;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.slackspace.openkeepass.util.ByteUtils;

/**
 * This class is a JAXB adapter to transform UUIDs to/from xml using JAXB.
 * <p>
 * This works because JAXB is representing bytes by default as Base64 in xml.
 *
 */
public class UUIDXmlAdapter extends XmlAdapter<byte[], UUID> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public byte[] marshal(UUID value) throws Exception {
		byte[] bytes = ByteUtils.uuidToBytes(value);

		return bytes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public UUID unmarshal(byte[] value) throws Exception {
		return ByteUtils.bytesToUUID(value);
	}

}
