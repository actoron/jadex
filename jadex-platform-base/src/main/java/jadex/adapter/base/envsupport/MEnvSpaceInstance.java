package jadex.adapter.base.envsupport;

import jadex.adapter.base.agr.MGroupInstance;
import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.bridge.IClockService;

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
	public ISpace createSpace(ApplicationContext app)
	{
//		AGRSpace	ret	= new AGRSpace(getName(), app);
		// Hack!!! todo: use reflection
		IClockService cs = (IClockService)app.getPlatform().getService(IClockService.class);
		MApplicationType mapt = app.getApplicationType();
		MEnvSpaceType st = (MEnvSpaceType)mapt.getMSpaceType(getTypeName());
		List dims = st.getDimensions();
		if(dims==null || dims.size()!=2)
			throw new RuntimeException("todo");
		IVector2 d = new Vector2Double(((Number)dims.get(0)).doubleValue(), ((Number)dims.get(1)).doubleValue());
		
		ContinuousSpace2D ret = new ContinuousSpace2D(cs, null, d);
		
		for(int i=0; objects!=null && i<objects.size(); i++)
		{
			MEnvObject mobj = (MEnvObject)objects.get(i);
		
			// What to do with id?
			// How to set owner (agent)?
			Object id = ret.createSpaceObject(mobj.getType(), null, null, null);
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
