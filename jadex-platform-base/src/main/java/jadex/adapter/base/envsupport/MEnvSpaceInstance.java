package jadex.adapter.base.envsupport;

import jadex.adapter.base.agr.MGroupInstance;
import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentSpaceTime;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IClockService;
import jadex.bridge.ILibraryService;
import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.List;

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
		
		// Create actions.
		List actions = spacetype.getMEnvActionTypes();
		if(actions!=null)
		{
			for(int i=0; i<actions.size(); i++)
			{
				MEnvActionType maction = (MEnvActionType)actions.get(i);
				Class proccl = SReflect.findClass(maction.getClassName(), mapt.getAllImports(), cl);
				
				ISpaceAction action = (ISpaceAction)proccl.newInstance();
				
				// todo: id
//				ret.addSpaceAction(maction.getName(), action);
				System.out.println("Adding environment action: "+maction.getName());
				ret.addSpaceAction(action);
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
				
				// todo: id
//				ret.addSpaceProcess(mproc.getName(), proc);
				System.out.println("Adding environment process: "+mproc.getName());
				ret.addSpaceProcess(proc);
			}
		}
		
		// Create initial objects.
		if(objects!=null)
		{
			for(int i=0; i<objects.size(); i++)
			{
				MEnvObject mobj = (MEnvObject)objects.get(i);
			
				// What to do with obj?
				// How to set owner (agent)?
				Object obj = ret.createSpaceObject(mobj.getType(), null, null, null);
			}
		}
		
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
