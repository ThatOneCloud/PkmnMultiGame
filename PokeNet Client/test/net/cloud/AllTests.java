package net.cloud;

import net.cloud.client.file.FileSuite;
import net.cloud.client.task.TaskSuite;
import net.cloud.client.util.UtilSuite;
import net.cloud.gfx.GfxSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	UtilSuite.class,
	TaskSuite.class,
	FileSuite.class,
	GfxSuite.class
})
public class AllTests {
	// Nothing goes here. Annotations are all we need
}

