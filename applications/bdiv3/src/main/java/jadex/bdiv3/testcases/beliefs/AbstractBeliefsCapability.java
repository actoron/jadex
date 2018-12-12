package jadex.bdiv3.testcases.beliefs;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.base.test.TestReport;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

@Capability
public class AbstractBeliefsCapability
{
	//-------- capabilities --------
	
	@Capability(beliefmapping=@Mapping(value="string2", target="string"))
	protected AbstractBeliefsSubcapability	capa	= new AbstractBeliefsSubcapability();

	//-------- attributes --------
	
	/** The test results. */
	protected Map<String, TestReport>	results;
	
	//-------- beliefs --------
	
	@Belief
	public native byte	getByte();
	@Belief
	public native void	setByte(byte b);
	
	@Belief
	public native short	getShort();
	@Belief
	public native void	setShort(short s);

	@Belief
	public native int	getInt();
	@Belief
	public native void	setInt(int i);

	@Belief
	public native long	getLong();
	@Belief
	public native void	setLong(long l);

	@Belief
	public native float	getFloat();
	@Belief
	public native void	setFloat(float f);
	
	@Belief
	public native double	getDouble();
	@Belief
	public native void	setDouble(double d);

	@Belief
	public native char	getChar();
	@Belief
	public native void	setChar(char c);

	@Belief
	public native boolean	isBoolean();
	@Belief
	public native void	setBoolean(boolean b);

	@Belief
	public native String	getString();
	@Belief
	public native void	setString(String s);

	@Belief
	public native String[]	getArray();
	@Belief
	public native void	setArray(String[] a);
	
	@Belief
	protected String	string2;

	//-------- constructors --------
	
	// Annotation to inform FindBugs that the uninitialized field is not a bug.
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")
	
	@Agent
	protected IInternalAccess	agent;
	
	public AbstractBeliefsCapability()
	{
		results	= new LinkedHashMap<String, TestReport>();
		results.put("byte", new TestReport("#1", "Test abstract byte belief."));
		results.put("short", new TestReport("#2", "Test abstract short belief."));
		results.put("int", new TestReport("#3", "Test abstract int belief."));
		results.put("long", new TestReport("#4", "Test abstract long belief."));
		results.put("float", new TestReport("#5", "Test abstract float belief."));
		results.put("double", new TestReport("#6", "Test abstract double belief."));
		results.put("char", new TestReport("#7", "Test abstract char belief."));
		results.put("boolean", new TestReport("#8", "Test abstract boolean belief."));
		results.put("string", new TestReport("#9", "Test abstract string belief."));
		results.put("array", new TestReport("#10", "Test abstract array belief."));
		
		// todo: agentCreated
		agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				setByte((byte)1);
				setShort((short)1);
				setInt(1);
				setLong(1);
				setFloat(1);
				setDouble(1);
				setChar('a');
				setBoolean(true);
				setString("hello");
				setArray(new String[]{"hello", "world"});
				
				agent.getFeature(IExecutionFeature.class).waitForDelay(300, new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						for(TestReport tr: results.values())
						{
							if(!tr.isFinished())
							{
								tr.setFailed("Plan was not triggered.");
							}
						}
						agent.killComponent();
						return IFuture.DONE;
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
	
	//-------- plans --------
	
	@Plan(trigger=@Trigger(factchanged={"byte", "short", "int", "long", "float", "double", "char", "boolean", "string", "array"}))
	public void	beliefChanged(ChangeEvent event)
	{
		TestReport	tr	= results.get(event.getSource());
		tr.setSucceeded(true);
	}
}
