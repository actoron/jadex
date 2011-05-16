package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.factories.ADataViewFactory;

import java.util.Collections;
import java.util.Set;

import javax.swing.JFrame;

public class AConstraintParameter extends ABasicParameter implements IAParameter
{
	protected Set<IAConstraint> constraints;

	public AConstraintParameter(String name, Class type, Object value)
	{
		super(name, type, value);
		// setValue(value);
	}

	public AConstraintParameter(String name, Class type, Object value, Set<IAConstraint> constraints)
	{
		this(name, type, value);
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
		frame.add(ADataViewFactory.createParameterView(para).getComponent());
		frame.setVisible(true);
	}
}
