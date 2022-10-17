package com.fullscreentoggle;

import javax.inject.Inject;
import javax.swing.*;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ExpandResizeType;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.*;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@PluginDescriptor(
	name = "Fullscreen Toggle"
)
public class FullscreenTogglePlugin extends Plugin
{
	@Inject
	private ClientUI clientUI;
	@Inject
	private ConfigManager configManager;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ClientPluginToolbar clientPluginToolbar;
	private GraphicsDevice gd;
	private NavigationButton navButton;

	private int defaultDecorations = -1;
	private JRootPane rootPane;
	private ContainableFrame frame;
	private Window window;

//	@Inject
//	private NavigationButton getNavButton;
//	@Inject
//	private JButton getSidebarNavigationJButton;
//	@Inject
//	private BufferedImage getSidebarOpenIcon;
//	@Inject
//	private BufferedImage getSidebarClosedIcon;
//	@Inject
//	private boolean sidebarOpen;
	@Override
	protected void startUp() {
		log.info("Fullscreen started!");
		configManager.setConfiguration("runelite", "automaticResizeType", ExpandResizeType.KEEP_WINDOW_SIZE);
		gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		//get root frame
		Window[] windows = Frame.getWindows();
		//log the window names
		for (Window w : windows) {
			if(w instanceof ContainableFrame){
				window = w;
			}
		}
		frame = (ContainableFrame) window;
		rootPane = ((ContainableFrame) window).getRootPane();

		navButton =
				NavigationButton.builder()
						.tooltip("Fullscreen Toggle")
						.icon(Icons.NAV_BUTTON)
						.priority(5)
						.onClick(this::selectNavButton)
						.build();

		clientToolbar.addNavigation(navButton);
	}

	private void toggleFullScreenWindow(boolean on) throws InterruptedException, InvocationTargetException {

		if(!on) {
			gd.setFullScreenWindow(null);
			rootPane.setWindowDecorationStyle(defaultDecorations);
			return;
		}

		assert frame != null;
		frame.setExpandResizeType(ExpandResizeType.KEEP_WINDOW_SIZE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		if(defaultDecorations == -1) {
			defaultDecorations = rootPane.getWindowDecorationStyle();
		}
		rootPane.setWindowDecorationStyle(JRootPane.NONE);

		//epic hackeronies
		gd.setFullScreenWindow(frame);
		gd.setFullScreenWindow(null);
		gd.setFullScreenWindow(frame);

		//AddToggleSideBarButton();

		rootPane.grabFocus();
	}

//	private void AddToggleSideBarButton() throws InterruptedException, InvocationTargetException {
//		SwingUtilities.invokeAndWait(() ->
//				{
//					clientToolbar.addNavigation(getNavButton);
//					clientPluginToolbar.add(getSidebarNavigationJButton);
//					// Open sidebar if the config closed state is unset
//					if (configManager.getConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED) == null)
//					{
//						toggleSidebar();
//					}
//				}
//				);
//	}

//	private void toggleSidebar()
//	{
//		// Toggle sidebar open
//		boolean isSidebarOpen = sidebarOpen;
//		sidebarOpen = !sidebarOpen;
//
//		if (isSidebarOpen)
//		{
//			getSidebarNavigationJButton.setIcon(new ImageIcon(getSidebarOpenIcon));
//			getSidebarNavigationJButton.setToolTipText("Open SideBar");
//			configManager.setConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED, true);
//
//			// Remove plugin toolbar
//			window.remove(clientPluginToolbar);
//		}
//		else
//		{
//			getSidebarNavigationJButton.setIcon(new ImageIcon(getSidebarClosedIcon));
//			getSidebarNavigationJButton.setToolTipText("Close SideBar");
//			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_CLIENT_SIDEBAR_CLOSED);
//
//			// Add plugin toolbar back
//			window.add(clientPluginToolbar);
//		}
//
//		// Revalidate sizes of affected Swing components
//		window.revalidate();
//		rootPane.grabFocus();
//	}


	@Override
	protected void shutDown() throws InterruptedException, InvocationTargetException {
		toggleFullScreenWindow(false);
		clientToolbar.removeNavigation(navButton);
		log.info("Fullscreen stopped!");
	}

	@Provides
	FullscreenToggleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FullscreenToggleConfig.class);
	}

	public void selectNavButton() {
		SwingUtilities.invokeLater(
				() -> {
					log.info("Fullscreen toggle!");
					try {
						toggleFullScreenWindow(gd.getFullScreenWindow() == null);
					} catch (InterruptedException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				});
	}
}
