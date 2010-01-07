package jadex.application.space.envsupport.evaluation;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.commons.SUtil;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 *  A space object source can provide space objects of a specific object type.
 *  If aggregation is used the values are provided as one element (a list).
 */
public class SpaceObjectSource implements IObjectSource
{	
	//-------- attributes --------
	
	/** The variable name. */
	protected String varname;
	
	/** The environment space. */
	protected AbstractEnvironmentSpace envspace;

	/** The objecttype. */
	protected String objecttype;
	
	/** The flag if aggregate values should be returned. */
	protected boolean aggregate;
	
	/** The object expression. */
	protected IParsedExpression exp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new row provider.
	 */
	public SpaceObjectSource(String varname, AbstractEnvironmentSpace envspace, String objecttype, boolean aggregate, IParsedExpression exp)
	{
		this.varname = varname;
		this.envspace = envspace;
		this.objecttype = objecttype;
		this.aggregate = aggregate;
		this.exp = exp;
	}
	
	/**
	 *  Get the row objects.
	 */
	public List getObjects()
	{
		List ret = new ArrayList();
		
		Object[] obs = envspace.getSpaceObjectsByType(objecttype);
		if(obs!=null && exp!=null)
		{
			Object[] tmp = new Object[obs.length];
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			for(int i=0; i<obs.length; i++)
			{
				fetcher.setValue("$object", obs[i]);
				tmp[i] = exp.getValue(fetcher);
			}
			obs = tmp;
		}
		
		if(aggregate)
		{
			ret.add(SUtil.arrayToList(obs));
		}
		else
		{
			ret = SUtil.arrayToList(obs);
		}
		return ret;
	}

	/**
	 *  Get the variable name.
	 *  @return The variable name.
	 */
	public String getSourceName()
	{
		return this.varname;
	}
	
}
