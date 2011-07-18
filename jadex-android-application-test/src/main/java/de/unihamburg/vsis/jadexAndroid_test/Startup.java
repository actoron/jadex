package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.Starter;
import jadex.commons.future.IFuture;
import jadex.standalone.ComponentAdapterFactory;

public class Startup {
	public static void main(String[] args) {
		createPlatform();
	}

	public static void createPlatform() {
		Starter.main(new String[] {
				"-conf",
				 "de/unihamburg/vsis/jadexAndroid_test/Platform.component.xml",
				"-configname", "android_fixed", 
				"-platformname", "testcases",
				"-saveonexit", "false", 
				"-gui", "false", 
				"-component", "jadex/micro/benchmarks/AgentCreationAgent.class" });
	}
}
