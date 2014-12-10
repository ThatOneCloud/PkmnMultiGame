package net.cloud.mmo;

import net.cloud.mmo.nio.NettyServer;

/**
 * The entry point for the PokeNet server.
 * Will start the Netty Server and start other necessary initialization tasks.
 */
public class Server {

	public static void main(String[] args) {
		// Start the network server
		try {
			new NettyServer().start();
		} catch (InterruptedException e) {
			System.err.println("Could not start server. Shutting down.");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
