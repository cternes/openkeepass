package de.slackspace.openkeepass.domain.xml.adapter;

import org.simpleframework.xml.transform.Transform;
import org.spongycastle.util.encoders.Base64;

public class ByteSimpleXmlAdapter implements Transform<byte[]> {

    @Override
    public byte[] read(String value) throws Exception {
        return Base64.decode(value.getBytes());
    }

    @Override
    public String write(byte[] value) throws Exception {
        return Base64.toBase64String(value);
    }

}
