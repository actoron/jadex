package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.ABasicDataObject;
import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

public class ABasicParameter extends ABasicDataObject implements IAParameter
{
	private String name = "defaultName";
	private Object value = "";
	private Class type = Object.class;
	private Boolean usage = Boolean.TRUE;

	public ABasicParameter(String name, Class type, Object value)
	{
		super();
		setName(name);
		setValueClass(type);
		setValue(value);
//		view = new ABasicParameterView(this);
	}

	// ----- Interface IAParameter -----

	// Name
	public void setName(String name)
	{
		synchronized (mutex)
		{
			this.name = name;
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	// Type
	@Override
	public Class getValueClass()
	{
		return type;
	}

	public void setValueClass(Class type)
	{
		synchronized (mutex)
		{
			this.type = type;
		}
	}

	// feasable
	@Override
	public boolean isFeasable()
	{
		return true;
	}

	// value

	@Override
	public Object getValue()
	{
		return value;
	}

	@Override
	public void setValue(Object value)
	{
		synchronized (mutex)
		{
			if (value.getClass().equals(getValueClass()))
			{
				synchronized (mutex)
				{
					this.value = value;
					System.out.println(getName() + ": value=" + value);
				}
			}
			else
			{
				throw new RuntimeException("Parametervalue falsch gesetzt " + this);
			}
			dataChanged(new ADataEvent(this, AConstants.PARAMETER_VALUE));
		}
	}

	// ----- override ABasicDataObject -----

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ABasicParameter)
		{
			ABasicParameter parameter = (ABasicParameter) obj;
			if (this.getName().equalsIgnoreCase(parameter.getName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}

	@Override
	public Boolean isUsage()
	{
		return usage;
	}

	@Override
	public void setUsage(Boolean usage)
	{
		synchronized (mutex)
		{
			this.usage = usage;
		}
		dataChanged(new ADataEvent(this, AConstants.PARAMETER_USAGE));
	}

	@Override
	public String toString()
	{
		return "ABasicParameter: " + "name=" + getName() + ", " + "value=" + getValue() + ", " + "type=" + getValueClass();
	}

}
