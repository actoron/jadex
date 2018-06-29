package jadex.extension.envsupport.evaluation;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.SUtil;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

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
	protected IParsedExpression dataexp;
	
	/** The object expression. */
	protected IParsedExpression includeexp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new row provider.
	 */
	public SpaceObjectSource(String varname, AbstractEnvironmentSpace envspace, 
		String objecttype, boolean aggregate, IParsedExpression dataexp, IParsedExpression includeexp)
	{
		this.varname = varname;
		this.envspace = envspace;
		this.objecttype = objecttype;
		this.aggregate = aggregate;
		this.dataexp = dataexp;
		this.includeexp = includeexp;
	}
	
	/**
	 *  Get the row objects.
	 */
	public List getObjects()
	{
		List ret = new ArrayList();
		
		Object[] obs = envspace.getSpaceObjectsByType(objecttype);
		
		// Replace values with data expression values.
		if(obs!=null)
		{
			if(includeexp!=null)
			{
				SimpleValueFetcher fetcher = new SimpleValueFetcher();
				for(int i=0; i<obs.length; i++)
				{
					fetcher.setValue("$object", obs[i]);
					if(((Boolean)includeexp.getValue(fetcher)).booleanValue())
					{
						if(dataexp!=null)
						{
							ret.add(dataexp.getValue(fetcher));
						}
						else
						{
							ret.add(obs[i]);
						}
					}
				}
			}
			else if(dataexp!=null)
			{
				SimpleValueFetcher fetcher = new SimpleValueFetcher();
				for(int i=0; i<obs.length; i++)
				{
					fetcher.setValue("$object", obs[i]);
					ret.add(dataexp.getValue(fetcher));
				}
			}
			else
			{
				ret = SUtil.arrayToList(obs);
			}
		}
			
		if(aggregate)
		{
			List tmp = ret;
			ret = new ArrayList();
			ret.add(tmp);
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
