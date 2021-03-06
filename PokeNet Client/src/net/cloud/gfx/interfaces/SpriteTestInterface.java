package net.cloud.gfx.interfaces;

import net.cloud.gfx.constants.Colors;
import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.CenteredText;
import net.cloud.gfx.elements.Checkbox;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.PasswordField;
import net.cloud.gfx.elements.RadioButton;
import net.cloud.gfx.elements.ReferenceText;
import net.cloud.gfx.elements.RadioButton.RadioButtonGroup;
import net.cloud.gfx.elements.ScrollView;
import net.cloud.gfx.elements.TextArea;
import net.cloud.gfx.elements.VerticalView;
import net.cloud.gfx.elements.decorator.DraggableElement;
import net.cloud.gfx.elements.decorator.FrameButton;
import net.cloud.gfx.elements.decorator.FramedElement;
import net.cloud.gfx.elements.Sprite;
import net.cloud.gfx.elements.Text;
import net.cloud.gfx.elements.TextField;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * Interface to make sure sprites are loading. 
 * Shows test sprites in four corners and in middle. 
 */
public class SpriteTestInterface extends Interface {
	
	/**
	 * Create interface
	 * @param width width
	 * @param height height
	 */
	public SpriteTestInterface(int width, int height)
	{
		super(0, 0, width, height);
		super.setPriority(10);
		
		// Add sprites to corners
//		add(new Sprite(SpriteSet.TEST, 0, 0, 0));
//		add(new Sprite(SpriteSet.TEST, 1, width-50, 0));
//		add(new Sprite(SpriteSet.TEST, 2, 0, height-50));
		add(new Sprite(SpriteSet.TEST, 3, 150, 130));
//		add(new Sprite(SpriteSet.TEST, 4, (width / 2) - 25, (height / 2) - 25));
		
		
		
		TextField textField = new TextField(50, 50, 150, 22, "hint");
		add(textField);
		
		PasswordField pwField = new PasswordField(50, 100, 150, 22);
		add(pwField);
		
		textField.setActionHandler((field, text) -> System.out.println(textField.getText() + ", " + pwField.getText()));
		pwField.setActionHandler((field, text) -> System.out.println(textField.getText() + ", " + pwField.getText()));
		
		textField.linkNextFocusable(textField, pwField);
		pwField.linkNextFocusable(pwField, textField);
		
		
		
		Button button = new Button("Button", 50, 150, 75, 25);
		add(button);
		button.setDeregisterTarget(this);
		
		Checkbox checkbox = new Checkbox("Checkbox", 60, 200);
		checkbox.setActionHandler((b) -> System.out.println("Button selected? " + b.isSelected()));
		add(checkbox);
		
		RadioButtonGroup radioGroup = new RadioButtonGroup();
		radioGroup.setActionHandler((evt) -> System.out.println("Selected " + evt.getSelected().getLabelText()));
		RadioButton radio1 = new RadioButton(radioGroup, "Red", 200, 200);
		RadioButton radio2 = new RadioButton(radioGroup, "Green", 200, 220);
		RadioButton radio3 = new RadioButton(radioGroup, "Blue", 200, 240);
		radio1.setLabelColor(Colors.RED.get());
		radio2.setLabelColor(Colors.GREEN.get());
		radio3.setLabelColor(Colors.BLUE.get());
		add(radio1);
		add(radio2);
		add(radio3);
		radio3.linkNextFocusable(radio3, radio1);

		
		
		
		Interface frInt = new Interface(20, 270, 100, 100);
		frInt.setBackground(SpriteSet.BACKGROUND, 0);
		Text frTxt = new Text("Framed", 10, 10);
		Button frBut = new Button("Interface", 10, 40, 75, 25);
		frInt.add(frTxt);
		frInt.add(frBut);
		FramedElement frame = new FramedElement("drag me", frInt, FrameButton.CLOSE);
		frame.setWidth(250);
		DraggableElement drag = new DraggableElement(frame);
		drag.addStartBound(frame.getTitleBounds());
		add(drag);
		
		
		
		Text text = new Text("Plain text", 600, 20);
		add(text);
		CenteredText cText = new CenteredText("Centered Text", 600, 50, 200, 30);
		add(cText);
		ReferenceText rText = new ReferenceText("Reference Text", 600, 90, (c) -> {return c;});
		add(rText);
		
		
		
		TextArea textArea = new TextArea("A large block of text that is displayed on multiple "
				+ "lines almost like a paragraph is placed within a texty area", 600, 130, 120);
		add(textArea);

		
		
		
		VerticalView longView = new VerticalView(0, 0, 250);
		longView.setPadding(10);
		longView.setOptimizedDrawing(true);
		for(int i = 0; i < 100; ++i)
		{
			longView.add(new TextArea("Block " + i + " of text used for a rather long vertical view meant to be placed in a scroll view",
					0, 0, 250));
		}
		ScrollView scrollView = new ScrollView(longView, 300, 40, 170, 200);
		add(scrollView);
		
		
		
		
		
//		EventQueue.invokeLater(new Runnable()
//		{
//			@Override
//			public void run() {
//				try {
//					System.out.println("showing dialog");
//					InputValidator<String> v = (txt) -> txt.length() > 15 ? InputValidator.VALID : "Needs to be longer";
//					String input = ModalManager.instance().showInputDialog("Input!", "Enter something!", v);
//					System.out.println("done showing dialog. Input: " + input);
//				} catch (ModalException e) {
//					e.printStackTrace();
//				}
//			}
//		});

		
	}

}
