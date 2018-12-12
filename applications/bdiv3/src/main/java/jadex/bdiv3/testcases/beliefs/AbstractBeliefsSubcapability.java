package jadex.bdiv3.testcases.beliefs;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;

@Capability
public class AbstractBeliefsSubcapability
{
	//-------- beliefs --------
	
	@Belief
	public native String	getString();
	@Belief
	public native void	setString(String s);
}
