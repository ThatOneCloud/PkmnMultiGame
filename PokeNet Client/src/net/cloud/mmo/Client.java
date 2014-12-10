package net.cloud.mmo;

import net.cloud.mmo.nio.NettyClient;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Changed on laptop");
		
		try {
			new NettyClient().startup();
		} catch (InterruptedException e) {
			System.err.println("Failed to start NettyClient. Exiting.");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
