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
import jadex.adapter.base.envsupport.observer.graphics.drawable.TexturedRectangle;
import jadex.adapter.base.envsupport.observer.graphics.layer.GridLayer;
import jadex.adapter.base.envsupport.observer.graphics.layer.TiledLayer;
import jadex.adapter.base.envsupport.observer.gui.Configuration;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.bridge.ILibraryService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.util.ArrayList;
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

	//-------- attributes --------
	
	/** The environment objects. */
//	protected List objects;

	//-------- methods --------
	
	/**
	 *  Get the objects of this space.
	 *  @return An array of objects (if any).
	 * /
	public MEnvObject[] getMEnvObjects()
	{
		return objects==null? null:
			(MEnvObject[])objects.toArray(new MGroupInstance[objects.size()]);
	}*/

	/**
	 *  Add an object to this space.
	 *  @param object The object to add. 
	 * /
	public void addMEnvObject(MEnvObject object)
	{
		if(objects==null)
			objects	= new ArrayList();
		objects.add(object);
	}*/
	
	/**
	 *  Create a space.
	 */
	public ISpace createSpace(ApplicationContext app) throws Exception
	{
		MApplicationType mapt = app.getApplicationType();
		MEnvSpaceType spacetype = (MEnvSpaceType)mapt.getMSpaceType(getTypeName());
		ClassLoader cl = ((ILibraryService)app.getPlatform().getService(ILibraryService.class)).getClassLoader();
		
		// Create and init space.
		AbstractEnvironmentSpace ret = (AbstractEnvironmentSpace)((Class)getProperty(spacetype.getProperties(), "clazz")).newInstance();
		
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
				ISpaceAction action = (ISpaceAction)((Class)getProperty(maction, "clazz")).newInstance();
				
				System.out.println("Adding environment action: "+getProperty(maction, "name"));
				ret.addSpaceAction(getProperty(maction, "name"), action);
			}
		}
		
		// Create agent actions.
		List agentactions = spacetype.getPropertyList("agentactiontypes");
		if(agentactions!=null)
		{
			for(int i=0; i<agentactions.size(); i++)
			{
				Map maction = (Map)agentactions.get(i);
				IAgentAction action = (IAgentAction)((Class)getProperty(maction, "clazz")).newInstance();
				
				System.out.println("Adding environment action: "+getProperty(maction, "name"));
				ret.addAgentAction(getProperty(maction, "name"), action);
			}
		}
		
		// Create processes.
		List processes = spacetype.getPropertyList("processtypes");
		if(processes!=null)
		{
			for(int i=0; i<processes.size(); i++)
			{
				Map mproc = (Map)processes.get(i);
				ISpaceProcess proc = (ISpaceProcess)((Class)getProperty(mproc, "clazz")).newInstance();
				
				System.out.println("Adding environment process: "+getProperty(mproc, "name"));
				ret.addSpaceProcess(getProperty(mproc, "name"), proc);
			}
		}
		
		// Create percept generators.
		List gens = spacetype.getPropertyList("perceptgeneratortypes");
		if(gens!=null)
		{
			for(int i=0; i<gens.size(); i++)
			{
				Map mgen = (Map)gens.get(i);
				IPerceptGenerator gen = (IPerceptGenerator)((Class)getProperty(mgen, "clazz")).newInstance();
				
				System.out.println("Adding environment percept generator: "+getProperty(mgen, "name"));
				ret.addPerceptGenerator(getProperty(mgen, "name"), gen);
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
				Object obj = ret.createSpaceObject(getProperty(mobj, "type"), getProperty(mobj, "owner"), null, null, null);
			}
		}
		
		// Hack! Is configuation the presentation?
		Configuration cfg = new Configuration();
		cfg.setInvertYAxis(true);
		cfg.setObjectShift(new Vector2Double(0.5));
		
		List sourceviews = spacetype.getPropertyList("views");
		if(sourceviews!=null)
		{
			for(int i=0; i<sourceviews.size(); i++)
			{				
				Map sourceview = (Map)sourceviews.get(i);
				
				List sourcethemes = (List)sourceview.get("themes");
				if(sourcethemes!=null)
				{
					for(int j=0; j<sourcethemes.size(); j++)
					{
						Map sourcetheme = (Map)sourcethemes.get(j);
						Map targettheme = new HashMap();
						
						cfg.setTheme((String)getProperty(sourcetheme, "name"), targettheme);
						
						List drawables = (List)sourcetheme.get("drawables");
						if(drawables!=null)
						{
							for(int k=0; k<drawables.size(); k++)
							{
								Map sourcedrawable = (Map)drawables.get(k);
								IVector2 size = getVector2((Double)getProperty(sourcedrawable, "width"), (Double)getProperty(sourcedrawable, "height"));
								DrawableCombiner targetdrawable = new DrawableCombiner(size);
								targettheme.put(getProperty(sourcedrawable, "objecttype"), targetdrawable);
								
								List parts = (List)sourcedrawable.get("parts");
								if(parts!=null)
								{
									for(int l=0; l<parts.size(); l++)
									{
										Map sourcepart = (Map)parts.get(l);
										// todo
										size = getVector2((Double)getProperty(sourcepart, "width"), (Double)getProperty(sourcepart, "height"));
										IVector2 shift = getVector2((Double)getProperty(sourcepart, "shiftx"), (Double)getProperty(sourcepart, "shifty"));
										boolean rotating = getProperty(sourcepart, "rotating")==null? false: ((Boolean)getProperty(sourcepart, "rotating")).booleanValue();
										TexturedRectangle targetpart = new TexturedRectangle(size, shift, rotating, (String)getProperty(sourcepart, "imagepath"));
										targetdrawable.addDrawable(targetpart);
									}
								}
							}
						}
						
						List prelayers = (List)sourcetheme.get("prelayers");
						if(prelayers!=null)
						{
							List targetprelayers = new ArrayList();
							targettheme.put("prelayers", targetprelayers);
							for(int k=0; k<prelayers.size(); k++)
							{
								Object tmp = prelayers.get(k);
								System.out.println("prelayer: "+tmp);
//								if(tmp instanceof MEnvGridLayer)
//								{
//									MEnvGridLayer sourceprelayer = (MEnvGridLayer)tmp;
//									GridLayer targetprelayer = new GridLayer(sourceprelayer.getSize(), sourceprelayer.getColor());
//									targetprelayers.add(targetprelayer);
//								}
//								else if(tmp instanceof MEnvTiledLayer)
//								{
//									MEnvTiledLayer sourceprelayer = (MEnvTiledLayer)tmp;
//									TiledLayer targetprelayer = new TiledLayer(sourceprelayer.getSize(), sourceprelayer.getImagePath());
//									targetprelayers.add(targetprelayer);
//								}
							}
						}
						
						List postlayers = (List)sourcetheme.get("postlayers");
						if(prelayers!=null)
						{
							List targetprelayers = new ArrayList();
							targettheme.put("postlayers", targetprelayers);
							for(int k=0; k<prelayers.size(); k++)
							{
								Object tmp = prelayers.get(k);
								System.out.println("postlayer: "+tmp);
							}
						}
					}
				}
				
				IView targetview = (IView)((Class)getProperty(sourceview, "clazz")).newInstance();
				targetview.setSpace(ret);
				ret.addView((String)getProperty(sourceview, "name"), targetview);
			}

			ObserverCenter oc = new ObserverCenter(ret, cfg, (ILibraryService)app.getPlatform().getService(ILibraryService.class));
		}


		
		/*Map theme = new HashMap();
		
		DrawableCombiner combiner = new DrawableCombiner();
		combiner.addDrawable(new RegularPolygon(new Vector2Double(2.0), new Vector2Double(0.0), false, new Color(1.0f, 1.0f, 0.0f, 0.5f), 24), -1);
		combiner.addDrawable(new Triangle(new Vector2Double(1.0), new Vector2Double(0.0), true, Color.BLUE), 0);
		theme.put("collector", combiner);

		DrawableCombiner burner = new DrawableCombiner();
		combiner.addDrawable(new RegularPolygon(new Vector2Double(2.0), new Vector2Double(0.0), false, new Color(1.0f, 1.0f, 0.0f, 0.5f), 24), -1);
		combiner.addDrawable(new Triangle(new Vector2Double(1.0), new Vector2Double(0.0), true, Color.BLUE), 0);
		theme.put("burner", combiner);

		combiner = new DrawableCombiner(new Vector2Double(0.5));
		combiner.addDrawable(new RegularPolygon(new Vector2Double(0.5), new Vector2Double(0.0), false, Color.RED, 24));
		theme.put("garbage", combiner);
		
		ILayer grid = new GridLayer(new Vector2Double(1.0), Color.WHITE);
		List prelayers = new ArrayList();
		prelayers.add(grid);
		
		theme.put("prelayers", prelayers);
		
		cfg.setTheme("abstract", theme);
		
		cfg.setInvertYAxis(true);
		cfg.setObjectShift(new Vector2Double(0.5));
		
		ObserverCenter oc = new ObserverCenter(ret, cfg, (ILibraryService)app.getPlatform().getService(ILibraryService.class));
		ret.addView(GeneralView2D.class.getName(), new GeneralView2D((Space2D)ret));*/
		
		
		// Create (and start) the environment executor.
		IParsedExpression exp = (IParsedExpression)getProperty(spacetype.getProperties(), "spaceexecutor");
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", ret);
		fetcher.setValue("$platform", app.getPlatform());
		Object spaceexe = exp.getValue(fetcher);
		
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
