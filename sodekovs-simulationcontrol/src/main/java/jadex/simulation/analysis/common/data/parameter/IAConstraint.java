package jadex.simulation.analysis.common.data.parameter;

/**
 * A Contriants for AContraintParameter
 * 
 * @author 5Haubeck
 */
public interface IAConstraint
{
	/**
	 * Validate the contraint with given value
	 * 
	 * @param currentValue
	 *            the value to test the contraint with
	 * @return true, if contrain is satistfied
	 */
	public Boolean isValid(Object currentValue);
}
