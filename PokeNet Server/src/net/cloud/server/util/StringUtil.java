package net.cloud.server.util;

import java.nio.ByteBuffer;
import java.text.ParseException;

import io.netty.buffer.ByteBuf;

/**
 * Contains, as the name implies, String utility functions.
 */
public class StringUtil {
	
	/** The line terminator character */
	public static final char TERMINATOR = 0;
	
	/**
	 * Formats a string of text to be upper case letter first, and then lower case. 
	 * The upper case will be put for the first letter after any spaces, too. 
	 * Also, underscores are replaced with spaces.<br>
	 * This is the inverse of <code>allCaps(text)</code>
	 * @param text The text to format
	 * @return A formatted string
	 */
	public static String upperFirst(String text) {
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
	public static String allCaps(String text) {
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
	public static String getFromBuffer(ByteBuffer buffer) {
		StringBuilder builder = new StringBuilder();
		char c = (char) buffer.get();
		
		while(c != TERMINATOR) {
			builder.append(c);
			c = (char) buffer.get();
		}
		
		return builder.toString();
	}
	
	/** A variant that uses the Netty ByteBuf rather than the NIO ByteBuffer */
	public static String getFromBuffer(ByteBuf buffer) {
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
	public static void writeStringToBuffer(String string, ByteBuffer buffer) {
		for(int i = 0; i < string.length(); ++i) {
			buffer.put((byte) string.charAt(i));
		}
		
		buffer.put((byte) TERMINATOR);
	}
	
	/** Uses a Netty ByteBuf rather than NIO's ByteBuffer */
	public static void writeStringToBuffer(String string, ByteBuf buffer) {
		for(int i = 0; i < string.length(); ++i) {
			buffer.writeByte((byte) string.charAt(i));
		}
		
		buffer.writeByte((byte) TERMINATOR);
	}
	
	/**
	 * @return The number of bytes this string will occupy when written out
	 */
	public static int getNumBytesInString(String string) {
		return string.length() + 1;
	}
	
	/**
	 * Parses a hexadecimal literal string into an array of bytes.  <br>
	 * Ex: "1F2A" = {31, 42}
	 * @param hex The hexadecimal literal (Must be of even length)
	 * @return An array of bytes, matching the hex value.
	 */
	public static byte[] getBytesFromHex(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		
		for(int i = 0; i < bytes.length; ++i) {
			bytes[i] = Byte.parseByte(hex.substring(i*2, i*2 + 2), 16);
		}
		
		return bytes;
	}
	
	/**
	 * Figure out what the next token is in a command string, and return it. 
	 * The token is removed from the StringBuilder, so the first character, if any remain, 
	 * will be the one immediately following the token.  Leading whitespace is removed.<br>
	 * The token is defined to be either the block of characters between the beginning 
	 * of the string and the next space, or the block of characters between a set of 
	 * quotation marks.<br>
	 * Ex: <code>' word' returns 'word'</code>, and 
	 * <code> '"multiple words here"' returns 'multiple words here'</code>
	 * @param sb The StringBuilder containing the string to parse
	 * @return The next token in the string, as defined.
	 * @throws ParseException If the StringBuilder contained no token, or quotations were mismatched
	 */
	public static String extractCommandToken(StringBuilder sb) throws ParseException
	{
		// A null object would be bad.
		if(sb == null)
		{
			throw new ParseException("Null StringBuilder", -1);
		}
		
		// Trim spaces from beginning
		trimLeadingSpaces(sb);
		
		// Make sure there are still some characters to work on
		if(sb.length() == 0)
		{
			throw new ParseException("Command is empty", -1);
		}
				
		// Decide if the end of the token will be a space or a quotation mark
		char endOfToken = sb.charAt(0) == '"' ? '"' : ' ';
		
		// Now either way, we'll need to know where to stop
		int endIndex = sb.length();
		
		// A space is what will divide this token
		if(endOfToken == ' ')
		{
			// So look for the next space in the string
			int spaceIndex = sb.indexOf(" ");
			
			// If it came back -1, it wasn't in there. Otherwise, the space is our cutoff
			endIndex = (spaceIndex == -1) ? sb.length() : spaceIndex;
		}
		// A pair of quotations is what will divide the token
		else
		{
			// Well, we know a quotation was up front. Remove that one to start
			sb.deleteCharAt(0);
			
			// Now look for the next quotation mark - the matching one
			int endQuoteIndex = sb.indexOf("\"");
			
			// See if the ending quotation mark was found
			if(endQuoteIndex == -1)
			{
				// Nope. Mismatched. Unacceptable formatting
				throw new ParseException("No matching set of quotation marks", sb.length()-1);
			} else {
				// Yup. So it becomes the end index
				endIndex = endQuoteIndex;
			}
		}
		
		// So now the token extends from the beginning to the endIndex
		String token = sb.substring(0, endIndex);
		
		// We'll trim off what we're returning right here - since we know the index
		sb.delete(0, endIndex+1);
		
		// And trim off any leading spaces that have been introduced
		trimLeadingSpaces(sb);
		
		// And return the token that we found
		return token;
	}
	
	/**
	 * Remove spaces from the beginning of the StringBuilder
	 * @param sb The StringBuilder to trim
	 */
	public static void trimLeadingSpaces(StringBuilder sb)
	{
		// A null object would be bad.
		if(sb == null)
		{
			return;
		}

		// Trim spaces from beginning
		while(sb.length() > 0 && sb.charAt(0) == ' ')
		{
			sb.deleteCharAt(0);
		}
	}

}
