package net.cloud.server;

import net.cloud.server.command.CommandSuite;
import net.cloud.server.file.FileSuite;
import net.cloud.server.task.TaskSuite;
import net.cloud.server.util.UtilSuite;

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

