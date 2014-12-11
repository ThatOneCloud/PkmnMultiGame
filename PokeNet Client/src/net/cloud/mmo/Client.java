package net.cloud.mmo;

import net.cloud.mmo.game.World;
import net.cloud.mmo.nio.NettyClient;

public class Client {

	public static void main(String[] args) {
		// Get the World initialized (rather than lazy initialization)
		World.getInstance();
		
		// Start the NettyClient - which will try to connect to the server
		try {
			new NettyClient().startup();
		} catch (InterruptedException e) {
			System.err.println("Failed to start NettyClient. Exiting.");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
