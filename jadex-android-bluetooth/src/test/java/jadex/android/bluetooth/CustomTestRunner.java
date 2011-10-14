package jadex.android.bluetooth;

import jadex.android.bluetooth.routing.MyShadowBluetoothAdapter;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class CustomTestRunner extends RobolectricTestRunner {

	public CustomTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}
	
	@Override
	protected void bindShadowClasses() {
		Robolectric.bindShadowClass(MyShadowBluetoothAdapter.class);
	}

}
