package net.cloud.server.game.action;

import java.io.PrintWriter;

import net.cloud.server.groovy.GroovyObjectLoader;

/**
 * An interface more for symmetry than anything. 
 * All ActionHandlers will be stored separate, since their handle methods may well have different 
 * signatures, and that's just fine. However, all handlers will need to be able to load the actions 
 * they take care of, so that's here.
 * 
 * @param <A> Type of the action ID
 */
public interface ActionHandler<A extends ActionEnum> {
	
	/**
	 * Load all of the actions the handler is responsible for. 
	 * This will re-load them if they have already been loaded. 
	 * This will continue even if a single action fails to load, instead concatenating the exception messages 
	 * and throwing one at the end of loading.
	 * @param out A PrintWriter to write output progress messages to
	 * @param loader Used to obtain instances of the actions
	 * @throws If one or more of the actions could not be loaded
	 */
	public void loadAllActions(PrintWriter out, GroovyObjectLoader loader) throws Exception;
	
	/**
	 * Load only a single action this handler is responsible for. 
	 * If it has already been loaded, then it will be reloaded.
	 * @param out A PrintWriter to write output progress messages to
	 * @param loader Used to obtain instances of the actions
	 * @param id The ID of the action to load
	 * @throws If the action could not be loaded for some reason
	 */
	public void loadAction(PrintWriter out, GroovyObjectLoader loader, A id) throws Exception;

}
