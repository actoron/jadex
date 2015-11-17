package jadex.android.commons;


import jadex.base.RootComponentConfiguration;

public interface JadexPlatformOptions
{
	public static final String KERNEL_COMPONENT = RootComponentConfiguration.KERNEL.component.name();
	public static final String KERNEL_MICRO = RootComponentConfiguration.KERNEL.micro.name();
	public static final String KERNEL_BPMN = RootComponentConfiguration.KERNEL.bpmn.name();
	public static final String KERNEL_BDIV3 = RootComponentConfiguration.KERNEL.v3.name();
	public static final String KERNEL_BDI = RootComponentConfiguration.KERNEL.bdi.name();
//	public static final String KERNEL_BDIBPMN = "bdibpmn";
	
	public static final String[] DEFAULT_KERNELS = new String[]
			{ KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN, KERNEL_BDIV3};
	
	public static final String[] ALL_KERNELS = new String[]
			{ KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN, KERNEL_BDIV3};
}
