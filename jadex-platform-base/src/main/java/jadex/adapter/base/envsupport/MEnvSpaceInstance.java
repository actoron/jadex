package jadex.adapter.base.envsupport;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IPerceptGenerator;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.environment.view.IView;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.adapter.base.envsupport.observer.perspective.IPerspective;
import jadex.bridge.ILibraryService;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Java representation of environemnt space instance for xml description.
 */
public class MEnvSpaceInstance extends MSpaceInstance
{
//-------- attributes --------
	
	/** The properties. */
	protected Map properties;
	
	//-------- methods --------
	
	/**
	 *  Add a property.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public void addProperty(String key, Object value)
	{
		if(properties==null)
			properties = new MultiCollection();
		properties.put(key, value);
	}
	
	/**
	 *  Get a property.
	 *  @param key The key.
	 *  @return The value.
	 */
	public List getPropertyList(String key)
	{
		return properties!=null? (List)properties.get(key):  null;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return properties;
	}

	/**
	 *  Create a space.
	 */
	public ISpace createSpace(ApplicationContext app) throws Exception
	{
		MApplicationType mapt = app.getApplicationType();
		MEnvSpaceType spacetype = (MEnvSpaceType)mapt.getMSpaceType(getTypeName());
		
		// Create and init space.
		AbstractEnvironmentSpace ret = (AbstractEnvironmentSpace)((Class)MEnvSpaceInstance.getProperty(spacetype.getProperties(), "clazz")).newInstance();
		
		ret.setContext(app);
		
		if(getName()!=null)
		{
			ret.setName(getName());
		}
		
		if(ret instanceof Space2D) // Hack?
		{
			IVector2 areasize;
			List dims = spacetype.getPropertyList("dimensions");
			Number dim1 = (Number)dims.get(0);
			Number dim2 = (Number)dims.get(1);
			
			if(dim1 instanceof Integer)
				areasize = new Vector2Double(dim1.doubleValue(), dim2.doubleValue());
			else if(dim2 instanceof Double)
				areasize = new Vector2Int(dim1.intValue(), dim2.intValue());
			else
				throw new RuntimeException("Dimension class not supported: "+dim1);
			
			((Space2D)ret).setAreaSize(areasize);
		}
		
		// Create space actions.
		List spaceactions = spacetype.getPropertyList("spaceactiontypes");
		if(spaceactions!=null)
		{
			for(int i=0; i<spaceactions.size(); i++)
			{
				Map maction = (Map)spaceactions.get(i);
				ISpaceAction action = (ISpaceAction)((Class)MEnvSpaceInstance.getProperty(maction, "clazz")).newInstance();
				
				System.out.println("Adding environment action: "+MEnvSpaceInstance.getProperty(maction, "name"));
				ret.addSpaceAction(MEnvSpaceInstance.getProperty(maction, "name"), action);
			}
		}
		
		// Create agent actions.
		List agentactions = spacetype.getPropertyList("agentactiontypes");
		if(agentactions!=null)
		{
			for(int i=0; i<agentactions.size(); i++)
			{
				Map maction = (Map)agentactions.get(i);
				IAgentAction action = (IAgentAction)((Class)MEnvSpaceInstance.getProperty(maction, "clazz")).newInstance();
				
				System.out.println("Adding environment action: "+MEnvSpaceInstance.getProperty(maction, "name"));
				ret.addAgentAction(MEnvSpaceInstance.getProperty(maction, "name"), action);
			}
		}
		
		// Create processes.
		List processes = spacetype.getPropertyList("processtypes");
		if(processes!=null)
		{
			for(int i=0; i<processes.size(); i++)
			{
				Map mproc = (Map)processes.get(i);
				ISpaceProcess proc = (ISpaceProcess)((Class)MEnvSpaceInstance.getProperty(mproc, "clazz")).newInstance();
				
				System.out.println("Adding environment process: "+MEnvSpaceInstance.getProperty(mproc, "name"));
				ret.addSpaceProcess(MEnvSpaceInstance.getProperty(mproc, "name"), proc);
			}
		}
		
		// Create percept generators.
		List gens = spacetype.getPropertyList("perceptgeneratortypes");
		if(gens!=null)
		{
			for(int i=0; i<gens.size(); i++)
			{
				Map mgen = (Map)gens.get(i);
				IPerceptGenerator gen = (IPerceptGenerator)((Class)MEnvSpaceInstance.getProperty(mgen, "clazz")).newInstance();
				
				System.out.println("Adding environment percept generator: "+MEnvSpaceInstance.getProperty(mgen, "name"));
				ret.addPerceptGenerator(MEnvSpaceInstance.getProperty(mgen, "name"), gen);
			}
		}
		
		// Create initial objects.
		List objects = (List)getPropertyList("objects");
		if(objects!=null)
		{
			for(int i=0; i<objects.size(); i++)
			{
				Map mobj = (Map)objects.get(i);
			
				// Hmm local name as owner? better would be agent id, but agents are created after space?
				ret.createSpaceObject(MEnvSpaceInstance.getProperty(mobj, "type"), MEnvSpaceInstance.getProperty(mobj, "owner"), null, null, null);
			}
		}
		
		// Create initial space actions.
		List actions = (List)getPropertyList("spaceactions");
		if(actions!=null)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$space", ret);
			fetcher.setValue("$platform", app.getPlatform());
			for(int i=0; i<actions.size(); i++)
			{
				Map action = (Map)actions.get(i);
				List ps = (List)action.get("parameters");
				Map params = null;
				if(ps!=null)
				{
					params = new HashMap();
					for(int j=0; j<ps.size(); j++)
					{
						Map param = (Map)ps.get(j);
						// Create (and start) the environment executor.
						IParsedExpression exp = (IParsedExpression)param.get("value");
						params.put(param.get("name"), exp.getValue(fetcher));
					}
				}
				
				System.out.println("Performing initial space action: "+getProperty(action, "type"));
				ret.performSpaceAction(getProperty(action, "type"), params);
			}
		}
		
//		Map themes = new HashMap();
		List sourceviews = spacetype.getPropertyList("views");
		if(sourceviews!=null)
		{
			for(int i=0; i<sourceviews.size(); i++)
			{				
				Map sourceview = (Map)sourceviews.get(i);
				
				Map viewargs = new HashMap();
				viewargs.put("sourceview", sourceview);
				viewargs.put("space", ret);
				
				ret.addView((String)MEnvSpaceInstance.getProperty(sourceview, "name"), (IView)((IObjectCreator)MEnvSpaceInstance.getProperty(sourceview, "creator")).createObject(viewargs));
			}
		}
		
