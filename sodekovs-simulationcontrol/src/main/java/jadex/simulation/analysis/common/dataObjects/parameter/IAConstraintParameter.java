package jadex.simulation.analysis.common.dataObjects.parameter;

import java.util.Set;

public interface IAConstraintParameter extends IAParameter
{
	// TODO: Comments
	public void addConstraint(IAConstraint constraint);

	public void removeConstraint(IAConstraint constraint);

	public void clearConstraints();

	public Set<IAConstraint> getConstraints();

	public boolean containsConstraint(IAConstraint constraint);

	public Integer numberOfConstaints();

	public boolean hasConstraints();
}
