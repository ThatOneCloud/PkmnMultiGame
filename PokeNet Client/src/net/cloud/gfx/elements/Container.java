package net.cloud.gfx.elements;

/**
 * Interface for Container-like elements. These are elements which compose themselves of other 
 * elements, freely allowing those other elements to be added and removed. 
 * Some type restriction may be placed, so that only certain types may be added. Most often 
 * this will just be Element, though.
 * 
 * @param <T> Restrictive type on what kinds of Elements may be contained
 */
public interface Container<T extends Element> {

	/**
	 * Add an element to this container. Thread-safe. 
	 * Assigns the parent of the element to be this container. 
	 * @param newChild The element to add to this container
	 */
	public abstract void add(T newChild);

	/**
	 * Remove the given element from this container. 
	 * The parent of the element is reset to null, regardless of result. 
	 * @param child The element to remove from this Container
	 * @return True if the element was indeed removed
	 */
	public abstract boolean remove(T child);

	/**
	 * Remove all of the children from this Container. 
	 * Each of the children has its parent set to null. 
	 */
	public abstract void removeAllChildren();

}