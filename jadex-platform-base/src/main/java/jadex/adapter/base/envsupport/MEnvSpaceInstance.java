package jadex.adapter.base.envsupport;

import jadex.adapter.base.agr.MGroupInstance;
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
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.drawable.IDrawable;
import jadex.adapter.base.envsupport.observer.graphics.drawable.TexturedRectangle;
import jadex.adapter.base.envsupport.observer.graphics.layer.GridLayer;
import jadex.adapter.base.envsupport.observer.graphics.layer.TiledLayer;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.adapter.base.envsupport.observer.gui.presentation.IPresentation;
import jadex.adapter.base.envsupport.observer.gui.presentation.Presentation2D;
import jadex.adapter.base.envsupport.observer.theme.Theme2D;
import jadex.bridge.ILibraryService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		ClassLoader cl = ((ILibraryService)app.getPlatform().getService(ILibraryService.class)).getClassLoader();
		
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
				Object obj = ret.createSpaceObject(MEnvSpaceInstance.getProperty(mobj, "type"), MEnvSpaceInstance.getProperty(mobj, "owner"), null, null, null);
			}
		}
		
		Map themes = new HashMap();
		List sourceviews = spacetype.getPropertyList("views");
		if(sourceviews!=null)
		{
			for(int i=0; i<sourceviews.size(); i++)
			{				
				Map sourceview = (Map)sourceviews.get(i);
				
				Map viewargs = new HashMap();
				viewargs.put("sourceview", sourceview);
				viewargs.put("themes", themes);
				viewargs.put("space", ret);
				
				ret.addView((String)MEnvSpaceInstance.getProperty(sourceview, "name"), (IView)((IObjectCreator)MEnvSpaceInstance.getProperty(sourceview, "creator")).createObject(viewargs));
			}
			
			ObserverCenter oc = new ObserverCenter("Default Window Title", ret, (ILibraryService)app.getPlatform().getService(ILibraryService.class), null);
			
			// Hack! Is configuation the presentation?
			// Yes!
			Presentation2D presentation = new Presentation2D();
			presentation.setInvertYAxis(true);
			presentation.setObjectShift(new Vector2Double(0.5));
			oc.addPresentation("Simple 2D Space", presentation);
			
			for (Iterator it = themes.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry entry = (Entry) it.next();
				oc.addTheme((String) entry.getKey(), entry.getValue());
			}
		}
		
		// Create (and start) the environment executor.
		IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(spacetype.getProperties(), "spaceexecutor");
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", ret);
		fetcher.setValue("$platform", app.getPlatform());
		Object spaceexe = exp.getValue(fetcher);
		
		return ret;		
		
//		Map args = new HashMap();
//		args.put("application", app);
//		args.put("spacetype", spacetype);
//		args.put("spaceinstance", this);
//
//		return ret;
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
			return Vector2Double.ZERO;
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
