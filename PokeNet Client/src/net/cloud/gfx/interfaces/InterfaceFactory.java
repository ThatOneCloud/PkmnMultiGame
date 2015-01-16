package net.cloud.gfx.interfaces;

import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.ReferenceText;
import net.cloud.mmo.tracking.StatTracker;
import net.cloud.mmo.util.StringUtil;

import java.util.function.Function;

/**
 * Factory class for interfaces that are fairly simple, and not justifiably
 * their own class. So, it's just a location for some code snippets as compared
 * to creating a class for each snippet.
 */
public class InterfaceFactory {

	/**
	 * Creates an interface which will show various system statistics. It will
	 * be an overlay - essentially no background and no dimensions so that it
	 * will not hide the elements under it and will not consume any input
	 * events.
	 * 
	 * @param width
	 *            The width of the interface this will be in - for positioning
	 * @param height
	 *            The height of the interface this will be in - for positioning
	 * @return An Interface which can be added to some Container... maybe the
	 *         root...
	 */
	public static Interface createStatOverlay(int width, int height) {
		// Create an interface to add components to
		Interface overlay = new Interface();

		// Add components which will show statistics
		// The text which will show the current fps
		ReferenceText fpsText = new ReferenceText("FPS: ",
				createUpdateFpsFunction());
		overlay.add(fpsText);

		return overlay;
	}

	/**
	 * Create a Function object which will supply a string for the FPS
	 * statistics label. The function will only create a new String object if
	 * need be, preferring to re-use the existing String if it's the same. I
	 * think an anonymous inner class may be the best solution, lambdas won't
	 * work since I need to store the previous value and modify it.
	 * 
	 * @return A Function supplying an updated FPS string
	 */
	private static Function<String, String> createUpdateFpsFunction() {
		return new Function<String, String>() {
			double previousFps = 0.0; // Keep track of the fps from the previous
										// update
			int CYCLE_LIMIT = 5; // How many cycles to wait before letting the
									// string update
			int cycleCount = CYCLE_LIMIT; // Keep track of how many times we're
											// called. Don't update too fast...
											// can't read it. Start off
											// updating, though.

			@Override
			public String apply(String curTxt) {
				// Grab the latest fps reading
				double curFps = StatTracker.instance().getStats().getFpsStat();

				// Check if an update is even necessary (must be a new number
				// and reached cycle limit)
				if ((previousFps == curFps) || (cycleCount < CYCLE_LIMIT)) {
					cycleCount++;
					return curTxt;
				}
				// Yes, we're going to allow the update
				else {
					// Reset the cycle count
					cycleCount = 0;

					// Make sure to update the previous fps reading
					previousFps = curFps;
					return "FPS: " + StringUtil.cleanDecimal(curFps, 1);
				}
			}

		};
	}

}
