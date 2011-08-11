package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.AExperimentView;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

public class ABasicParameter extends ADataObject implements IAParameter
{
	protected Object value = "";
	private Class type = Object.class;
//	private Boolean usage = Boolean.TRUE;
	protected Boolean onlyValue = Boolean.TRUE;

	public ABasicParameter(String name, Class type, Object value)
	{
		super(name);
		synchronized (mutex)
		{
			setValueClass(type);
			setValue(value);
			view = new ABasicParameterView(this);
		}
	}

	// ----- Interface IAParameter -----

	// Type
	@Override
	public Class getValueClass()
	{
		return type;
	}

	@Override
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
			if (value == null)
			{
				this.value = "";
			}
			else if (value.getClass().equals(getValueClass()))
			{
				synchronized (mutex)
				{
					this.value = value;
//					System.out.println(getName() + ": value=" + value);
				}
			}
			else
			{
				throw new RuntimeException("Parametervalue falsch gesetzt " + this);
			}
			dataChanged(new ADataEvent(this, AConstants.PARAMETER_VALUE,value));
		}
	}

	// ----- override ADataObject -----

//	@Override
//	public boolean equals(Object obj)
//	{
//		if (obj instanceof ABasicParameter)
//		{
//			ABasicParameter parameter = (ABasicParameter) obj;
//			if (this.getName().equalsIgnoreCase(parameter.getName()))
//			{
//				return true;
//			}
//		}
//		return false;
//	}

//	@Override
//	public int hashCode()
//	{
//		return getName().hashCode();
//	}

//	@Override
//	public Boolean isUsage()
//	{
//		return usage;
//	}
//
//	@Override
//	public void setUsage(Boolean usage)
//	{
//		synchronized (mutex)
//		{
//			this.usage = usage;
//		}
//		dataChanged(new ADataEvent(this, AConstants.PARAMETER_USAGE, usage));
//	}

	@Override
	public String toString()
	{
		return "ABasicParameter: " + "name=" + getName() + ", " + "value=" + getValue() + ", " + "type=" + getValueClass();
	}
	
//	@Override
//	public IADataView getView()
//	{
//		return new ABasicParameterView(this);
//	}

	@Override
	public void setValueEditable(Boolean editable)
	{
		synchronized (mutex)
		{
			onlyValue = editable;
		}
		dataChanged(new ADataEvent(this, AConstants.PARAMETER_EDITABLE, editable));
		
	}

	@Override
	public Boolean isValueEditable()
	{
		return onlyValue;
	}
	
	@Override
	public void setEditable(Boolean editable)
	{
		super.setEditable(editable);
		setValueEditable(editable);
	}
	
	@Override
	public ADataObject clonen()
	{
		ABasicParameter clone = new ABasicParameter(name, type, value);
		Boolean oValue = onlyValue;
		clone.setEditable(editable);
		clone.setValueEditable(oValue);
		
		return clone;
	}

}
