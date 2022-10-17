package com.fullscreentoggle;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface FullscreenToggleConfig extends Config
{
	@ConfigItem(
		keyName = "fullscreentoggle",
		name = "Fullscreen Toggle",
		description = "Allows you to toggle fullscreen on and off"
	)
	default boolean fullscreentoggle()
	{
		return false;
	}
}
