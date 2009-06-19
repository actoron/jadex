package jadex.adapter.base.envsupport.environment;

import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.util.HashMap;
import java.util.Map;

/**
 *  Process for continuously creating objects in the space.
 *  The following properties are supported:
 *  <ul>
 *  <li><code>type</code>: The type of the object to be created (String, required).
 *  <li><code>properties</code>: The initial properties of the object (Map, optional).
 *  <li><code>condition</code>: A condition to enable/disable object creation (boolean, optional).
 *  <li><code>tickrate</code>: Number of ticks between object creation (double, optional, 0 == off).
 *  <li><code>timerate</code>: Number of seconds between object creation (double, optional, 0 == off).
 *  </ul>
 *  Properties may be dynamic and refer to the environment space using <code>$space</code>
 *  and to the clock service using <code>$clock</code>.
 */
public class DefaultObjectCreationProcess extends SimplePropertyObject implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The last executed tick. */
	protected double	lasttick;

	/** The last executed time. */
	protected double	lasttime;

	/** The fetcher. */
	protected SimpleValueFetcher fetcher;

	//-------- constructors --------
	
	/**
	 *  Create a new create food process.
	 */
	public DefaultObjectCreationProcess()
	{		
	}
	
	//-------- ISpaceProcess interface --------
	
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space)
	{
		this.lasttick	= clock.getTick();
		this.lasttime	= clock.getTime();
		this.fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", space);
		fetcher.setValue("$clock", clock);
	}

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		if(getProperty("tickrate")!=null)
		{
			double	rate	= ((Number)getProperty("tickrate")).doubleValue();
			double	current	= clock.getTick();
			while(rate>0 && lasttick+rate<current)
			{
				Boolean	cond	= (Boolean)getProperty("condition");
				if(cond!=null && !cond.booleanValue())
				{
					lasttick	= clock.getTick();
				}
				else
				{
					lasttick	+= rate;
					String	type	= (String)getProperty("type");
					Map	props	= new HashMap((Map)getProperty("properties"));
					Object	obj	= space.createSpaceObject(type, props, null);
//					System.out.println("Created: "+obj);
				}
			}
		}
	
		if(getProperty("timerate")!=null)
		{
			double	rate	= ((Number)getProperty("timerate")).doubleValue()*1000;
			double	current	= clock.getTime();
			while(rate>0 && lasttime+rate<current)
			{
				Boolean	cond	= (Boolean)getProperty("condition");
				if(cond!=null && !cond.booleanValue())
				{
					lasttime	= clock.getTime();
				}
				else
				{
					lasttime	+= rate;
					String	type	= (String)getProperty("type");
					Map	props	= new HashMap((Map)getProperty("properties"));
					Object	obj	= space.createSpaceObject(type, props, null);
//					System.out.println("Created: "+obj);
				}
			}
		}
	}
	
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		Object	ret	= super.getProperty(name);
		if(ret instanceof IParsedExpression)
		{
			ret	= ((IParsedExpression)ret).getValue(fetcher);
		}
		return ret;
	}
}
