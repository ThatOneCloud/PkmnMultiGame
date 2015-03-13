package net.cloud.server.game;

import static org.junit.Assert.*;
import io.netty.channel.Channel;
import net.cloud.server.entity.player.Player;
import net.cloud.server.entity.player.PlayerFactory;

import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Testing some of the functionality here */
public class WorldPlayerMapTest {
	
	/** An object to run the tests on */
	private static WorldPlayerMap map;
	
	// Consistent access to test data
	private static final Channel ch1 = EasyMock.createMock(Channel.class);
	private static final Channel ch2 = EasyMock.createMock(Channel.class);
	private static final Channel ch3 = EasyMock.createMock(Channel.class);
	
	private static final String user1 = "Alice";
	private static final String user2 = "Bob";
	private static final String user3 = "Charles";
	
	private static final String pass1 = "1234";
	private static final String pass2 = "password";
	private static final String pass3 = "apple";
	
	private static final Player p1 = PlayerFactory.createNewPlayer(user1, pass1);
	private static final Player p2 = PlayerFactory.createNewPlayer(user2, pass2);
	private static final Player p3 = PlayerFactory.createNewPlayer(user3, pass3);
	
	/**
	 * Initialize the map and fill it with dummy players
	 */
	@BeforeClass
	public static void beforeClass()
	{
		map = new WorldPlayerMap();
		
		map.place(ch1, p1);
		map.place(ch2, p2);
		map.place(ch3, p3);
	}

	/**
	 * Test the predicate search to make sure it's turning up results, and stopping when it should
	 */
	@Test
	public void testSearch()
	{
		// Username search with expected result
		assertTrue(map.search((p) -> p.getUsername().equals(user1)) == p1);
		assertTrue(map.search((p) -> p.getUsername().equals(user2)) == p2);
		assertTrue(map.search((p) -> p.getUsername().equals(user3)) == p3);
		
		// Search with no result
		assertNull(map.search((p) -> p.getUsername().equals("not anyones username")));
	}

	/**
	 * Special case of search, just make sure it's interpreted right
	 */
	@Test
	public void testHasMatchingPlayer()
	{
		// Same as testSearch, but using true and false for expected results
		assertTrue(map.hasMatchingPlayer((p) -> p.getUsername().equals(user1)));
		assertTrue(map.hasMatchingPlayer((p) -> p.getUsername().equals(user2)));
		assertTrue(map.hasMatchingPlayer((p) -> p.getUsername().equals(user3)));
		
		assertFalse(map.hasMatchingPlayer((p) -> p.getUsername().equals("not anyones username")));
	}
	
	@AfterClass
	public static void afterClass()
	{
		map.remove(ch1);
		map.remove(ch2);
		map.remove(ch3);
		
		map = null;
	}

}
