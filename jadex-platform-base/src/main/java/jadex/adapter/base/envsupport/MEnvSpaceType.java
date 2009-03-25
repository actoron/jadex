package jadex.adapter.base.envsupport;

import jadex.adapter.base.agr.MGroupInstance;
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
	
	/** The height. */
	protected List dimensions;
	
	/** The implementation class. */
	protected String clazz;
	
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
	 * @return the clazz
	 */
	public String getClazz()
	{
		return this.clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz)
	{
		this.clazz = clazz;
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
		sbuf.append(", class=");
		sbuf.append(getClazz());
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
		types.add(new TypeInfo("envspacetype", MEnvSpaceType.class));
		types.add(new TypeInfo("envspace", MEnvSpaceInstance.class, null, null,
			SUtil.createHashMap(new String[]{"type"}, new String[]{"setTypeName"}), null));
		types.add(new TypeInfo("envobject", MEnvObject.class));
		return types;
	}
}
