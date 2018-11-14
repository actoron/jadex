package jadex.extension.envsupport;

import jadex.commons.meta.IPropertyMetaData;
import jadex.javaparser.IParsedExpression;

/**
 *  todo: comment me
 */
public class MObjectTypeProperty implements IPropertyMetaData
{

	private Object	value;

	private String	name;

	private Class	type;

	private boolean	dynamic;

	private boolean	event;

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;

		if(type == null)
			type = ((IParsedExpression)value).getStaticType();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Class getType()
	{
		return type;
	}

	public void setType(Class type)
	{
		this.type = type;
	}

	public boolean isDynamic()
	{
		return dynamic;
	}

	public void setDynamic(boolean dynamic)
	{
		this.dynamic = dynamic;
	}

	public boolean isEvent()
	{
		return event;
	}

	public void setEvent(boolean event)
	{
		this.event = event;
	}
}
