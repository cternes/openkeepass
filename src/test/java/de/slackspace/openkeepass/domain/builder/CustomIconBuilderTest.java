package de.slackspace.openkeepass.domain.builder;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.CustomIcon;
import de.slackspace.openkeepass.domain.CustomIconBuilder;

public class CustomIconBuilderTest {

    @Test
    public void shouldBuildCustomIcon() {
        UUID uuid = UUID.randomUUID();
        byte[] data = new byte[4];

        CustomIcon customIcon = new CustomIconBuilder().uuid(uuid).data(data).build();

        Assert.assertEquals(uuid, customIcon.getUuid());
        Assert.assertEquals(data, customIcon.getData());
    }

    @Test
    public void shouldBuildCustomIconFromExistingCustomIcon() {
        UUID uuid = UUID.randomUUID();
        byte[] data = new byte[4];

        CustomIcon customIcon = new CustomIconBuilder().uuid(uuid).data(data).build();
        CustomIcon customIconClone = new CustomIconBuilder(customIcon).build();

        Assert.assertEquals(uuid, customIconClone.getUuid());
        Assert.assertEquals(data, customIconClone.getData());
    }
}
