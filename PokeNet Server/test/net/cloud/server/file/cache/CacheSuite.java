package net.cloud.server.file.cache;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	CachedFileTest.class,
	CachedFileRegionTest.class,
	CacheTableTest.class
})
public class CacheSuite {

}
