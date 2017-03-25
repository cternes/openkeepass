package de.slackspace.openkeepass.domain;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class VariantDictionary {
    
    private static final String UTF_8 = "UTF-8";

    private static final int VDM_CRITICAL = 0xFF00;
    private static final int VDM_VERSION = 0x0100;

    private static final int UINT32 = 0x04;
    private static final int UINT64 = 0x05;
    private static final int BOOLEAN = 0x08;
    private static final int INT32 = 0x0C;
    private static final int INT64 = 0x0D;
    private static final int STRING = 0x18;
    private static final int BYTE_ARRAY = 0x42;

    private Map<String, Object> map = new HashMap<String, Object>();

    public VariantDictionary(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int version = buffer.getShort();
        
        if(!isVersionSupported(version)) {
            throw new UnsupportedOperationException("Cannot read Kdf parameters. A newer version is required to open this keepass file.");
        }
        
        while(true) {
            int btType = buffer.get();
            
            if(btType == 0) {
                break;
            }
            
            int nameLength = buffer.getInt();
            byte[] nameBytes = new byte[nameLength];
            buffer.get(nameBytes, 0, nameLength);

            String cbName = new String(nameBytes, Charset.forName(UTF_8));
            
            int valueLength = buffer.getInt();
            byte[] pbValue = new byte[valueLength];
            buffer.get(pbValue, 0, valueLength);
            
            switch(btType) {
                case BYTE_ARRAY: 
                    map.put(cbName, pbValue);
                    break;
                case UINT32:
                    int pbValueUInt = ByteBuffer.wrap(pbValue).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    map.put(cbName, pbValueUInt);
                    break;
                case UINT64:
                    long pbValueULong = ByteBuffer.wrap(pbValue).order(ByteOrder.LITTLE_ENDIAN).getLong();
                    map.put(cbName, pbValueULong);
                    break;
                case BOOLEAN:
                    boolean pbValueBool = pbValue[0] != 0;
                    map.put(cbName, pbValueBool);
                    break;
                case INT32:
                    int pbValueInt = ByteBuffer.wrap(pbValue).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    map.put(cbName, pbValueInt);
                    break;
                case INT64:
                    long pbValueLong = ByteBuffer.wrap(pbValue).order(ByteOrder.LITTLE_ENDIAN).getLong();
                    map.put(cbName, pbValueLong);
                    break;
                case STRING:
                    String pbValueString = new String(pbValue, Charset.forName(UTF_8));        
                    map.put(cbName, pbValueString);
                    break;
            }
        }
    }

    private boolean isVersionSupported(int version) {
        if ((version & VDM_CRITICAL) > (VDM_VERSION & VDM_CRITICAL)) {
            return false;
        }

        return true;
    }

    public byte[] getByteArray(String key) {
        return (byte[]) map.get(key);
    }

    public int getInt(String key) {
        return (Integer) map.get(key);
    }
    
    public long getLong(String key) {
        return (Long) map.get(key);
    }
}
