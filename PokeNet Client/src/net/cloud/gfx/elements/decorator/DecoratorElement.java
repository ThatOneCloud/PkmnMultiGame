package net.cloud.gfx.elements.decorator;

import net.cloud.gfx.elements.Element;

/**
 * An interface for elements that add functionality to another element via the decorator pattern. 
 * Each of these elements treat themselves and the wrapped element almost as one - maintaining outside 
 * functionality similar to if the element was not wrapped. There may be slight changes, to make behavior appropriate. 
 * The interface defines a method, getDecoratedElement(). This is so that it remains possible to 
 * unwrap the element and modify it without having each and every reference along the way. 
 */
public interface DecoratorElement extends Element {
	
	/**
	 * Obtain the element that is wrapped within this decorator. This makes it possible to unwrap all 
	 * of the decorates down to the core element. The value is a generic type so that type casts are 
	 * not always necessary. Of course, if the type parameter is a wildcard then that's just how it goes.
	 * @return The wrapped element
	 */
	public Element getDecoratedElement();
	
	/**
	 * Yes all objects have this so it's sort of a moot-point. Well, decorators treat themselves as the wrapped 
	 * element, so they're equal to an object if their underlying element is equal to it.
	 * @param obj The object to compare to
	 * @return True if the decorator's underlying element is equal to the object
	 */
	public boolean equals(Object obj);

}
