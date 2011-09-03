package jadex.simulation.analysis.common.data.parameter;

/**
 * IAContraint for AContrainParameter. Double borders
 * @author 5Haubeck
 *
 */
public class ABorderConstraint extends Object implements IAConstraint
{
	Double upperBorder;
	Double lowerBorder;
	
	public ABorderConstraint(Double up, Double down)
	{
		upperBorder = up;
		lowerBorder = down;
	}

	@Override
	public Boolean isValid(Object currentValue)
	{
		Boolean result = true;
		Double value = (Double) currentValue;
		if (value>=upperBorder) result = false;
		if (value<=lowerBorder) result = false;
		return result;
	}
	
	public void setUpperBound(Double up)
	{
		upperBorder = up;
	}
	
	public void setLowerBound(Double low)
	{
		lowerBorder = low;
	}
	
	public Double getUpperBound()
	{
		return upperBorder;
	}
	
	public Double getLowerBound()
	{
		return lowerBorder;
	}

}
