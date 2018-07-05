package jadex.bdiv3.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class AbstractBeliefsBDI
{
	//-------- capabilities --------
	
	@Capability(beliefmapping={@Mapping(value="byteb", target="byte"), @Mapping(value="shortb", target="short"),
		@Mapping(value="intb", target="int"), @Mapping(value="longb", target="long"),
		@Mapping(value="floatb", target="float"), @Mapping(value="doubleb", target="double"),
		@Mapping(value="charb", target="char"), @Mapping(value="booleanb", target="boolean"),
		@Mapping(value="string"), @Mapping("array")})
	protected AbstractBeliefsCapability	capa	= new AbstractBeliefsCapability();
	
	//-------- beliefs --------
	
	@Belief
	protected byte	byteb;

	@Belief
	protected short	shortb;
	
	@Belief
	protected int	intb;
	
	@Belief
	protected long	longb;
	
	@Belief
	protected float	floatb;
	
	@Belief
	protected double	doubleb;
	
	@Belief
	protected char	charb;
	
	@Belief
	protected boolean	booleanb;
	
	@Belief
	protected String	string;
	
	@Belief
	protected String[]	array;

	
	//-------- constructors --------
	
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		TestReport[]	trs	= capa.results.values().toArray(new TestReport[capa.results.size()]);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(trs.length, trs));
	}
}
