package net.cloud.client.file;

import net.cloud.client.file.cache.CacheSuite;
import net.cloud.client.file.XmlRequestTest;

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
