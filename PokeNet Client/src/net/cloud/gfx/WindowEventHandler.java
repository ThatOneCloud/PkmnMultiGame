package net.cloud.gfx;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.cloud.mmo.Client;

/**
 * This handler will deal with WindowEvents. It's a WindowListener suited for dealing with 
 * when something happens to the application window.
 */
public class WindowEventHandler extends WindowAdapter {
	
	/**
	 * When the application window is closed, attempt graceful shutdown of the whole program.
	 */
	@Override
	public void windowClosed(WindowEvent evt)
	{
		Client.getInstance().shutdown();
	}

}
