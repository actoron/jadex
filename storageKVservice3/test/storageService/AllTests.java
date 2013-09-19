package storageService;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DB_DerbyConnectTest.class, VectorClockTest.class,
		VectorTimeTest.class, StorageAgentTest.class })
public class AllTests {

}
