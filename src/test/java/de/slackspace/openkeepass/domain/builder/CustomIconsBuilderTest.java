package de.slackspace.openkeepass.domain.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import de.slackspace.openkeepass.domain.CustomIcon;
import de.slackspace.openkeepass.domain.CustomIconBuilder;
import de.slackspace.openkeepass.domain.CustomIcons;
import de.slackspace.openkeepass.domain.CustomIconsBuilder;

public class CustomIconsBuilderTest {

    @Test
    public void shouldBuildCustomIcons() {
        CustomIcon customIconOne = createIcon(UUID.randomUUID());
        CustomIcon customIconTwo = createIcon(UUID.randomUUID());

        CustomIcons customIcons = new CustomIconsBuilder().addIcon(customIconOne).addIcon(customIconTwo).build();

        Assert.assertEquals(2, customIcons.getIcons().size());
    }

    @Test
    public void shouldFindIconByUuid() {
        UUID idOne = UUID.randomUUID();
        UUID idTwo = UUID.randomUUID();
        CustomIcon customIconOne = createIcon(idOne);
        CustomIcon customIconTwo = createIcon(idTwo);

        CustomIcons customIcons = new CustomIconsBuilder().addIcon(customIconOne).addIcon(customIconTwo).build();

        Assert.assertEquals(idOne, customIcons.getIconByUuid(idOne).getUuid());
        Assert.assertEquals(idTwo, customIcons.getIconByUuid(idTwo).getUuid());
    }

    @Test
    public void shouldBuildCustomIconsFromExisting() {
        List<CustomIcon> iconList = new ArrayList<CustomIcon>();
        iconList.add(createIcon(UUID.randomUUID()));

        CustomIcons customIcons = new CustomIconsBuilder().customIcons(iconList).build();
        Assert.assertEquals(1, customIcons.getIcons().size());

        CustomIcons customIconsClone = new CustomIconsBuilder(customIcons).build();
        Assert.assertEquals(1, customIconsClone.getIcons().size());
    }

    private CustomIcon createIcon(UUID id) {
        CustomIcon customIconOne = new CustomIconBuilder().uuid(id).build();
        return customIconOne;
    }
}
