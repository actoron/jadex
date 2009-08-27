package jadex.service;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SJavaParser;

/**
 *  A property service container derives its configuration from
 *  a jadex.commons.properties. The properties object must contain
 *  all services as direct sub property objects.  
 */
public class PropertyServiceContainer extends BasicServiceContainer
{
	/**
	 *  Create a new property service container.
	 */
	public void init(Properties props, IValueFetcher fetcher)
	{
		// Initialize services.
		if(props!=null)
		{
			Property[] services = props.getProperties();
			for(int i=0; i<services.length; i++)
			{
				Class type;
				if(services[i].getType()==null)
					type = IService.class;
				else
					type = SReflect.classForName0(services[i].getType(), null);
				if(type==null)
					throw new RuntimeException("Could not resolve service type: "+services[i].getType());
			
				addService(type, services[i].getName(), (IService)SJavaParser.evaluateExpression(services[i].getValue(), fetcher));
			}
		}
	}
	
}
