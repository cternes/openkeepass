package de.slackspace.openkeepass.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IconUtils {
  public static byte[] getStockIconData(int iconId) {
    if (iconId < 0) return null;

    InputStream is = IconUtils.class.getResourceAsStream("/icons/" + iconId + ".png");
    if (is == null) return null;

    try {
      return StreamUtils.toByteArray(is);
    } catch (IOException e) {
      return null;
    }
  }

  public static BufferedImage bytesToBufferedImage(byte[] data) throws IOException {
    return ImageIO.read(new ByteArrayInputStream(data));
  }
}
