package jadex.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

import jadex.commons.SReflect;

public class CustomTestRunner extends RobolectricTestRunner {

    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 18;

	static {
		try {
			Method setAndroid = SReflect.class.getDeclaredMethod("setAndroid", new Class[]{boolean.class});
			setAndroid.invoke(null, true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

    public CustomTestRunner(Class testClass) throws InitializationError {
//        super(testClass, new File("src/test"));
        super(testClass);
    }

    
    @Override
	protected AndroidManifest getAppManifest(
			org.robolectric.annotation.Config arg0) {
		String manifestProperty = "src/main/AndroidManifest.xml";
		String resProperty = "src/main/res";
		String assProperty = "src/main/assets";
		
		// calls that could potentially fail later:
		FsFile resDir = Fs.fileFromPath(resProperty);
		FsFile assDir = Fs.fileFromPath(assProperty);
		FsFile parent = resDir.getParent();
		FsFile join = parent.join(new String[] { "assets" });
		
		return new AndroidManifest(Fs.fileFromPath(manifestProperty), resDir, assDir) {
			@Override
			public int getTargetSdkVersion() {
				return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
			}
		};
	}

}
