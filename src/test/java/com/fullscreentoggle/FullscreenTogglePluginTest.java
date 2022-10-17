package com.fullscreentoggle;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FullscreenTogglePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FullscreenTogglePlugin.class);
		RuneLite.main(args);
	}
}