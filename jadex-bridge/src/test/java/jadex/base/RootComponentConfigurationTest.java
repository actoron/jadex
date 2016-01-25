package jadex.base;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import jadex.base.RootComponentConfiguration;
import static jadex.base.RootComponentConfiguration.*;

import static org.junit.Assert.*;
public class RootComponentConfigurationTest {

    private RootComponentConfiguration config;

    private Map<String, MyGetter> getters;


    public RootComponentConfigurationTest() {
        getters = new HashMap<String, MyGetter>();
        getters.put(USEPASS, new MyGetter() {
            public Object get() {
                return config.getUsePass();
            }
        });
        getters.put(RSPUBLISHCOMPONENT, new MyGetter() {
            public Object get() {
                return config.getRsPublishComponent();
            }
        });
    }

    @Before
    public void setUp() {
        config = PlatformConfiguration.getDefault().getRootConfig();
    }

    @Test
    public void testSimpleOptions() {
        goodOptions(USEPASS, true, false);
        badOptions(USEPASS, "abcdef");
        badValueOptions(USEPASS, "true", "false");
    }

    @Test
    public void testKernels() {
        config.setKernels(new KERNEL[0]);
        shouldFail(KERNELS, "[]");

        config.setKernels(KERNEL.bdi);
        config.checkConsistency();
    }

    @Test
    public void testRSpublish() {
        config.setRsPublish(true);
        badOptions(RSPUBLISHCOMPONENT, "", null);
        goodOptions(RSPUBLISHCOMPONENT, "someclass");
    }

    private void badValueOptions(String field, Object... values) {
        for (Object val: values) {
            setAsValueAndExpectError(field, val);
        }
    }

    private void badOptions(String field, Object... values) {
        for (Object val: values) {
            setAndExpectError(field, val);
        }
    }

    private void goodOptions(String field, Object... values) {
        for (Object val: values) {
            setAndExpect(field, val, val);
        }
    }

    private void setAndExpectError(String field, Object value) {
        setAsValueAndExpectError(field, value);
        // cannot pass non-string args via command line:
        if (value instanceof String && !((String) value).trim().isEmpty()) {
            setAsArgsAndExpectError(field, value);
        }
    }

    private void setAsValueAndExpectError(String field, Object value) {
        // method 1
        config.setValue(field, value);
        shouldFail(field, value);
    }

    private void shouldFail(String field, Object value) {
        try {
            config.checkConsistency();
            fail("Exception expected when setting field " + field + " to " + value +" !");
        } catch (RuntimeException e) {
        }
    }

    private void setAsArgsAndExpectError(String field, Object value) {
        // method 2
        config.enhanceWith(PlatformConfiguration.processArgs("-" + field + " " + value).getRootConfig());
        shouldFail(field, value);
    }

    private void setAndExpect(String field, Object value, Object expected) {
        // method 1
        config.setValue(field, value);
        config.checkConsistency();
        Object result = getters.get(field).get();
        assertEquals(expected, result);

        // method 2
        config.enhanceWith(PlatformConfiguration.processArgs("-" + field + " " + value).getRootConfig());
        config.checkConsistency();
        result = getters.get(field).get();
        assertEquals(expected, result);
    }

    private abstract static class MyGetter {
        abstract public Object get();
    }
}
