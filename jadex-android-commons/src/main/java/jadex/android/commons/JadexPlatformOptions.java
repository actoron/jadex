package jadex.android.commons;


public interface JadexPlatformOptions
{
	public static final String KERNEL_COMPONENT = "component";
	public static final String KERNEL_MICRO = "micro";
	public static final String KERNEL_BPMN = "bpmn";
	public static final String KERNEL_BDIV3 = "v3";
	public static final String KERNEL_BDI = "bdi";
//	public static final String KERNEL_BDIBPMN = "bdibpmn";
	
	public static final String[] DEFAULT_KERNELS = new String[]
			{ KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN, KERNEL_BDIV3};
	
	public static final String[] ALL_KERNELS = new String[]
			{ KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN, KERNEL_BDIV3};
}
