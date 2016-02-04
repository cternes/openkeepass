package de.slackspace.openkeepass.domain;

import java.util.List;

/**
 * A builder to create {@link CustomIcons} objects.
 *
 */
public class CustomIconsBuilder {

	List<CustomIcon> customIcons;

	public CustomIconsBuilder() {
	}

	public CustomIconsBuilder(CustomIcons customIcons) {
		this.customIcons = customIcons.getIcons();
	}

	public CustomIconsBuilder customIcons(List<CustomIcon> customIcons) {
		this.customIcons = customIcons;
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
}
