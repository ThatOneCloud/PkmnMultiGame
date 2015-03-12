package net.cloud.gfx.elements;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import net.cloud.gfx.constants.Priority;

/**
 * A radio button is the circular counterpart to a checkbox. However, radio buttons are placed into 
 * logical groups where only one of the buttons may be selected at a time. It is possible for none 
 * of the buttons in the group to be selected. <b>Note:</b> The order of creation defines the order in the group.<br>
 * As with checkbox, there is a label to the right of the button and there are limited constructor options 
 * but more may be accessed via setter methods. 
 */
public class RadioButton extends SelectableButton {
	
	/** Default priority of a Button. Moderate. */
	public static final int PRIORITY = Priority.MED;
	
	/** The group this button belongs to */
	private RadioButtonGroup group;
	
	/** Called when an action is performed on the button. */
	private Optional<Consumer<RadioButton>> actionHandler;
	
	/**
	 * Create a new radio button with default size, image assets, and priority. The default size is 
	 * 15x15. If you want to resize much bigger than that, it's probably a better idea to use a larger sprite. 
	 * The radio button will be placed at the end of the given group. <b>Note:</b> The order of creation defines the order in the group.
	 * @param group The logical group the button should be placed into
	 * @param label Text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 */
	public RadioButton(RadioButtonGroup group, String label, int x, int y)
	{
		// Defaults!
		this(group, label, x, y, 15, 15, 9);
	}
	
	/**
	 * Create a new radio button with default image assets, and priority. The provided dimensions are only for the button. 
	 * If you want to resize much bigger than that, it's probably a better idea to use a larger sprite. 
	 * The radio button will be placed at the end of the given group. <b>Note:</b> The order of creation defines the order in the group.
	 * @param group The logical group the button should be placed into
	 * @param label Text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the checkbox itself - does not include the text
	 * @param height The height of the checkbox itself - does not include the text
	 */
	public RadioButton(RadioButtonGroup group, String label, int x, int y, int width, int height)
	{
		// Less defaults!
		this(group, label, x, y, width, height, 9);
	}

	/**
	 * Create a new radio button with the given parameters. The provided dimensions are only for the button. 
	 * If you want to resize much bigger than that, it's probably a better idea to use a larger sprite. 
	 * If you want to use a custom sprite, prefer this constructor to avoid extra image work. 
	 * The radio button will be placed at the end of the given group. <b>Note:</b> The order of creation defines the order in the group.
	 * @param group The logical group the button should be placed into
	 * @param label Text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the checkbox itself - does not include the text
	 * @param height The height of the checkbox itself - does not include the text
	 * @param firstSpriteID The sprite ID of the first background image asset
	 */
	public RadioButton(RadioButtonGroup group, String label, int x, int y, int width, int height, int firstSpriteID)
	{
		// All taken care of by SelectableButton!
		super(label, PRIORITY, x, y, width, height, firstSpriteID);
		
		// Woops! Forgot this
		this.actionHandler = Optional.empty();
		
		// And this!
		this.group = group;
		group.add(this);
	}
	
	/**
	 * Toggles the state of the radio button, and then informs the action handler if one is present. 
	 */
	@Override
	public void actionPerformed()
	{
		// We let the group worry about selection for a radio button
		group.buttonAction(this);
		
		// Then we worry about passing the event off to a handler
		actionHandler.ifPresent((handler) -> handler.accept(this));
	}
	
	/**
	 * Set a new action to this button. When the button is pressed or has an action performed on it, 
	 * the action's method will be called and supply this button instance. By providing null as a parameter 
	 * to this method, you may remove the current handler. 
	 * This may be fired even when the selection state does not change, as a radio button does not toggle. 
	 * @param action The method to call when action is taken on this button
	 */
	public void setActionHandler(Consumer<RadioButton> action)
	{
		this.actionHandler = Optional.ofNullable(action);
	}
	
	
	
	/**
	 * A logical grouping of Radio Buttons. Controls some of the behaviors, such as enforcing selection of 
	 * one at a time and linking them in a focus traversal chain. 
	 */
	public static class RadioButtonGroup {
		
		/** The list of all buttons in the group */
		private LinkedList<RadioButton> buttons;
		
		/** The button which is currently selected - for convenience */
		private RadioButton currentlySelected;
		
		/** The handler which covers the entire group. Useful as opposed to setting a handler on each button. */
		private Optional<Consumer<RadioButtonEvent>> actionHandler;
		
		/**
		 * Create a new RadioButtonGroup with no buttons in it, no currently selected button, and no action handler. <br>
		 * Use <code>add(RadioButton)</code> to add buttons - in the order you want them. <br>
		 * Use <code>setSelected(int)</code> to set which button is selected. Doing this after adding buttons is like setting a default. <br>
		 * Use <code>setActionHandler(Consumer<RadioButtonEvent>)</code> to set an action handler over the entire group. <br>
		 */
		public RadioButtonGroup()
		{
			buttons = new LinkedList<RadioButton>();
			currentlySelected = null;
			actionHandler = Optional.empty();
		}
		
		/**
		 * @return The button currently selected in the group. May be null.
		 */
		public RadioButton getSelectedButton()
		{
			return currentlySelected;
		}
		
