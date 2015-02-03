package net.cloud.server.file;

import net.cloud.server.file.cache.CacheSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	FileServerTest.class,
	AddressBuilderTest.class,
	RequestHandlerTest.class,
	XmlRequestTest.class,
	
	CacheSuite.class
})
public class FileSuite {

}
