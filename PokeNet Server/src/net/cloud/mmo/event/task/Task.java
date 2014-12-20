package net.cloud.mmo.event.task;

@FunctionalInterface
public interface Task<V> {
	
	public V execute();

}
