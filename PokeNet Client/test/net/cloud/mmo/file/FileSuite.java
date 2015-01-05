package net.cloud.mmo.file;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	FileServerTest.class,
	AddressBuilderTest.class,
	RequestHandlerTest.class
})
public class FileSuite {

}
