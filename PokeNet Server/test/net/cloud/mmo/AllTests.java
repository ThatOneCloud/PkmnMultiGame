package net.cloud.mmo;

import net.cloud.mmo.command.CommandSuite;
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
	CommandSuite.class,
	FileSuite.class
})
public class AllTests {
	// Nothing goes here. Annotations are all we need
}

