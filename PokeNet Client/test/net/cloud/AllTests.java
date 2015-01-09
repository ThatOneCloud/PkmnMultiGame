package net.cloud;

import net.cloud.gfx.GfxSuite;
import net.cloud.mmo.file.FileSuite;
import net.cloud.mmo.task.TaskSuite;
import net.cloud.mmo.util.UtilSuite;

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

