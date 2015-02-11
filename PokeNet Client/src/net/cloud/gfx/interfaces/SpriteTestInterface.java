package net.cloud.gfx.interfaces;

import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.PasswordField;
import net.cloud.gfx.elements.Sprite;
import net.cloud.gfx.elements.TextField;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * Interface to make sure sprites are loading. 
 * Shows test sprites in four corners and in middle. 
 */
public class SpriteTestInterface extends Interface {
	
	/**
	 * Create interface
	 * @param width
	 * @param height
	 */
	public SpriteTestInterface(int width, int height)
	{
		super(0, 0, width, height);
		
		// Add sprites to corners
//		add(new Sprite(SpriteSet.TEST, 0, 0, 0));
		add(new Sprite(SpriteSet.TEST, 1, width-50, 0));
		add(new Sprite(SpriteSet.TEST, 2, 0, height-50));
		add(new Sprite(SpriteSet.TEST, 3, width-50, height-50));
		add(new Sprite(SpriteSet.TEST, 4, (width / 2) - 25, (height / 2) - 25));
		
		
		
		TextField textField = new TextField(50, 50, 150, 22, "hint");
		add(textField);
		
		PasswordField pwField = new PasswordField(50, 100, 150, 22);
		add(pwField);
		
		textField.setActionHandler((field, text) -> System.out.println(textField.getText() + ", " + pwField.getText()));
		pwField.setActionHandler((field, text) -> System.out.println(textField.getText() + ", " + pwField.getText()));
		
		textField.linkNextFocusable(pwField);
		pwField.linkNextFocusable(textField);
		
		
		
		Button button = new Button("Button", 50, 150, 75, 25);
		add(button);
	}

}
