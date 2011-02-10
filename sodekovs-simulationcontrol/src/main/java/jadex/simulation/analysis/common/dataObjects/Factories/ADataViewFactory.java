package jadex.simulation.analysis.common.dataObjects.Factories;

import jadex.simulation.analysis.common.dataObjects.ADataObjectView;
import jadex.simulation.analysis.common.dataObjects.AExperiment;
import jadex.simulation.analysis.common.dataObjects.AExperimentView;
import jadex.simulation.analysis.common.dataObjects.AModel;
import jadex.simulation.analysis.common.dataObjects.AModelView;
import jadex.simulation.analysis.common.dataObjects.IADataView;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.parameter.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.ABasicParameterView;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsembleView;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.IADataObservable;

public class ADataViewFactory
{
	//TODO: MAybe add ViewClass in ADataObject
	public static IADataView createView(IADataObservable dataObject)
	{
		IADataView view;
		if (dataObject instanceof ABasicParameter)
		{
			view = createParameterView((ABasicParameter)dataObject);
		} else if (dataObject instanceof IAParameterEnsemble)
		{
			view = createParameterEnsembleView((AParameterEnsemble)dataObject);
		} else if (dataObject instanceof IAModel)
		{
			view = createModelView((AModel)dataObject);
		} else if (dataObject instanceof IAExperiment)
		{
			view = createExperimentalFrameView((AExperiment)dataObject);
		}  else
		{
			view = createDataObjectView(dataObject);
		}
		return view;
	}

	public static AParameterEnsembleView createParameterEnsembleView(AParameterEnsemble dataObject)
	{
			return new AParameterEnsembleView(dataObject);
	}

	public static AModelView createModelView(AModel dataObject)
	{
		return new AModelView(dataObject);		
	}

	public static AExperimentView createExperimentalFrameView(AExperiment dataObject)
	{
		return new AExperimentView(dataObject);				
	}

	public static ADataObjectView createDataObjectView(IADataObservable dataObject)
	{
		return new ADataObjectView(dataObject);				
	}

	public static ABasicParameterView createParameterView(ABasicParameter dataObject)
	{
		return new ABasicParameterView(dataObject);				
	}

	
}
