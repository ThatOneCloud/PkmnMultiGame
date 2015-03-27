package net.cloud.client.util;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import io.netty.buffer.ByteBuf;

/**
 * Contains, as the name implies, String utility functions.
 */
public class StringUtil {
	
	/** The line terminator character */
	public static final char TERMINATOR = 0;
	
	/** Re-usable pattern for alphanumeric regular expressions */
	private static final Pattern ALPHA_NUMERIC = Pattern.compile("[a-zA-Z0-9_]+");
	
	/** Re-usable pattern for alphanumeric and most of the number row special chars */
	private static final Pattern ALPHA_NUMERIC_SPECIAL = Pattern.compile("[a-zA-Z0-9_!@#$%^&*]+");
	
	/**
	 * Takes the value and returns a formatted string for the value, without tons of digits showing. 
	 * @param value The decimal value
	 * @param maxDigits The maximum number of decimal digits that will show
	 * @return A cleaned up formatted string
	 */
	public static String cleanDecimal(double value, int maxDigits)
	{
		// Create a new formatter. Guess it could be re-used, but would need to be thread safe
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(maxDigits);
		
		return formatter.format(value);
	}
	
	/**
	 * Formats a string of text to be upper case letter first, and then lower case. 
	 * The upper case will be put for the first letter after any spaces, too. 
	 * Also, underscores are replaced with spaces.<br>
	 * This is the inverse of <code>allCaps(text)</code>
	 * @param text The text to format
	 * @return A formatted string
	 */
	public static String upperFirst(String text)
	{
		String newText = text.replaceAll("_", " ");
		newText = newText.toLowerCase();
		StringBuilder builder = new StringBuilder(newText);
		
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
		int spaceIdx = builder.indexOf(" ", 1);
		while(spaceIdx != -1) {
			if(Character.isLowerCase(builder.charAt(spaceIdx+1))) {
				builder.setCharAt(spaceIdx+1, Character.toUpperCase(builder.charAt(spaceIdx+1)));
			}
			spaceIdx = builder.indexOf(" ", spaceIdx+1);
		}
		
		return builder.toString();
	}
	
	/**
	 * Format a string of text into the all capitals format.  
	 * This means all characters are uppercase, and spaces are represented 
	 * as underscores.<br>
	 * This is the inverse of <code>upperFirst(text)</code>
	 * @param text The text to format
	 * @return A formatted string
	 */
	public static String allCaps(String text)
	{
		String newText = text.replaceAll(" ", "_");
		newText = newText.toUpperCase();
		
		return newText;
	}
	
	/**
	 * Get a terminated String from a ByteBuffer.  This will move the pos in the buffer.
	 * The string terminator is the TERMINATOR constant character.
	 * @param buffer The Buffer to read from.
	 * @return The String of chars from the start pos to the pos of the first TERMINATOR char.
	 */
	public static String getFromBuffer(ByteBuffer buffer)
	{
		StringBuilder builder = new StringBuilder();
		char c = (char) buffer.get();
		
		while(c != TERMINATOR) {
			builder.append(c);
			c = (char) buffer.get();
		}
		
		return builder.toString();
	}
	
	/** 
	 * A variant that uses the Netty ByteBuf rather than the NIO ByteBuffer
	 * @param buffer The buffer to read the string from
	 * @return A string from the data in the buffer
	 */
	public static String getFromBuffer(ByteBuf buffer)
	{
		StringBuilder builder = new StringBuilder();
		char c = (char) buffer.readByte();
		
		while(c != TERMINATOR) {
			builder.append(c);
			c = (char) buffer.readByte();
		}
		
		return builder.toString();
	}
	
	/**
	 * Write a String to a ByteBuffer, terminating it with TERMINATOR.  
	 * This can be in turn read back with <code>StringUtil.getFromBuffer(ByteBuffer)</code>
	 * @param string The text to write
	 * @param buffer The buffer to write to.
	 */
	public static void writeStringToBuffer(String string, ByteBuffer buffer)
	{
		for(int i = 0; i < string.length(); ++i) {
			buffer.put((byte) string.charAt(i));
		}
		
		buffer.put((byte) TERMINATOR);
	}
	
	/**
	 * Write a String to a ByteBuf, terminating it with TERMINATOR.  
	 * This can be in turn read back with <code>StringUtil.getFromBuffer(ByteBuffer)</code>
	 * @param string The text to write
	 * @param buffer The buffer to write to.
	 */
	public static void writeStringToBuffer(String string, ByteBuf buffer)
	{
		for(int i = 0; i < string.length(); ++i) {
			buffer.writeByte((byte) string.charAt(i));
		}
		
		buffer.writeByte((byte) TERMINATOR);
	}
	
	/**
	 * @param string The string
	 * @return The number of bytes this string will occupy when written out
	 */
	public static int getNumBytesInString(String string)
	{
		return string.length() + 1;
	}
	
	/**
	 * Parses a hexadecimal literal string into an array of bytes.  <br>
	 * Ex: "1F2A" = {31, 42}
	 * @param hex The hexadecimal literal (Must be of even length)
	 * @return An array of bytes, matching the hex value.
	 */
	public static byte[] getBytesFromHex(String hex)
	{
		byte[] bytes = new byte[hex.length() / 2];
		
		for(int i = 0; i < bytes.length; ++i) {
			bytes[i] = Byte.parseByte(hex.substring(i*2, i*2 + 2), 16);
		}
		
		return bytes;
	}
	
	/**
	 * Check to see if a string contains only alphabetical, numerical, or underscore characters. 
	 * @param s The string to test
	 * @return True if the string matches
	 */
	public static boolean isAlphaNumeric(String s)
	{
		return ALPHA_NUMERIC.matcher(s).matches();
	}
	
	/**
	 * Check to see if a string contains only alphabetical, numerical, underscore, 
	 * or the special characters from the number keys. 
	 * @param s The string to test
	 * @return True if the string matches
	 */
	public static boolean isAlphaNumericSpecial(String s)
	{
		return ALPHA_NUMERIC_SPECIAL.matcher(s).matches();
	}

}
