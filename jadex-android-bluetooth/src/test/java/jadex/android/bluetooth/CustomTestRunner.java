package jadex.android.bluetooth;

import jadex.android.bluetooth.shadows.MyShadowBluetoothAdapter;
import jadex.android.bluetooth.shadows.MyShadowLog;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.ShadowMap;


public class CustomTestRunner extends RobolectricTestRunner {

	public CustomTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		System.out.println("All LogCat Messages will be printed to the System Console while Testing");
	}
	
	
	@Override
	protected ShadowMap createShadowMap() {
		ShadowMap createShadowMap = super.createShadowMap();
		createShadowMap = createShadowMap.newBuilder()
			.addShadowClass(MyShadowBluetoothAdapter.class)
			.addShadowClass(MyShadowLog.class)
			.build();
		return createShadowMap;
	}


//	@Override
//	protected void bindShadowClasses() {
//		Robolectric.bindShadowClass(MyShadowBluetoothAdapter.class);
//		Robolectric.bindShadowClass(MyShadowLog.class);
//		
//	}

}
