package de.slackspace.openkeepass.domain.xml.adapter;

import java.util.UUID;

import org.bouncycastle.util.encoders.Base64;
import org.simpleframework.xml.transform.Transform;

import de.slackspace.openkeepass.util.ByteUtils;

public class UUIDSimpleXmlAdapter implements Transform<UUID> {

    @Override
    public UUID read(String value) throws Exception {
        return null;
    }

    @Override
    public String write(UUID value) throws Exception {
        return Base64.toBase64String(ByteUtils.uuidToBytes(value));
    }

}
