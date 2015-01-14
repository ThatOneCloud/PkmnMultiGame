package net.cloud.mmo.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BoundedCircularIntArrayTest.class,
	ReverseIteratorTest.class,
	StringUtilTest.class,
	StrongIteratorTest.class
})
public class UtilSuite {

}
