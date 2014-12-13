package net.cloud.mmo.event.task;

public interface TriFunction<T, U, V, R> {
	
	public R apply(T arg1, U arg2, V arg3);

}
