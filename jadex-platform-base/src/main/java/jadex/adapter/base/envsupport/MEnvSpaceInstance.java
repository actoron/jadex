package jadex.adapter.base.envsupport;

import jadex.adapter.base.agr.MGroupInstance;
import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentSpaceTime;
import jadex.adapter.base.envsupport.environment.IPerceptGenerator;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.agentaction.IAgentAction;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.environment.view.GeneralView2D;
import jadex.adapter.base.envsupport.environment.view.IView;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Rectangle;
import jadex.adapter.base.envsupport.observer.graphics.drawable.RegularPolygon;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Triangle;
import jadex.adapter.base.envsupport.observer.graphics.layer.GridLayer;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;
import jadex.adapter.base.envsupport.observer.gui.Configuration;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.bridge.IClockService;
import jadex.bridge.ILibraryService;
import jadex.commons.SReflect;

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
		
		Class envcl = SReflect.findClass(spacetype.getClassName(), mapt.getAllImports(), cl);
		
		// Create and init space.
		AbstractEnvironmentSpace ret = (AbstractEnvironmentSpace)envcl.newInstance();
		
		ret.setContext(app);
		
		if(getName()!=null)
		{
			ret.setName(getName());
		}
		
		if(ret instanceof EnvironmentSpaceTime) // Hack?
		{
			((EnvironmentSpaceTime)ret).setClockService((IClockService)app.getPlatform().getService(IClockService.class));
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
				Class proccl = SReflect.findClass(maction.getClassName(), mapt.getAllImports(), cl);
				
				ISpaceAction action = (ISpaceAction)proccl.newInstance();
				
				// TODO: id --- fixed! correct?
				System.out.println("Adding environment action: "+maction.getName());
				ret.addSpaceAction(maction.getName(), action);
				//ret.addSpaceAction(action);
			}
		}
		
		// Create agent actions.
		List agentactions = spacetype.getMEnvAgentActionTypes();
		if(agentactions!=null)
		{
			for(int i=0; i<agentactions.size(); i++)
			{
				MEnvAgentActionType maction = (MEnvAgentActionType)agentactions.get(i);
				Class proccl = SReflect.findClass(maction.getClassName(), mapt.getAllImports(), cl);
				
				IAgentAction action = (IAgentAction)proccl.newInstance();
				
				// TODO: id --- fixed! correct?
				System.out.println("Adding environment action: "+maction.getName());
				ret.addAgentAction(maction.getName(), action);
				//ret.addSpaceAction(action);
			}
		}
		
		// Create processes.
		List processes = spacetype.getMEnvProcessTypes();
		if(processes!=null)
		{
			for(int i=0; i<processes.size(); i++)
			{
				MEnvProcessType mproc = (MEnvProcessType)processes.get(i);
				Class proccl = SReflect.findClass(mproc.getClassName(), mapt.getAllImports(), cl);
				
				ISpaceProcess proc = (ISpaceProcess)proccl.newInstance();
				
				// TODO: id --- fixed! correct?
				System.out.println("Adding environment process: "+mproc.getName());
				ret.addSpaceProcess(mproc.getName(), proc);
				//ret.addSpaceProcess(proc);
			}
		}
		
		// Create percept generators.
		List gens = spacetype.getMEnvPerceptGeneratorTypes();
		if(gens!=null)
		{
			for(int i=0; i<gens.size(); i++)
			{
				MEnvPerceptGeneratorType mgen = (MEnvPerceptGeneratorType)gens.get(i);
				Class gencl = SReflect.findClass(mgen.getClassName(), mapt.getAllImports(), cl);
				
				IPerceptGenerator gen = (IPerceptGenerator)gencl.newInstance();
				
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
		
		// Create the observers.
		// Hack!
		Configuration cfg = new Configuration();
		Map theme = new HashMap();
		
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
		ret.addView(GeneralView2D.class.getName(), new GeneralView2D((Space2D)ret));
		
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