		List sourceobs = getPropertyList("observers");
		if(sourceobs!=null)
		{
			for(int i=0; i<sourceobs.size(); i++)
			{				
				Map sourceob = (Map)sourceobs.get(i);
				
				String title = getProperty(sourceob, "name")!=null? (String)getProperty(sourceob, "name"): "Default Observer";
				// todo: add plugins
				
				ObserverCenter oc = new ObserverCenter(title, ret, (ILibraryService)app.getPlatform().getService(ILibraryService.class), null);
				
				// Hack! Is configuation the presentation?
				// Yes! No, now it's, together with the Theme, the Perspective.
				//Perspective2D perspective = new Perspective2D();
				//perspective.setInvertYAxis(true);
				//perspective.setObjectShift(new Vector2Double(0.5));
				//oc.addPerspective("Simple 2D Space", perspective);
				
				List perspectives = spacetype.getPropertyList("perspectives");
				for(int j=0; j<perspectives.size(); j++)
				{
					Map sourcetheme = (Map)perspectives.get(j);
					oc.addPerspective((String)getProperty(sourcetheme, "name"), (IPerspective)((IObjectCreator)getProperty(sourcetheme, "creator")).createObject(sourcetheme));
				}
			}
		}
		
		// Create (and start) the environment executor.
		IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(spacetype.getProperties(), "spaceexecutor");
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", ret);
		fetcher.setValue("$platform", app.getPlatform());
		exp.getValue(fetcher);	// Executor starts itself
		
		return ret;		
	}

	/**
	 * 
	 */
	public static Object getProperty(Map map, String name)
	{
		Object tmp = map.get(name);
		return (tmp instanceof List)? ((List)tmp).get(0): tmp; 
	}
	
	/**
	 * 
	 */
	public static IVector2 getVector2(Double x, Double y)
	{
		if(x==null || y==null)
			return null;
		return x.doubleValue()==0 && y.doubleValue()==0? Vector2Double.ZERO: new Vector2Double(x.doubleValue(), y.doubleValue());
	}
	
	/**
	 *  Get a string representation of this AGR space instance.
	 *  @return A string representation of this AGR space instance.
	 * /
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(type=");
		sbuf.append(getType());
		if(objects!=null)
		{
			sbuf.append(", objects=");
			sbuf.append(objects);
		}
		sbuf.append(")");
		return sbuf.toString();
	}*/
}
