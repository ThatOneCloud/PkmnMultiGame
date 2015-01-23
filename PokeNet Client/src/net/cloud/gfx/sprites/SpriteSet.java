package net.cloud.gfx.sprites;

/**
 * Sprites are categorized into groupings, to simplify obtaining and storing them. 
 * This enum defines the various sets of sprites that are available. 
 * Each set contains a canonical name that can be used to refer to it. 
 */
public enum SpriteSet {
	
	//			Canon Name	Load	Collection Type					Parameters
	
	/** Default set with various and miscellaneous sprites */
	DEFAULT(	"default",	false,	SpriteCollectionType.SIMPLE,	null),
	
	/** Set for images dedicated to the user interface */
	UI(			"ui",		false,	SpriteCollectionType.SIMPLE,	null);
	
	/** Canonical name for general use */
	private String canonName;
	
	/** Whether or not this set wants to be loaded on startup */
	private boolean loadOnStartup;
	
	/** What type of collection this set should have */
	private SpriteCollectionType collectionType;
	
	/** An array of parameters to pass to the collection constructor. May be null if there are none. */
	private Object[] collectionParams;
	
	/** Create a new enum value */
	private SpriteSet(String canonName, boolean loadOnStartup, SpriteCollectionType collectionType, Object[] collectionParams)
	{
		this.canonName = canonName;
		this.loadOnStartup = loadOnStartup;
		this.collectionType = collectionType;
		this.collectionParams = collectionParams;
	}
	
	/**
	 * Obtain a name for the sprite set that can be used generally here and there. 
	 * @return The set's canonical name
	 */
	public String getCanonicalName()
	{
		return canonName;
	}
	
	/**
	 * @return True if this set wants to be loaded completely on startup of the sprite system
	 */
	public boolean loadOnStartup()
	{
		return loadOnStartup;
	}
	
	/**
	 * @return The type of SpriteCollection this particular SpriteSet wants for its sprites
	 */
	public SpriteCollectionType getCollectionType()
	{
		return collectionType;
	}
	
	/**
	 * @return The parameters this set wants to pass to the SpriteCollection constructor
	 */
	public Object[] getCollectionParams()
	{
		return collectionParams;
	}

}