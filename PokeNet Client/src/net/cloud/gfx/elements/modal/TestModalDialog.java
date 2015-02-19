package net.cloud.gfx.elements.modal;

import net.cloud.gfx.Mainframe;
import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.ParentElement;
import net.cloud.gfx.elements.TextField;

/**
 * Quick and dirty dialog that can be created for testing purposes.
 * This class may as well be considered volatile... never know when I may change it or how I may change it.
 */
public class TestModalDialog extends AbstractModalDialog {

	/**
	 * A test modal dialog that will throw itself in the middle of the quasi-root, and be fairly sized
	 */
	public TestModalDialog()
	{
		super(new ParentElement(Mainframe.instance().gfx().rootPanel().getQuasiRoot()), 100, 250, 200, 200);
		
		Interface intf = new Interface(0, 0, 200, 200);
		
		TextField textField = new TextField(20, 20, 100, 22, "hint");
		intf.add(textField);
		
		Button button = new Button("Button", 20, 60, 75, 25);
		intf.add(button);
		
		add(intf);
	}

}
