package jadex.android.bluetooth;

import jadex.android.bluetooth.shadows.MyShadowBluetoothAdapter;
import jadex.android.bluetooth.shadows.MyShadowLog;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class CustomTestRunner extends RobolectricTestRunner {

	public CustomTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		System.out.println("All LogCat Messages will be printed to the System Console while Testing");
	}
	
	@Override
	protected void bindShadowClasses() {
		Robolectric.bindShadowClass(MyShadowBluetoothAdapter.class);
		Robolectric.bindShadowClass(MyShadowLog.class);
		
	}

}
