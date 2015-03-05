package net.cloud.client.util.function;

/**
 * A functional interface to validate some input. (Go figure) 
 * The single method validate will be called when validation needs to take place. 
 * The result will then determine whether or not the provided data is valid.
 * 
 * @param <T> The type of the input data to check
 */
@FunctionalInterface
public interface InputValidator<T> {
	
	/**
	 * Constant that may be used to check if the result is valid. It's just a null pointer, but may serve for clarity purposes.
	 */
	public static String VALID = null;
	
	/**
	 * Check to see if the data is valid. This can happen however you would like. 
	 * The returned result then should be a short string explaining why the data is invalid, 
	 * or null to indicate that the data is indeed valid. 
	 * @param data The input data to check
	 * @return A reason the data is invalid, or null for valid. (May use InputValidator.VALID)
	 */
	public String validate(T data);

}
