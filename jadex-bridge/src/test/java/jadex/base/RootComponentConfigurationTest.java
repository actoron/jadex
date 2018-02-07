package jadex.base;

public class RootComponentConfigurationTest
{
//	private IRootComponentConfiguration	config;

//	private Map<String, MyGetter>		getters;


//	public RootComponentConfigurationTest()
//	{
//		getters = new HashMap<String, MyGetter>();
//		getters.put(USEPASS, new MyGetter()
//		{
//			public Object get()
//			{
//				return config.getUsePass();
//			}
//		});
//		getters.put(RSPUBLISHCOMPONENT, new MyGetter()
//		{
//			public Object get()
//			{
//				return config.getRsPublishComponent();
//			}
//		});
//	}
//
//	@Before
//	public void setUp()
//	{
//		config = PlatformConfigurationHandler.getDefault().getRootConfig();
//	}
//
//	@Test
//	public void testSimpleOptions()
//	{
//		goodOptions(USEPASS, true, false);
//		badOptions(USEPASS, "abcdef");
//		badValueOptions(USEPASS, "true", "false");
//	}
//
//	@Test
//	public void testKernels()
//	{
//		config.setKernels(new String[0]);
//		shouldFail(KERNELS, "[]");
//
//		config.setKernels(KERNEL_BDI);
//		checkConsistency((IPlatformConfiguration) config);
//	}
//
//	@Test
//	public void testRSpublish()
//	{
//		config.setRsPublish(true);
//		badOptions(RSPUBLISHCOMPONENT, "", null);
//		goodOptions(RSPUBLISHCOMPONENT, "someclass");
//	}
//
//	private void badValueOptions(String field, Object... values)
//	{
//		for(Object val : values)
//		{
//			setAsValueAndExpectError(field, val);
//		}
//	}
//
//	private void badOptions(String field, Object... values)
//	{
//		for(Object val : values)
//		{
//			setAndExpectError(field, val);
//		}
//	}
//
//	private void goodOptions(String field, Object... values)
//	{
//		for(Object val : values)
//		{
//			setAndExpect(field, val, val);
//		}
//	}
//
//	private void setAndExpectError(String field, Object value)
//	{
//		setAsValueAndExpectError(field, value);
//		// cannot pass non-string args via command line:
//		if(value instanceof String && !((String)value).trim().isEmpty())
//		{
//			setAsArgsAndExpectError(field, value);
//		}
//	}
//
//	private void setAsValueAndExpectError(String field, Object value)
//	{
//		// method 1
//		config.setValue(field, value);
//		shouldFail(field, value);
//	}
//
//	private void shouldFail(String field, Object value)
//	{
//		try
//		{
//			checkConsistency((IPlatformConfiguration) config);
//			fail("Exception expected when setting field " + field + " to " + value + " !");
//		}
//		catch(RuntimeException e)
//		{
//		}
//	}
//
////	private void setAsArgsAndExpectError(String field, Object value)
////	{
////		// method 2
////		enhanceWith(config, Starter.processArgs("-" + field + " " + value).getRootConfig());
////		shouldFail(field, value);
////	}
//
//	private void setAndExpect(String field, Object value, Object expected)
//	{
//		// method 1
//		config.setValue(field, value);
//		checkConsistency((IPlatformConfiguration) config);
//		Object result = getters.get(field).get();
//		assertEquals(expected, result);
//
//		// method 2
//		enhanceWith(config, Starter.processArgs("-" + field + " " + value).getRootConfig());
//		checkConsistency((IPlatformConfiguration) config);
//		result = getters.get(field).get();
//		assertEquals(expected, result);
//	}
//
//	private abstract static class MyGetter
//	{
//		abstract public Object get();
//	}
//
//	/**
//	 * Checks this config for consistency.
//	 */
//	protected void checkConsistency(IPlatformConfiguration config)
//	{
//		StringBuilder errorText = new StringBuilder();
//		Object publish = config.getValue(RSPUBLISH);
//		Object publishComponent = config.getValue(RSPUBLISHCOMPONENT);
//		
//		if(Boolean.TRUE.equals(publish) && (publishComponent == null || "".equals(publishComponent)))
//		{
//			errorText.append(RSPUBLISH + " set to true, but no " + RSPUBLISHCOMPONENT + " found.");
//		}
//
//		Object kernels = config.getValue(KERNELS); // may need to get value from model
//		if (kernels == null || ((String[]) kernels).length==0) {
//			errorText.append("No Kernels set. Cannot start platform.");
//		}
//
//		for (String argName:BOOLEAN_ARGS) {
//			if (!isBoolean(config.getArgs().get(argName))) {
//				errorText.append(USEPASS + " must be a boolean value (or null), but is set to: " + config.getValue(USEPASS));
//			}
//		}
//
//		if (config.getRelayTransport() && SReflect.classForName0("jadex.platform.service.message.relaytransport.RelayTransportAgent",  this.getClass().getClassLoader()) == null) {
//			errorText.append(RELAYTRANSPORT + " set to true, but 'jadex.platform.service.message.relaytransport.RelayTransportAgent' is not in classpath (maybe include module jadex-platform-extension-relaytransport in dependencies?).\n");
//		}
//
//		if (config.getWsTransport() && SReflect.classForName0("jadex.platform.service.message.websockettransport.WebSocketTransportAgent",  this.getClass().getClassLoader()) == null) {
//			errorText.append(WSTRANSPORT + " set to true, but 'jadex.platform.service.message.websockettransport.WebSocketTransportAgent' is not in classpath (maybe include module jadex-platform-extension-websockettransport in dependencies?).\n");
//		}
//
//		if(errorText.length() != 0)
//		{
//			throw new RuntimeException("Configuration consistency error: \n" + errorText.toString());
//		}
//	}
//
//	/**
//	 * Check whether value can be converted to boolean or not.
//	 * @param value
//	 * @return
//	 */
//	private boolean isBoolean(Object value) {
//		boolean result = false;
//		if (value != null) {
//			if (value instanceof Boolean) {
//				result = true;
//			}
//		} else {
//			result = true;
//		}
//		return result;
//	}
//	
//	/**
//	 * Enhance this config with given other config. Will overwrite all values
//	 * that are set in the other config.
//	 * 
//	 * @param other
//	 */
//	public void enhanceWith(IRootComponentConfiguration config, IRootComponentConfiguration other)
//	{
//		for(Map.Entry<String, Object> entry : other.getArgs().entrySet())
//		{
//			config.setValue(entry.getKey(), entry.getValue());
//		}
//	}

}
