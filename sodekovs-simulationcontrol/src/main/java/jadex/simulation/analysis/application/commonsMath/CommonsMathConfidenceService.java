package jadex.simulation.analysis.application.commonsMath;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.data.parameter.IASummaryParameter;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisService;
import jadex.simulation.analysis.service.continuative.computation.IAConfidenceService;

import org.apache.commons.math3.stat.inference.TTest;

public class CommonsMathConfidenceService extends ABasicAnalysisService implements IAConfidenceService
{
	private TTest ttest = new TTest();

	public CommonsMathConfidenceService(IExternalAccess access)
	{
		super(access, IAConfidenceService.class);
	}

	@Override
	public IFuture computeTTest(IASummaryParameter parameter, Double intervallPercent)
	{
		if (parameter.getN() <2 )
		{
			throw new RuntimeException("TTest: n must be >= 2");
		}
		
		double[] values = new double[parameter.getN().intValue()];
		for (int i = 0; i < parameter.getN().intValue(); i++)
		{
			values[i] = parameter.getValues().get(i);
		}
		Double pvalue = 0.0;
		try
		{
			pvalue = 1-ttest.tTest(parameter.getMean()*(1-intervallPercent),values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return new Future(pvalue);
	}

	@Override
	public IFuture testTTest(IASummaryParameter parameter, Double intervallPercent, Double alpha)
	{
		if ((Double)computeTTest(parameter,intervallPercent).get(new ThreadSuspendable(this)) >= alpha)
		{
			return new Future(Boolean.TRUE);
		} else
		{
			return new Future(Boolean.FALSE);
		}
	}
	
	

}
