package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

public class Startup {
	
//	public static final String platformconfig = "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml";
	public static final String platformconfig = "jadex/standalone/Platform.component.xml";
//	public static final String configname = "android_fixed";
//	public static final String configname_bluetooth = "android_bluetooth";
	
	public static void main(String[] args) {
		//startBluetoothPlatform("java");
	}

	public static void micro_test() {
		Starter.main(new String[] {
				"-conf",
				platformconfig,
				"-kernels", "\"component, micro\"",
				"-platformname", "testcases",
				"-saveonexit", "false", 
				"-android", "true",
				"-component", "jadex/micro/benchmarks/AgentCreationAgent.class" });
	}
	
//	public static void bpmn_test() {
//		Starter.main(new String[] {
//				"-conf", platformconfig,
//				"-kernels", "\"component, micro, bpmn\"",
//				"-platformname", "testcases",
//				"-saveonexit", "false",
//				"-android", "true",
//				"-component", "jadex/bpmn/benchmarks/AgentCreation.bpmn"
//		});
//	}
//	
//	public static void bdi_test() {
//		Starter.main(new String[] {
//				"-conf", platformconfig,
//				"-kernels", "\"component, micro, bdi\"",
//				"-platformname", "testcases",
//				"-saveonexit", "false",
//				"-android", "true",
//				"-component", "jadex/bdi/benchmarks/AgentCreation.agent.xml"
//		});
//	}
	
	public static IFuture<IExternalAccess> startComponent(String component) {
		return Starter.createPlatform(new String[] {
				"-conf", platformconfig,
				"-kernels", "\"component, micro, bpmn, bdi\"",
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-awareness", "false",
				"-android", "true",
				"-component", component
		});
	}
	
	public static IFuture<IExternalAccess> startEmptyPlatform() {
		IFuture<IExternalAccess> future = Starter.createPlatform(new String[] {
				"-conf", platformconfig,
				"-kernels", "\"component, micro\"",
				"-platformname", "mobile",
				"-saveonexit", "false",
				"-awareness", "false",
				"-android", "true",
		});
		return future;
	}
	
	public static IFuture<IExternalAccess> startBluetoothPlatform(String platformname) {
		IFuture<IExternalAccess> future = Starter.createPlatform(new String[] {
				"-conf", platformconfig,
				"-kernels", "\"component, micro\"",
				"-platformname", platformname,
				"-saveonexit", "false",
				"-awareness", "true",
				"-awamechanisms", "new String[]{\"Broadcast\", \"Bluetooth\"}",
				"-android", "true",
				
		});
		return future;
	}
	
}
