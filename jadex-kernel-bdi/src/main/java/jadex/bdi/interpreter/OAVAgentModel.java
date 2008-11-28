package jadex.bdi.interpreter;

import jadex.rules.rulesystem.IPatternMatcherFunctionality;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;

import java.util.Set;


/**
 *  The agent model contains the OAV agent model in a state and
 *  a type-specific compiled rulebase (matcher functionality).
 */
public class OAVAgentModel	extends OAVCapabilityModel
{
	//-------- attributes --------

	/** The compiled rulebase of the agent (including additional capability rules). */
	protected IPatternMatcherFunctionality matcherfunc;
	
	//-------- constructors --------
	
	/**
	 *  Create a model for later copying content into.
	 */
	protected OAVAgentModel()
	{
		super();
	}
	
	/**
	 *  Create a model.
	 */
	public OAVAgentModel(IOAVState state, Object handle, 
		OAVTypeModel typemodel, Set types)
	{
		super(state, handle, typemodel, types);
	}
	
	//-------- IAgentModel methods --------
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return true;
	}
	
	/**
	 *  Get the model type.
	 *  @reeturn The model type (kernel specific).
	 */
	public String getType()
	{
		// todo: 
		return "v2bdiagent";
	}


	/**
	 *  Get the matcherfunc.
	 *  @return The matcherfunc.
	 */
	public IPatternMatcherFunctionality getMatcherFunctionality()
	{
		return matcherfunc;
	}
		
	/**
	 *  Get the matcherfunc.
	 *  @return The matcherfunc.
	 */
	protected void setMatcherFunctionality(IPatternMatcherFunctionality matcherfunc)
	{
		this.matcherfunc	= matcherfunc;
	}
	
	/**
	 *  Copy content from another capability model.
	 * /
	protected void	copyContentFrom(OAVCapabilityModel model)
	{
		super.copyContentFrom(model);
		this.matcherfunc	= ((OAVAgentModel)model).getMatcherFunctionality();		
	}*/
	
	//-------- methods --------
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name);
		return "OAVAgentModel("+name+")";
	}
}
