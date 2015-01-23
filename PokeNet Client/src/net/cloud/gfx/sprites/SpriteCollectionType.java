package net.cloud.gfx.sprites;

/**
 * An enumerable for the various types of SpriteCollection objects. 
 * Since I'd rather have the collection being held somewhere not in the SpriteSet enum, 
 * but yet have the SpriteSet enum define what kind of collection to use. 
 */
public enum SpriteCollectionType {
	
	/** {@link SimpleSpriteCollection} */
	SIMPLE,
	
	/** {@link BlockSpriteCollection} */
	BLOCK;

}
