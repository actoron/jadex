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
public class PropertyServiceContainer extends HierarchicalServiceContainer
{
	/**
	 *  Create a new property service container.
	 * /
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
	}*/
	
	/**
	 *  Create a new property service container.
	 */
	public void init(Properties[] props, IValueFetcher fetcher, IServiceContainer parent)
	{
		super.init(parent);
		
		// Initialize services.
		if(props!=null)
		{
			for(int i=0; i<props.length; i++)
			{
				Property[] services = props[i].getProperties();
				for(int j=0; j<services.length; j++)
				{
					Class type;
					if(services[j].getType()==null)
						type = IService.class;
					else
						type = SReflect.classForName0(services[j].getType(), null);
					if(type==null)
						throw new RuntimeException("Could not resolve service type: "+services[j].getType());
				
					// Try fetching an existing service by name/type.
					if(parent==null || parent.getService(type, services[j].getName())==null)
						addService(type, services[j].getName(), (IService)SJavaParser.evaluateExpression(services[j].getValue(), fetcher));
				
					if(parent!=null && parent.getService(type, services[j].getName())!=null)
						System.out.println("Reusing service: "+type+" "+services[j].getName());
				}	
			}
		}
	}
	
}
