package net.cloud.gfx.sprites;

/**
 * Sprites are categorized into groupings, to simplify obtaining and storing them. 
 * This enum defines the various sets of sprites that are available. 
 * Each set contains a canonical name that can be used to refer to it. 
 */
public enum SpriteSets {
	
	/** Default set with various and miscellaneous sprites */
	DEFAULT("default"),
	
	/** Set for images dedicated to the user interface */
	UI("ui");
	
	/** Canonical name for general use */
	private String canonName;
	
	/** Create a new enum value */
	private SpriteSets(String canonName)
	{
		this.canonName = canonName;
	}
	
	/**
	 * Obtain a name for the sprite set that can be used generally here and there. 
	 * @return The set's canonical name
	 */
	public String getCanonicalName()
	{
		return canonName;
	}

}
