package jadex.android.bluetooth;

import jadex.android.bluetooth.shadows.MyShadowBluetoothAdapter;
import jadex.android.bluetooth.shadows.MyShadowLog;
import jadex.commons.SReflect;

import org.junit.Before;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.internal.bytecode.ShadowMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class CustomTestRunner extends RobolectricTestRunner {


	public CustomTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		System.out.println("All LogCat Messages will be printed to the System Console while Testing");
	}

	@Before
	public void setup() {
		new SReflectSub().setIsAndroid(true, true);
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

	private class SReflectSub extends SReflect {
		public void setIsAndroid(Boolean isAndroidFlag, Boolean isAndroidTestingFlag) {
			SReflect.setAndroid(isAndroidFlag, isAndroidTestingFlag);
		}
	}

}
