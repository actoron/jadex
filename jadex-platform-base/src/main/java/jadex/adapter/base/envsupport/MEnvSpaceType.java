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
 * An Environment space description.
 */
public class MEnvSpaceType	extends MSpaceType
{
	//-------- attributes --------
	
	/** The width. */
	protected double width;

	/** The height. */
	protected double height;
	
	/** The implementation class. */
	protected String clazz;
	
	//-------- methods --------
		
	/**
	 *  Get the width.
	 *  @return The width.
	 */
	public double getWidth()
	{
		return width;
	}
	
	/**
	 *  Set the width.
	 *  @param width The width to set.
	 */
	public void setWidth(double width)
	{
		this.width = width;
	}
	
	/**
	 * @return the height
	 */
	public double getHeight()
	{
		return this.height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height)
	{
		this.height = height;
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
		sbuf.append(", width=");
		sbuf.append(getWidth());
		sbuf.append(", height=");
		sbuf.append(getHeight());
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
		types.add(new TypeInfo("envspace", MEnvSpaceInstance.class));
		return types;
	}
}
