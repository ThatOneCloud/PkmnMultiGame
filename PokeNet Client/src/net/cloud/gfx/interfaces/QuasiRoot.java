package net.cloud.gfx.interfaces;

import net.cloud.client.tracking.StatTracker;
import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.focus.FocusController;

public class QuasiRoot extends Interface {

	/** This interface will show various system statistics on the screen */
	private Interface statOverlay;
	
	/** An interface for testing whatever */
	private Interface testInterface;

	/**
	 * Create a new root interface to the graphic element hierarchy. This is
	 * meant to serve as a bridge between some other graphic library. The
	 * position will be 0,0 as this is the root - there's really no other
	 * option.
	 * @param width The width of the graphic interface
	 * @param height The height of the graphic interface
	 */
	public QuasiRoot(int width, int height)
	{
		super(0, 0, width, height);

		statOverlay = null;
		testInterface = null;
	}

	/**
	 * When the root interface receives a key event, it will check to see if the
	 * key is essentially a global hotkey of some sort. Since this is sort of
	 * the final element any event should get to.
	 * @param key The key that was typed
	 */
	@Override
	public void keyTyped(char key)
	{
		// Check if we want to do something with the key
		switch (key) {
		
		// Toggle the statistics overlay
		case KeyConstants.STAT_OVERLAY:
			toggleStatOverlay();
			break;
			
		// Open the test interface. It should close itself too 
		case KeyConstants.TEST_INTERFACE:
			toggleTestInterface();
			break;
		}

		// No interest in the key. We'll still let it travel up the class
		// hierarchy.
		super.keyTyped(key);
	}

	/**
	 * Will show the stat overlay if it isn't already, otherwise it will close
	 * it.
	 */
	private void toggleStatOverlay()
	{
		// null is used to indicate it's not up
		if (statOverlay == null) {
			// Creation code is external
			statOverlay = InterfaceFactory.createStatOverlay(getWidth(),
					getHeight());

			// Add the interface to ourselves so it gets drawn
			super.add(statOverlay);

			// Tell the StatTracker the interface is up, so it'll start tracking
			StatTracker.instance().overlayOpened();
		}
		// Otherwise it must already be showing
		else {
			// So we need to remove it
			super.remove(statOverlay);
			statOverlay = null;

			// And tell the StatTracker it can stop for now
			StatTracker.instance().overlayClosed();
		}
	}
	
	/**
	 * Show the test interface - made to see if sprites or whatever are working. 
	 * It should well and remove itself. 
	 */
	private void toggleTestInterface()
	{
		if(testInterface == null)
		{
			// Change the constructor for different test interfaces
			testInterface = new SpriteTestInterface(getWidth(), getHeight());
			
			// Add the interface and transfer focus over to it
			add(testInterface);
			FocusController.instance().register(testInterface);
		}
		else {
			// Remove the interface and transfer focus back to us
			super.remove(testInterface);
			FocusController.instance().register(this);
			
			// Null it out for the next go-round
			testInterface = null;
		}
	}

}
