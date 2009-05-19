package jadex.adapter.base.envsupport;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IPerceptGenerator;
import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.adapter.base.envsupport.observer.perspective.IPerspective;
import jadex.adapter.base.envsupport.observer.perspective.Perspective2D;
import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ILibraryService;
import jadex.commons.IPropertyObject;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
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
		
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", ret);
		fetcher.setValue("$platform", app.getPlatform());
		
		ret.setContext(app);
		
		if(getName()!=null)
		{
			ret.setName(getName());
		}
		
		if(ret instanceof Space2D) // Hack?
		{
			Double width = getProperty(properties, "width")!=null? (Double)getProperty(properties, "width"): (Double)getProperty(spacetype.getProperties(), "width");
			Double height = getProperty(properties, "height")!=null? (Double)getProperty(properties, "height"): (Double)getProperty(spacetype.getProperties(), "height");
			
			((Space2D)ret).setAreaSize(Vector2Double.getVector2(width, height));
		}
		
		// Create space object types.
		List objecttypes = spacetype.getPropertyList("objecttypes");
		if(objecttypes!=null)
		{
			for(int i=0; i<objecttypes.size(); i++)
			{
				Map mobjecttype = (Map)objecttypes.get(i);
				List props = (List)mobjecttype.get("properties");
				Map properties = null;
				
				if(props!=null)
				{
					properties = new HashMap();
					for(int j=0; j<props.size(); j++)
					{
						Map prop = (Map)props.get(j);
						IParsedExpression exp = (IParsedExpression)prop.get("value");
						boolean dyn = ((Boolean)prop.get("dynamic")).booleanValue();
						if(dyn)
							properties.put((String)prop.get("name"), exp);
						else
							properties.put((String)prop.get("name"), exp.getValue(fetcher));
					}
				}
				
				System.out.println("Adding environment object type: "+(String)getProperty(mobjecttype, "name"));
				ret.addSpaceObjectType((String)getProperty(mobjecttype, "name"), properties);
			}
		}
		
		// Add avatar mappings.
		List avmappings = spacetype.getPropertyList("avatarmappings");
		if(avmappings!=null)
		{
			for(int i=0; i<avmappings.size(); i++)
			{
				Map mapping = (Map)avmappings.get(i);
				ret.addAvatarMappings((String)MEnvSpaceInstance.getProperty(mapping, "agenttype"), 
					(String)MEnvSpaceInstance.getProperty(mapping, "objecttype"));
			}
		}
		
		// Create space actions.
		List spaceactions = spacetype.getPropertyList("spaceactiontypes");
		if(spaceactions!=null)
		{
			for(int i=0; i<spaceactions.size(); i++)
			{
				Map maction = (Map)spaceactions.get(i);
				ISpaceAction action = (ISpaceAction)((Class)MEnvSpaceInstance.getProperty(maction, "clazz")).newInstance();
				List props = (List)maction.get("properties");
				setProperties(action, props, fetcher);
				
//				System.out.println("Adding environment action: "+MEnvSpaceInstance.getProperty(maction, "name"));
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
				List props = (List)maction.get("properties");
				setProperties(action, props, fetcher);
				
//				System.out.println("Adding environment action: "+MEnvSpaceInstance.getProperty(maction, "name"));
				ret.addAgentAction(MEnvSpaceInstance.getProperty(maction, "name"), action);
			}
		}
		
		// Create processes.
		List processes = spacetype.getPropertyList("processtypes");
		if(processes!=null)
		{
			for(int i=0; i<processes.size(); i++)
			{
				Map mprocess = (Map)processes.get(i);
				ISpaceProcess process = (ISpaceProcess)((Class)MEnvSpaceInstance.getProperty(mprocess, "clazz")).newInstance();
				List props = (List)mprocess.get("properties");
				setProperties(process, props, fetcher);
				
//				System.out.println("Adding environment process: "+MEnvSpaceInstance.getProperty(mprocess, "name"));
				ret.addSpaceProcess(MEnvSpaceInstance.getProperty(mprocess, "name"), process);
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
				List props = (List)mgen.get("properties");
				setProperties(gen, props, fetcher);
				
//				System.out.println("Adding environment percept generator: "+MEnvSpaceInstance.getProperty(mgen, "name"));
				ret.addPerceptGenerator(MEnvSpaceInstance.getProperty(mgen, "name"), gen);
			}
		}
		
		// Create percept mappings.
		List pmaps = spacetype.getPropertyList("perceptmappings");
		if(pmaps!=null)
		{
			for(int i=0; i<pmaps.size(); i++)
			{
				Map mgen = (Map)pmaps.get(i);
				IPerceptProcessor proc = (IPerceptProcessor)((Class)MEnvSpaceInstance.getProperty(mgen, "clazz")).newInstance();
				ret.addPerceptMapping((String)MEnvSpaceInstance.getProperty(mgen, "agenttype"), proc);
			}
		}

		// Create initial objects.
		List objects = (List)getPropertyList("objects");
		if(objects!=null)
		{
			for(int i=0; i<objects.size(); i++)
			{
				Map mobj = (Map)objects.get(i);
			
				// todo: support static objecttype declarartions
				
				List mprops = (List)mobj.get("properties");
				Map props = null;
				if(mprops!=null)
				{
					props = new HashMap();
					for(int j=0; j<mprops.size(); j++)
					{
						Map prop = (Map)mprops.get(j);
						IParsedExpression exp = (IParsedExpression)prop.get("value");
						boolean dyn = ((Boolean)prop.get("dynamic")).booleanValue();
						if(dyn)
							props.put((String)prop.get("name"), exp);
						else
							props.put((String)prop.get("name"), exp.getValue(fetcher));
					}
				}
				String	owner	= (String)MEnvSpaceInstance.getProperty(mobj, "owner");
				if(owner!=null)
				{
					IAgentIdentifier	ownerid;
					IAMS	ams	= ((IAMS)app.getPlatform().getService(IAMS.class));
					if(owner.indexOf("@")!=-1)
						ownerid	= ams.createAgentIdentifier((String)owner, false);
					else
						ownerid	= ams.createAgentIdentifier((String)owner, true);
					if(props==null)
						props	= new HashMap();
					props.put(ISpaceObject.PROPERTY_OWNER, ownerid);
				}
				
				ret.createSpaceObject((String)MEnvSpaceInstance.getProperty(mobj, "type"), props, null, null);
			}
		}
		
		// Create initial space actions.
		List actions = (List)getPropertyList("spaceactions");
		if(actions!=null)
		{
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
						IParsedExpression exp = (IParsedExpression)param.get("value");
						params.put(param.get("name"), exp.getValue(fetcher));
					}
				}
				
