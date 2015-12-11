package jadex.extension.envsupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.commons.meta.IPropertyMetaData;
import jadex.commons.meta.IPropertyMetaDataSet;


/**
 *  todo: comment me
 */
public class MObjectType implements IPropertyMetaDataSet
{

	private String	name;

	private boolean	kdtree;

	private Map		properties	= new HashMap();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isKdTree()
	{
		return kdtree;
	}

	public void setKdTree(boolean kdtree)
	{
		this.kdtree = kdtree;
	}

	public void addProperty(MObjectTypeProperty property)
	{
		properties.put(property.getName(), property);
	}

	public IPropertyMetaData getProperty(String name)
	{
		return (MObjectTypeProperty)properties.get(name);
	}

	public Iterator iterator()
	{
		return properties.values().iterator();
	}
}
