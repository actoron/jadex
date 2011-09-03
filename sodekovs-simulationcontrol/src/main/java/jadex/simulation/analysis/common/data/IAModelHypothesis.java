package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.IAParameter;

public interface IAModelHypothesis extends IADataObject
{
	public String getName();

	public void setName(String name);
	
	public void setCorrelation(Boolean correlation);
	
	public Boolean getCorrelation();

	public void setSecondParameters(IAParameter parameters);

	public IAParameter getSecondParameter();

	public void setFirstParameters(IAParameter parameters);

	public IAParameter getFirstParameter();
}
