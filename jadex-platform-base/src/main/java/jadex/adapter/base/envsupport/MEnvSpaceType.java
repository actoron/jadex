package jadex.adapter.base.envsupport;

import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.TypeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Java representation of environemnt space type for xml description.
 */
public class MEnvSpaceType	extends MSpaceType
{
	//-------- attributes --------
	
	/** The dimensions. */
	protected List dimensions;
	
	/** The action types. */
	protected List actiontypes;
	
	/** The process types. */
	protected List processtypes;
	
	/** The implementation class name. */
	protected String classname;
	
	//-------- methods --------
		
	/**
	 *  Add a dimension.
	 */
	public void addDimension(Double d)
	{
		if(dimensions==null)
			dimensions = new ArrayList();
		dimensions.add(d);	
	}
	
	/**
	 *  Get the dimensions.
	 *  @return The dimensions.
	 */
	public List getDimensions()
	{
		return dimensions;
	}
	
	/**
	 *  Add a action type.
	 *  @param action The action.
	 */
	public void addMEnvActionType(MEnvActionType action)
	{
		if(actiontypes==null)
			actiontypes = new ArrayList();
		actiontypes.add(action);	
	}
	
	/**
	 *  Get the action types.
	 *  @return The action types.
	 */
	public List getMEnvActionTypes()
	{
		return actiontypes;
	}
	
	/**
	 *  Add a process type.
	 *  @param process The process.
	 */
	public void addMEnvProcessType(MEnvProcessType process)
	{
		if(processtypes==null)
			processtypes = new ArrayList();
		processtypes.add(process);	
	}
	
	/**
	 *  Get the process types.
	 *  @return The process types.
	 */
	public List getMEnvProcessTypes()
	{
		return processtypes;
	}
	
	/**
	 *  Get the class name.
	 *  @return The class name.
	 */
	public String getClassName()
	{
		return this.classname;
	}

	/**
	 *  Set the class name.
	 *  @param classname The class name to set.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}

	/**
	 *  Get a string representation of this AGR space type.
	 *  @return A string representation of this AGR space type.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		sbuf.append(", dimensions=");
		sbuf.append(getDimensions());
		sbuf.append(", action types=");
		sbuf.append(getMEnvActionTypes());
		sbuf.append(", class=");
		sbuf.append(getClassName());
		sbuf.append(")");
		return sbuf.toString();
	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		types.add(new TypeInfo("envspacetype", MEnvSpaceType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		types.add(new TypeInfo("envspace", MEnvSpaceInstance.class, null, null,
			SUtil.createHashMap(new String[]{"type"}, new String[]{"setTypeName"}), null));
		types.add(new TypeInfo("actiontype", MEnvActionType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		types.add(new TypeInfo("processtype", MEnvProcessType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		types.add(new TypeInfo("object", MEnvObject.class));
		return types;
	}
}
