package jadex.android;

import java.io.File;

import org.junit.runners.model.InitializationError;

import android.app.Application;

import com.xtremelabs.robolectric.RobolectricTestRunner;

public class CustomTestRunner extends RobolectricTestRunner {
    public CustomTestRunner(Class testClass) throws InitializationError {
        super(testClass, new File("src/test"));
    }

}
