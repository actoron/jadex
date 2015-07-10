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
	
	/** The belief mappings (abstract/reference name -> concrete belief name). */
	protected Map<String, String> beliefreferences;
	
	//-------- xml only --------
	
	/** The result mappings <belief->result>. */
	// Cached for speed.
	protected Map<String, String> resultmappings;
	
	/** The event mappings (abstract/reference name -> concrete name). */
	protected Map<String, String> eventreferences;
	
	/** The expression mappings (abstract/reference name -> concrete name). */
	protected Map<String, String> expressionreferences;
	
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
	 *  Get the fully qualified belief references (abstract/reference name -> concrete belief name).
	 */
	public Map<String, String> getBeliefReferences()
	{
		Map<String, String>	ret;
		if(beliefreferences==null)
		{
			ret	= Collections.emptyMap();
		}
		else
		{
			ret	= beliefreferences;
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
	 *  Add a belief reference (abstract/reference name -> concrete belief name).
	 *  @param reference The fully qualified abstract / reference belief name. 
	 *  @param concrete The fully qualified concrete belief name.
	 */
	public void addBeliefReference(String reference, String concrete)
	{
		if(beliefreferences==null)
		{
			beliefreferences = new LinkedHashMap<String, String>();
		}
		beliefreferences.put(reference, concrete);
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
	
	/**
	 *  Get the fully qualified expression references (abstract/reference name -> concrete expression name).
	 */
	public Map<String, String> getExpressionReferences()
	{
		Map<String, String>	ret;
		if(expressionreferences==null)
		{
			ret	= Collections.emptyMap();
		}
		else
		{
			ret	= expressionreferences;
		}
		return ret;
	}

	/**
	 *  Add a expression reference (abstract/reference name -> concrete expression name).
	 *  @param reference The fully qualified abstract / reference expression name. 
	 *  @param concrete The fully qualified concrete expression name.
	 */
	public void addExpressionReference(String reference, String concrete)
	{
		if(expressionreferences==null)
		{
			expressionreferences = new LinkedHashMap<String, String>();
		}
		expressionreferences.put(reference, concrete);
	}
	
	/**
	 *  Get the fully qualified event references (abstract/reference name -> concrete event name).
	 */
	public Map<String, String> getEventReferences()
	{
		Map<String, String>	ret;
		if(eventreferences==null)
		{
			ret	= Collections.emptyMap();
		}
		else
		{
			ret	= eventreferences;
		}
		return ret;
	}

	/**
	 *  Add a event reference (abstract/reference name -> concrete event name).
	 *  @param reference The fully qualified abstract / reference event name. 
	 *  @param concrete The fully qualified concrete event name.
	 */
	public void addEventReference(String reference, String concrete)
	{
		if(eventreferences==null)
		{
			eventreferences = new LinkedHashMap<String, String>();
		}
		eventreferences.put(reference, concrete);
	}
}
