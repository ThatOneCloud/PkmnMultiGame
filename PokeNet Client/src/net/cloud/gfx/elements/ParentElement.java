package net.cloud.gfx.elements;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fooled you! This isn't really an element. It does however, contain an element, intended to be the 
 * parent of some other element. Rather than simply have the parent stored as a reference to another 
 * element, this stores that other element along with some functionality (defined and injected by the 
 * parent itself.)  This allows, for example, a child to request that it is removed without such a 
 * request being visible to all outside classes.
 */
public class ParentElement {
	
	/** The parent element. It must exist, will not be null, and may not change */
	private final Element parent;
	
	/** A function which will add a child to the parent */
	private final Optional<Consumer<Element>> addChildFunction;
	
	/** A function which will remove a child from the parent */
	private final Optional<Function<Element, Boolean>> removeChildFunction;
	
	/**
	 * Create a parent element that has no functionality added.
	 * @param parent The parent. May not be null.
	 * @throws IllegalArgumentException If the parent parameter was null
	 */
	public ParentElement(Element parent)
	{
		if(parent == null)
		{
			throw new IllegalArgumentException("Parent may not be null - Element should keep a null parent reference instead");
		}
		
		this.parent = parent;
		
		// Rest of the functions are empty, i.e. undefined
		addChildFunction = Optional.empty();
		removeChildFunction = Optional.empty();
	}
	
	/**
	 * Create a parent element that has as much functionality as desired added. 
	 * Each of the function parameters may be null to indicate it is not supported.
	 * @param parent The parent. May not be null.
	 * @param addChildFunction A consumer function to add the child, in whatever way the parent sees fit, if at all
	 * @param removeChildFunction A consumer function to remove the child, in whatever way the parent sees fit, if at all
	 * @throws IllegalArgumentException If the parent parameter was null
	 */
	public ParentElement(
			Element parent,
			Consumer<Element> addChildFunction,
			Function<Element, Boolean> removeChildFunction)
	{
		if(parent == null)
		{
			throw new IllegalArgumentException("Parent may not be null - Element should keep a null parent reference instead");
		}
		
		this.parent = parent;
		
		// Rest of the functions are possibly empty, possibly defined
		this.addChildFunction = Optional.ofNullable(addChildFunction);
		this.removeChildFunction = Optional.ofNullable(removeChildFunction);
	}
	
	/**
	 * Obtain the parent element contained within this parent element. Will never be null.
	 * @return The element's parent
	 */
	public Element getParent()
	{
		return parent;
	}
	
	/**
	 * Check to see if the addChild method is supported by the parent
	 * @return True if addChild is a supported operation
	 */
	public boolean addChildSupported()
	{
		return addChildFunction.isPresent();
	}
	
	/**
	 * If this operation is supported, the child will be added to its parent, in whatever 
	 * way the parent has defined.
	 * @param child The child element
	 */
	public void addChild(Element child)
	{
		addChildFunction.ifPresent((func) -> func.accept(child));
	}
	
	/**
	 * Check to see if the removeChild method is supported by the parent
	 * @return True if removeChild is a supported operation
	 */
	public boolean removeChildSupported()
	{
		return removeChildFunction.isPresent();
	}
	
	/**
	 * If this operation is supported, the child will be removed from its parent, in whatever 
	 * way the parent has defined. The return value is a boolean result of the remove operation. 
	 * If the operation is not supported, it will always be false, otherwise the result is from the 
	 * parent's operation.
	 * @param child The child element
	 * @return True if the removal returned true, false if removal returned false or is not supported
	 */
	public boolean removeChild(Element child)
	{
		return removeChildSupported() ? removeChildFunction.get().apply(child) : false;
	}

}
