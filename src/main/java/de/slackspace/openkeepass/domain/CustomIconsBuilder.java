package de.slackspace.openkeepass.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder to create {@link CustomIcons} objects.
 *
 */
public class CustomIconsBuilder implements CustomIconsContract {

    List<CustomIcon> customIcons = new ArrayList<CustomIcon>();

    public CustomIconsBuilder() {
        // default no-args constructor
    }

    public CustomIconsBuilder(CustomIcons customIcons) {
        this.customIcons = customIcons.getIcons();
    }

    public CustomIconsBuilder customIcons(List<CustomIcon> customIcons) {
        this.customIcons = customIcons;
        return this;
    }

    public CustomIconsBuilder addIcon(CustomIcon icon) {
        customIcons.add(icon);
        return this;
    }

    /**
     * Builds a new custom icons list with the values from the builder.
     *
     * @return a new CustomIcons object
     */
    public CustomIcons build() {
        return new CustomIcons(this);
    }

    @Override
    public List<CustomIcon> getCustomIcons() {
        return customIcons;
    }
}
