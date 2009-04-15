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
import jadex.adapter.base.envsupport.environment.RoundBasedExecutor;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.environment.view.GeneralView2D;
import jadex.adapter.base.envsupport.environment.view.IView;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Rectangle;
import jadex.adapter.base.envsupport.observer.graphics.drawable.RegularPolygon;
import jadex.adapter.base.envsupport.observer.graphics.drawable.TexturedRectangle;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Triangle;
import jadex.adapter.base.envsupport.observer.graphics.layer.GridLayer;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;
import jadex.adapter.base.envsupport.observer.gui.Configuration;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.bridge.IClockService;
import jadex.bridge.ILibraryService;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Color;
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
		
	/** The environment objects. */
	protected List objects;

	//-------- methods --------
	
	/**
	 *  Get the objects of this space.
	 *  @return An array of objects (if any).
	 */
	public MEnvObject[] getMEnvObjects()
	{
		return objects==null? null:
			(MEnvObject[])objects.toArray(new MGroupInstance[objects.size()]);
	}

	/**
	 *  Add an object to this space.
	 *  @param object The object to add. 
	 */
	public void addMEnvObject(MEnvObject object)
	{
		if(objects==null)
			objects	= new ArrayList();
		objects.add(object);
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
		AbstractEnvironmentSpace ret = (AbstractEnvironmentSpace)spacetype.getClazz().newInstance();
		
		ret.setContext(app);
		
		if(getName()!=null)
		{
			ret.setName(getName());
		}
		
		if(ret instanceof Space2D) // Hack?
		{
			IVector2 areasize;
			List dims = spacetype.getDimensions();
			Number dim1 = (Number)dims.get(0);
			Number dim2 = (Number)dims.get(0);
			
			if(dim1 instanceof Integer)
				areasize = new Vector2Double(dim1.doubleValue(), dim2.doubleValue());
			else if(dim2 instanceof Double)
				areasize = new Vector2Int(dim1.intValue(), dim2.intValue());
			else
				throw new RuntimeException("Dimension class not supported: "+dim1);
			
			((Space2D)ret).setAreaSize(areasize);
		}
		
		// Create space actions.
		List spaceactions = spacetype.getMEnvSpaceActionTypes();
		if(spaceactions!=null)
		{
			for(int i=0; i<spaceactions.size(); i++)
			{
				MEnvAgentActionType maction = (MEnvAgentActionType)spaceactions.get(i);
				ISpaceAction action = (ISpaceAction)maction.getClazz().newInstance();
				
				System.out.println("Adding environment action: "+maction.getName());
				ret.addSpaceAction(maction.getName(), action);
			}
		}
		
		// Create agent actions.
		List agentactions = spacetype.getMEnvAgentActionTypes();
		if(agentactions!=null)
		{
			for(int i=0; i<agentactions.size(); i++)
			{
				MEnvAgentActionType maction = (MEnvAgentActionType)agentactions.get(i);
				IAgentAction action = (IAgentAction)maction.getClazz().newInstance();
				
				System.out.println("Adding environment action: "+maction.getName());
				ret.addAgentAction(maction.getName(), action);
			}
		}
		
		// Create processes.
		List processes = spacetype.getMEnvProcessTypes();
		if(processes!=null)
		{
			for(int i=0; i<processes.size(); i++)
			{
				MEnvProcessType mproc = (MEnvProcessType)processes.get(i);
				ISpaceProcess proc = (ISpaceProcess)mproc.getClazz().newInstance();
				
				System.out.println("Adding environment process: "+mproc.getName());
				ret.addSpaceProcess(mproc.getName(), proc);
			}
		}
		
		// Create percept generators.
		List gens = spacetype.getMEnvPerceptGeneratorTypes();
		if(gens!=null)
		{
			for(int i=0; i<gens.size(); i++)
			{
				MEnvPerceptGeneratorType mgen = (MEnvPerceptGeneratorType)gens.get(i);
				IPerceptGenerator gen = (IPerceptGenerator)mgen.getClazz().newInstance();
				
				// TODO: id --- fixed! correct?
				System.out.println("Adding environment percept generator: "+mgen.getName());
				ret.addPerceptGenerator(mgen.getName(), gen);
			}
		}
		
		// Create initial objects.
		if(objects!=null)
		{
			for(int i=0; i<objects.size(); i++)
			{
				MEnvObject mobj = (MEnvObject)objects.get(i);
			
				// Hmm local name as owner? better would be agent id, but agents are created after space?
				Object obj = ret.createSpaceObject(mobj.getType(), mobj.getOwner(), null, null, null);
			}
		}
		
		// Hack! Is configuation the presentation?
		Configuration cfg = new Configuration();
		cfg.setInvertYAxis(true);
		cfg.setObjectShift(new Vector2Double(0.5));
		
		List sourceviews = spacetype.getMEnvViews();
		if(sourceviews!=null)
		{
			for(int i=0; i<sourceviews.size(); i++)
			{				
				MEnvView sourceview = (MEnvView)sourceviews.get(i);
				
				List sourcethemes = sourceview.getMEnvThemes();
				if(sourcethemes!=null)
				{
					for(int j=0; j<sourcethemes.size(); j++)
					{
						MEnvTheme sourcetheme = (MEnvTheme)sourcethemes.get(j);
						Map targettheme = new HashMap();
						cfg.setTheme(sourcetheme.getName(), targettheme);
						
						List drawables = sourcetheme.getMEnvDrawables();
						if(drawables!=null)
						{
							for(int k=0; k<drawables.size(); k++)
							{
								MEnvDrawable sourcedrawable = (MEnvDrawable)drawables.get(k);
								DrawableCombiner targetdrawable = new DrawableCombiner(sourcedrawable.getSize());
								targettheme.put(sourcedrawable.getObjectType(), targetdrawable);
								
								List parts = sourcedrawable.getParts();
								if(parts!=null)
								{
									for(int l=0; l<parts.size(); l++)
									{
										MEnvTexturedRectangle sourcepart = (MEnvTexturedRectangle)parts.get(l);
										TexturedRectangle targetpart = new TexturedRectangle(sourcepart.getSize(), sourcepart.getShift(), sourcepart.isRotating(), sourcepart.getImagePath());
										targetdrawable.addDrawable(targetpart);
									}
								}
							}
						}
						
						List prelayers = sourcetheme.getPreLayers();
						if(prelayers!=null)
						{
							List targetprelayers = new ArrayList();
							targettheme.put("prelayers", targetprelayers);
							for(int k=0; k<prelayers.size(); k++)
							{
								Object tmp = prelayers.get(k);
								if(tmp instanceof MEnvGridPreLayer)
								{
									MEnvGridPreLayer sourceprelayer = (MEnvGridPreLayer)tmp;
									GridLayer targetprelayer = new GridLayer(sourceprelayer.getSize(), sourceprelayer.getColor());
									targetprelayers.add(targetprelayer);
								}
							}
						}
					}
				}
				
				IView targetview = (IView)sourceview.getClazz().newInstance();
				targetview.setSpace(ret);
				ret.addView(sourceview.getName(), targetview);
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
		IParsedExpression exp = spacetype.getSpaceExecutor();
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", ret);
		fetcher.setValue("$platform", app.getPlatform());
		Object spaceexe = exp.getValue(fetcher);
		
		return ret;
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
