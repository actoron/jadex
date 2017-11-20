package jadex.android.commons;


import jadex.base.IRootComponentConfiguration;

public interface JadexPlatformOptions
{

	public static final String[] DEFAULT_KERNELS = new String[]
			{ IRootComponentConfiguration.KERNEL_COMPONENT,
					IRootComponentConfiguration.KERNEL_MICRO,
					IRootComponentConfiguration.KERNEL_BPMN,
					IRootComponentConfiguration.KERNEL_BDIV3};
	
	public static final String[] ALL_KERNELS = new String[]
			{ IRootComponentConfiguration.KERNEL_COMPONENT,
					IRootComponentConfiguration.KERNEL_MICRO,
					IRootComponentConfiguration.KERNEL_BPMN,
					IRootComponentConfiguration.KERNEL_BDIV3};
}
