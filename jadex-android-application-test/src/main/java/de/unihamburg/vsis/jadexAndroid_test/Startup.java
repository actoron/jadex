package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.Starter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.standalone.ComponentAdapterFactory;

public class Startup {
	public static void main(String[] args) {
		bdi_test();
	}

	public static void micro_test() {
		Starter.main(new String[] {
				"-conf",
				 "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml",
				"-configname", "android_fixed", 
				"-platformname", "testcases",
				"-saveonexit", "false", 
				"-gui", "false", 
				"-component", "jadex/micro/benchmarks/AgentCreationAgent.class" });
	}
	
	public static void bpmn_test() {
		Starter.main(new String[] {
				"-conf", "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml",
				"-configname", "android_fixed",
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-gui", "false",
				"-component", "jadex/bpmn/benchmarks/AgentCreation.bpmn"
		});
	}
	
	public static void bdi_test() {
		Starter.main(new String[] {
				"-conf", "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml",
				"-configname", "allkernels_fixed",
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-gui", "false",
				"-component", "jadex/bdi/benchmarks/AgentCreation.agent.xml"
		});
	}
	
	public static IFuture startComponent(String component) {
		return Starter.createPlatform(new String[] {
				"-conf", "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml",
				"-configname", "android_fixed",
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-gui", "false",
				"-component", component
		});
	}
	
	public static IFuture startEmptyPlatform() {
		IFuture future = Starter.createPlatform(new String[] {
				"-conf", "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml",
				"-configname", "android_fixed",
				"-platformname", "testcases",
				"-saveonexit", "false",
				"-gui", "false"
		});
		return future;
	}
	
	
}
