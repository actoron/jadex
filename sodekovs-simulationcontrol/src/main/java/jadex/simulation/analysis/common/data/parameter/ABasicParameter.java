package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.AExperimentView;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

/**
 * ABasicParameter class, which holds a parameter with value and class
 * 
 * @author 5Haubeck
 * 
 */
public class ABasicParameter extends ADataObject implements IAParameter {
	
	private Class type = Object.class;
	protected Boolean onlyValue = Boolean.TRUE;
	protected Object value = "";

	public ABasicParameter() {
//		view = new ABasicParameterView(this);
	}

	public ABasicParameter(String name, Class type, Object value) {
		super(name);
		synchronized (mutex) {
			setValueClass(type);
			setValue(value);
//			view = new ABasicParameterView(this);
		}
	}

	// ----- Interface IAParameter -----

	// Type
	@Override
	public Class getValueClass() {
		return type;
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		synchronized (mutex) {
			this.type = type;
		}
	}

	public Boolean getOnlyValue() {
		return onlyValue;
	}

	public void setOnlyValue(Boolean onlyValue) {
		synchronized (mutex) {
			this.onlyValue = onlyValue;
		}
	}

	@Override
	public void setValueClass(Class type) {
		synchronized (mutex) {
			this.type = type;
		}
	}

	// feasable
	@Override
	public boolean isFeasable() {
		return true;
	}

	// value
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		synchronized (mutex) {
			// System.out.println(value.getClass());
			// System.out.println(getValueClass());
			if (value == null) {
				this.value = "";
			}

//			else if (value.getClass().equals(getValueClass())) {
				synchronized (mutex) {
					this.value = value;
					// System.out.println(getName() + ": value=" + value);
				}
//			} else {
//				throw new RuntimeException("Parametervalue falsch gesetzt "
//						+ this);
//			}
			notify(new ADataEvent(this, AConstants.PARAMETER_VALUE, value));
		}
	}

	// ----- override ADataObject -----

	@Override
	public void setValueEditable(Boolean editable) {
		synchronized (mutex) {
			onlyValue = editable;
		}
		notify(new ADataEvent(this, AConstants.PARAMETER_EDITABLE, editable));

	}

	@Override
	public Boolean isValueEditable() {
		return onlyValue;
	}

	@Override
	public void setEditable(Boolean editable) {
		super.setEditable(editable);
		setValueEditable(editable);
	}

	@Override
	public ADataObject clonen() {
		ABasicParameter clone = new ABasicParameter(name, type, value);
		Boolean oValue = onlyValue;
		clone.setEditable(editable);
		clone.setValueEditable(oValue);

		return clone;
	}
}
