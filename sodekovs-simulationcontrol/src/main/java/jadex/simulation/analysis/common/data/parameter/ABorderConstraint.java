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
	
	public ABorderConstraint()
	{
	}
	
	public ABorderConstraint(Double up, Double down)
	{
		upperBorder = up;
		lowerBorder = down;
	}
	
	

	public Double getUpperBorder() {
		return upperBorder;
	}

	public void setUpperBorder(Double upperBorder) {
		this.upperBorder = upperBorder;
	}

	public Double getLowerBorder() {
		return lowerBorder;
	}

	public void setLowerBorder(Double lowerBorder) {
		this.lowerBorder = lowerBorder;
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
}
