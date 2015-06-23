package jadex.bdiv3x;

import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MCapability;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Model for BDI v3x agents
 */
public class BDIXModel	extends ModelInfo implements IBDIModel
{
	//-------- attributes --------
	
	/** The capability. */
	protected MCapability	capa;
	
	/** The belief mappings. */
	protected Map<String, String> beliefmappings;
	
	/** The result mappings <belief->result>. */
	// Cached for speed.
	protected Map<String, String> resultmappings;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model.
	 */
	public BDIXModel()
	{
		this.capa	= new MCapability();
	}
	
	//-------- IBDIModel interface --------
	
	/**
	 *  Get the component model.
	 */
	public IModelInfo	getModelInfo()
	{
		return this;
	}
	
	/**
	 *  Get the capability.
	 */
	public MCapability	getCapability()
	{
		return capa;
	}
	
	/**
	 *  Get the belief mappings (target->source).
	 */
	public Map<String, String> getBeliefMappings()
	{
		Map<String, String>	ret;
		if(beliefmappings==null)
		{
			ret	= Collections.emptyMap();
		}
		else
		{
			ret	= beliefmappings;
		}
		return ret;
	}
	
	/**
	 *  Overridden to avoid null pointer when getRawModel() is used.
	 */
	public Object getRawModel()
	{
		return this;
	}
	
	//-------- internal methods --------
	
	/**
	 *  Add a belief mapping.
	 *  @param target The target belief in the subcapability. 
	 *  @param source The source belief.
	 */
	public void addBeliefMapping(String target, String source)
	{
		if(beliefmappings==null)
		{
			beliefmappings = new LinkedHashMap<String, String>();
		}
		beliefmappings.put(target, source);
	}
	
	/**
	 *  Get the result mappings (belief->result).
	 */
	public Map<String, String> getResultMappings()
	{
		Map<String, String>	ret;
		if(resultmappings==null)
		{
			ret	= Collections.emptyMap();
		}
		else
		{
			ret	= resultmappings;
		}
		return ret;
	}
	
	/**
	 *  Add a result mapping.
	 *  @param belief The belief name (fully qualified). 
	 *  @param result The result name.
	 */
	public void addResultMapping(String belief, String result)
	{
		if(resultmappings==null)
		{
			resultmappings = new LinkedHashMap<String, String>();
		}
		resultmappings.put(belief, result);
	}
}
