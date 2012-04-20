package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.ADataObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
/**
 * AConstraintParameter class, which holds a Parameter, double value and contraints
 * @author 5Haubeck
 *
 */
public class AConstraintParameter extends ABasicParameter implements IAParameter
{
	protected Set<IAConstraint> constraints;
	
	public AConstraintParameter() {
		super();
//		view = new AConstraintParameterView(this);
	}

	
	public AConstraintParameter(String name, Object value)
	{
		super(name, Double.class, value);
		synchronized (mutex)
		{
			this.constraints = Collections.synchronizedSet(new HashSet<IAConstraint>());
			addConstraint(new ABorderConstraint(100.0, 0.0));
//			view = new AConstraintParameterView(this);
		}
	}

	public AConstraintParameter(String name, Object value, Set<IAConstraint> constraints)
	{
		this(name, value);
		synchronized (mutex)
		{
			this.constraints = Collections.synchronizedSet(constraints);
		}
	}

	// ----- Interface IAConstraintParameter -----
	
	

	public Set<IAConstraint> getConstraints()
	{
		return constraints;
	}

	public void setConstraints(Set<IAConstraint> constraints) {
		synchronized (mutex)
		{
		this.constraints = constraints;
		}
	}


	public void addConstraint(IAConstraint constraint)
	{
		synchronized (mutex)
		{
			constraints.add(constraint);
		}
	}

	public void removeConstraint(IAConstraint constraint)
	{
		synchronized (mutex)
		{
			constraints.remove(constraint);
		}
	}

	public void clearConstraints()
	{
		synchronized (mutex)
		{
			constraints.clear();
		}
	}

	public boolean containsConstraint(IAConstraint constraint)
	{
		return constraints.contains(constraint);
	}

	public boolean hasConstraints()
	{
		return constraints.isEmpty();
	}

	public Integer numberOfConstaints()
	{
		return constraints.size();
	}

	// ----- Override ABasicParameter -----

	public boolean isFeasable()
	{
		synchronized (mutex)
		{
			boolean ret = true;
			for (IAConstraint constraint : constraints)
			{
				if (constraint.isValid(getValue())) ret = false;
			}
			return ret;
		}
	}
	
	@Override
	public ADataObject clonen()
	{
		AConstraintParameter clone = new AConstraintParameter(name, value);
		Boolean oValue = onlyValue;
		clone.setEditable(editable);
		clone.setValueEditable(oValue);
		for (IAConstraint cons : getConstraints())
		{
			clone.addConstraint(cons);
		}
		return clone;
	}

	/**
	 * Test View
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		ABasicParameter para = new ABasicParameter("Parameter", Double.class, 5.0);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 786);
		frame.add(para.getView().getComponent());
		frame.setVisible(true);
	}
}
