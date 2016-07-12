package de.slackspace.openkeepass.domain.xml.adapter;

import org.bouncycastle.util.encoders.Base64;
import org.simpleframework.xml.transform.Transform;

public class ByteSimpleXmlAdapter implements Transform<byte[]> {

    @Override
    public byte[] read(String value) throws Exception {
        return null;
    }

    @Override
    public String write(byte[] value) throws Exception {
        return Base64.toBase64String(value);
    }

}
