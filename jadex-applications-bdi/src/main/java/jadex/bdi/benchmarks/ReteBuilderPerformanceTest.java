package jadex.bdi.benchmarks;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rete.builder.BuildReport;
import jadex.rules.rulesystem.rete.builder.ReteBuilder;

import java.util.List;

/**
 *  Test the performance of the rete builder.
 */
public class ReteBuilderPerformanceTest
{
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		BuildReport report = new BuildReport();
		
		for(int i=0; i<1000; i++)
		{
			RetePatternMatcherFunctionality pm = new RetePatternMatcherFunctionality(BDIAgentFeature.RULEBASE);
			ReteBuilder builder = pm.getReteNode().getBuilder();
			if(builder!=null && builder.getBuildReport()!=null)
			{
				List newinfos = builder.getBuildReport().getBuildInfos();
				report.getBuildInfos().addAll(newinfos);
			}
		}
		
		System.out.println(report);
	}
}