//				System.out.println("Performing initial space action: "+getProperty(action, "type"));
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
				if(MEnvSpaceInstance.getProperty(sourceview, "objecttype")==null)
				{
					Map viewargs = new HashMap();
					viewargs.put("sourceview", sourceview);
					viewargs.put("space", ret);
					
					IDataView	view	= (IDataView)((IObjectCreator)MEnvSpaceInstance.getProperty(sourceview, "creator")).createObject(viewargs);
					ret.addDataView((String)MEnvSpaceInstance.getProperty(sourceview, "name"), view);
				}
				else
				{
					ret.addDataViewMapping((String)MEnvSpaceInstance.getProperty(sourceview, "objecttype"), sourceview);
				}
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
					Map sourcepers = (Map)perspectives.get(j);
					Map args = new HashMap();
					args.put("object", sourcepers);
					args.put("fetcher", fetcher);
					IPerspective	persp	= (IPerspective)((IObjectCreator)getProperty(sourcepers, "creator")).createObject(args);
					// TODO: Add attributes
					if(ret.getClass().getName().indexOf("2D")!=-1)
						((Perspective2D)persp).setInvertYAxis(true);
					if(ret.getClass().getName().indexOf("Grid")!=-1)
						((Perspective2D)persp).setObjectShift(new Vector2Double(0.5));
					oc.addPerspective((String)getProperty(sourcepers, "name"), persp);
				}
			}
		}
		
		// Create the environment executor.
		IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(spacetype.getProperties(), "spaceexecutor");
		if(exp!=null)
		{
			exp.getValue(fetcher);	// Executor starts itself
		}
		
		return ret;		
	}

	/**
	 *  Get a property from a (multi)map.
	 *  @param map The map.
	 *  @param name The name.
	 *  @return The property.
	 */
	public static Object getProperty(Map map, String name)
	{
		Object tmp = map.get(name);
		return (tmp instanceof List)? ((List)tmp).get(0): tmp; 
	}
	
	/**
	 *  Set properties on a IPropertyObject.
	 *  @param object The IPropertyObject.
	 *  @param properties A list properties (containing maps with "name", "value" keys).
	 *  @param fetcher The fetcher for parsing the Java expression (can provide
	 *  predefined values to the expression)
	 */
	public static void setProperties(IPropertyObject object, List properties, IValueFetcher fetcher)
	{
		if(properties!=null)
		{
			for(int i=0; i<properties.size(); i++)
			{
				Map prop = (Map)properties.get(i);
				IParsedExpression exp = (IParsedExpression)prop.get("value");
				boolean dyn = ((Boolean)prop.get("dynamic")).booleanValue();
				if(dyn)
					object.setProperty((String)prop.get("name"), exp);
				else
					object.setProperty((String)prop.get("name"), exp.getValue(fetcher));
			}
		}
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
