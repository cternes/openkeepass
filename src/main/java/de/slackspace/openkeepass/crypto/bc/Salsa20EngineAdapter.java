package de.slackspace.openkeepass.crypto.bc;

import java.lang.reflect.Method;

import org.bouncycastle.crypto.engines.Salsa20Engine;

/**
 * Adapter around BC's Salsa20 engine. Designed for BC 1.50+, but fallbacks to a
 * compatible method call using reflection in case of earlier BC implementations
 * where the processBytes method would return void.
 */
public class Salsa20EngineAdapter extends Salsa20Engine {

  private static final Method PROCESS_BYTES_METHOD;
  
  static {
    Method result = null;
    try {
      result = Salsa20Engine.class.getDeclaredMethod("processBytes", new Class<?>[] { byte[].class, Integer.TYPE, Integer.TYPE, byte[].class, Integer.TYPE });
    } catch (Throwable reflectionError) {
      // Ignore
    }
    PROCESS_BYTES_METHOD = result;
  }
  
  @Override
  public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
    try {
      return super.processBytes(in, inOff, len, out, outOff);
    } catch (NoSuchMethodError nsm) {
      if (PROCESS_BYTES_METHOD == null) {
        throw nsm;
      }
      try {
        PROCESS_BYTES_METHOD.invoke(this, in, inOff, len, out, outOff);
        return -1;
      } catch (Throwable reflectionError) {
        throw nsm;
      }
    }
  }
  
}
