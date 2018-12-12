package jadex.extension.envsupport.environment;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  Process for continuously creating objects in the space.
 *  The following properties are supported:
 *  <ul>
 *  <li><code>type</code>: The type of the object to be created (String, required).
 *  <li><code>properties</code>: The initial properties of the object (Map, optional).
 *  <li><code>condition</code>: A condition to enable/disable object creation (boolean, optional).
 *  <li><code>tickrate</code>: Number of ticks between object creation (double, optional, 0 == off).
 *  <li><code>timerate</code>: Number of milliseconds between object creation (double, optional, 0 == off).
 *  </ul>
 *  Properties may be dynamic and refer to the environment space using <code>$space</code>
 *  and to the clock service using <code>$clock</code>.
 */
public class DefaultObjectCreationProcess extends SimplePropertyObject implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The last executed tick. */
	protected double lasttick;

	/** The last executed time. */
	protected double lasttime;

	/** The last rate. */
	protected double lastrate;
	
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

		// Set back counters to trigger immediate object creation at startup.
		if(getProperty("tickrate")!=null)
		{
			this.lastrate	= ((Number)getProperty("tickrate")).doubleValue();
			lasttick	-= lastrate;
		}
		else if(getProperty("timerate")!=null)
		{
			this.lastrate	= ((Number)getProperty("timerate")).doubleValue();
			lasttime	-= lastrate;
		}
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
//			double	rate	= ((Number)getProperty("tickrate")).doubleValue();
			double	current	= clock.getTick();
			while(lastrate>0 && lasttick+lastrate<current)
			{
				Boolean	cond	= (Boolean)getProperty("condition");
				if(cond!=null && !cond.booleanValue())
				{
					lasttick	= clock.getTick();
				}
				else
				{
					lasttick	+= lastrate;
					String	type	= (String)getProperty("type");
					Map	props	= (Map)getProperty("properties");
					props	= props!=null ? new HashMap(props) : null;
					space.createSpaceObject(type, props, null);
//					Object	obj	= space.createSpaceObject(type, props, null);
//					System.out.println("Created: "+obj);
				}
				this.lastrate	= ((Number)getProperty("tickrate")).doubleValue();
			}
		}
	
		if(getProperty("timerate")!=null)
		{
//			double	rate	= ((Number)getProperty("timerate")).doubleValue();
			double	current	= clock.getTime();
			while(lastrate>0 && lasttime+lastrate<current)
			{
				Boolean	cond	= (Boolean)getProperty("condition");
				if(cond!=null && !cond.booleanValue())
				{
					lasttime	= clock.getTime();
				}
				else
				{
					lasttime	+= lastrate;
					String	type	= (String)getProperty("type");
					Map	props	= (Map)getProperty("properties");
					props	= props!=null ? new HashMap(props) : null;
					space.createSpaceObject(type, props, null);
//					Object	obj	= space.createSpaceObject(type, props, null);
//					System.out.println("Created: "+obj);
				}
				this.lastrate	= ((Number)getProperty("timerate")).doubleValue();
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