		/**
		 * @return The index of the currently selected button. -1 if there is none.
		 */
		public int getSelectedIndex()
		{
			// Don't even bother if we know there isn't one
			if(currentlySelected == null)
			{
				return -1;
			}
			
			// I wrote the loop out first... but then this.
			return buttons.indexOf(currentlySelected);
		}
		
		/**
		 * Finds the given button in the group and selects it. If the button is not in the group, 
		 * then false is returned and nothing happens. This will trigger an event if a handler is set. 
		 * @param newSelection The new button to make selected
		 * @return False if it was not in the group
		 */
		public boolean setSelection(RadioButton newSelection)
		{
			// Make sure the button is in the group
			if(!buttons.contains(newSelection))
			{
				return false;
			}
			
			// It's in the group. Have it become selected.
			buttonAction(newSelection);
			return true;
		}
		
		/**
		 * Set a button in the group to be selected. If another button is already selected, it 
		 * will be deselected. This will also trigger an event if there is a handler. 
		 * Prefer this method over <code>setSelection(RadioButton)</code>
		 * @param index The 0 based index of the button
		 */
		public void setSelection(int index)
		{
			// Get whichever button is requested
			RadioButton toSelected = buttons.get(index);
			
			// And have it become selected
			buttonAction(toSelected);
		}
		
		/**
		 * Selects the next button in the group. Works in a circular fashion, so if the current button 
		 * is the last, then the first button in the group will become selected. Of course, if there are no 
		 * buttons or only one in the group, this will do nothing. This will trigger an action event. 
		 */
		public void selectNext()
		{
			// Do nothing?
			if(buttons.size() <= 1)
			{
				return;
			}
			
			// Set the selection to the next button (circular fashion)
			setSelection((getSelectedIndex() + 1) % buttons.size());
		}
		
		/**
		 * Selects the previous button in the group. Works in a circular fashion, so if the current button 
		 * is the first, then the last button in the group will become selected. Of course, if there are no 
		 * buttons or only one in the group, this will do nothing. This will trigger an action event.
		 */
		public void selectPrevious()
		{
			// Do nothing?
			if(buttons.size() <= 1)
			{
				return;
			}
			
			int nextIdx = getSelectedIndex() - 1;
			int size = buttons.size();
			setSelection(((nextIdx % size) + size) % size);
		}
		
		/**
		 * Add a button to this group. The button will be added to the end of the group (So add buttons 
		 * in the order you want them in the group.) This will also establish a focus chain within the group. 
		 * The first and last buttons will not have previous and next elements, respectively. However, there will be 
		 * links established within the group itself. They are not maintained and may be reset if need be. 
		 * @param button The button to append to the group
		 */
		protected void add(RadioButton button)
		{
			// Establish link with previous button, if there is one
			if(!buttons.isEmpty())
			{
				button.linkPreviousFocusable(button, buttons.getLast());
			}
			
			// Add to the end of the list (doing this after link so accessing the previous button is easier)
			buttons.addLast(button);
		}
		
		/**
		 * Called when a button has an action performed on it.
		 * @param actedUpon The button that had the action happen
		 */
		protected void buttonAction(RadioButton actedUpon)
		{
			// Nothing happens within the group if the acted upon button is already selected
			if(actedUpon.isSelected() || actedUpon == currentlySelected)
			{
				return;
			}
			
			// Now the currently selected button becomes deselected
			RadioButton deselected = currentlySelected;
			if(currentlySelected != null)
			{
				currentlySelected.setSelected(false);
			}
			
			// A new button is currently selected
			currentlySelected = actedUpon;
			actedUpon.setSelected(true);
			
			// This constitutes an action happening
			actionHandler.ifPresent((h) -> h.accept(new RadioButtonEvent(this, Optional.ofNullable(deselected), currentlySelected)));
		}
		
		/**
		 * Set a new action to this group. When a button's state is changed within the group, 
		 * the action's method will be called and supply information about what happened. By providing null as a parameter 
		 * to this method, you may remove the current handler. 
		 * @param action The method to call when action is taken on this button
		 */
		public void setActionHandler(Consumer<RadioButtonEvent> action)
		{
			this.actionHandler = Optional.ofNullable(action);
		}
	}
	
	
	
	/**
	 * Rather than cram lots of parameters into a Consumer, just wrap them in a single object... should be easier to... consume.
	 */
	public static class RadioButtonEvent {
		
		/** The group that the event happened within */
		private final RadioButtonGroup group;
		
		/** The button that was de-selected. May not be one, if there was not already a selection */
		private final Optional<RadioButton> deselected;
		
		/** The button that was selected. This will always be present. */
		private final RadioButton selected;

		/**
		 * Create a new event. 
		 * @param group The group the event happened within
		 * @param deselected The button that was de-selected, if any
		 * @param selected The button that was selected
		 */
		protected RadioButtonEvent(RadioButtonGroup group, Optional<RadioButton> deselected, RadioButton selected)
		{
			this.group = group;
			this.deselected = deselected;
			this.selected = selected;
		}

		/** @return The group the event happened within */
		public RadioButtonGroup getGroup()
		{
			return group;
		}

		/** @return The de-selected button. May not always be present. */
		public Optional<RadioButton> getDeselected()
		{
			return deselected;
		}

		/** @return The newly selected button */
		public RadioButton getSelected()
		{
			return selected;
		}
		
	}

}
