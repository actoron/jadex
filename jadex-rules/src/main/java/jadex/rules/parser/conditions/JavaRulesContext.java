package jadex.rules.parser.conditions;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.state.OAVTypeModel;

/**
 * 
 */
public class JavaRulesContext
{
	/** The type model. */
	public OAVTypeModel tmodel;

	/** The object conditions. */
	public List conditions;
	
	/** The constraints. */
	public List constraints;
	
	
	/** The current element(s). */
	public List current;
	
	/**
	 * 
	 */
	public JavaRulesContext(OAVTypeModel tmodel)
	{
		this.tmodel = tmodel;
		this.current = new ArrayList();
	}
	
}
