package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.Starter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.standalone.ComponentAdapterFactory;

public class Startup {
	
	public static final String platformconfig = "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml";
//	public static final String platformconfig = "jadex/standalone/Platform.component.xml";
	public static final String configname = "android_fixed";
	public static final String configname_bluetooth = "android_bluetooth";
	
	public static void main(String[] args) {
		startBluetoothPlatform("java");
	}

	public static void micro_test() {
		Starter.main(new String[] {
				"-conf",
				platformconfig,
				"-configname", configname, 
				"-platformname", "testcases",
				"-saveonexit", "false", 
				"-gui", "false", 
				"-component", "jadex/micro/benchmarks/AgentCreationAgent.class" });
	}
	
	public static void bpmn_test() {
		Starter.main(new String[] {
				"-conf", platformconfig,
				"-configname", configname,
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-gui", "false",
				"-component", "jadex/bpmn/benchmarks/AgentCreation.bpmn"
		});
	}
	
	public static void bdi_test() {
		Starter.main(new String[] {
				"-conf", platformconfig,
				"-configname", configname,
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-gui", "false",
				"-component", "jadex/bdi/benchmarks/AgentCreation.agent.xml"
		});
	}
	
	public static IFuture startComponent(String component) {
		return Starter.createPlatform(new String[] {
				"-conf", platformconfig,
				"-configname", configname,
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-gui", "false",
				"-awareness", "false",
				"-component", component
		});
	}
	
	public static IFuture startEmptyPlatform() {
		IFuture future = Starter.createPlatform(new String[] {
				"-conf", platformconfig,
				"-configname", configname,
				"-platformname", "mobile",
				"-saveonexit", "false",
				"-gui", "false",
				"-awareness", "true"
		});
		return future;
	}
	
	public static IFuture startBluetoothPlatform(String platformname) {
		IFuture future = Starter.createPlatform(new String[] {
				"-conf", platformconfig,
				"-configname", configname_bluetooth,
				"-platformname", platformname,
				"-saveonexit", "false",
				"-gui", "false",
				"-awareness", "true",
		});
		return future;
	}
	
}
