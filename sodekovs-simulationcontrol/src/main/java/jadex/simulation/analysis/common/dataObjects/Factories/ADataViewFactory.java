package jadex.simulation.analysis.common.dataObjects.Factories;

import jadex.simulation.analysis.common.dataObjects.ABasicDataObjectView;
import jadex.simulation.analysis.common.dataObjects.AExperiment;
import jadex.simulation.analysis.common.dataObjects.AExperimentView;
import jadex.simulation.analysis.common.dataObjects.AModel;
import jadex.simulation.analysis.common.dataObjects.AModelView;
import jadex.simulation.analysis.common.dataObjects.IADataObject;
import jadex.simulation.analysis.common.dataObjects.IADataObjectView;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.parameter.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.ABasicParameterView;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsembleView;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

public class ADataViewFactory
{
	
	public static IADataObjectView createView(IADataObject dataObject)
	{
		IADataObjectView view;
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

	public static ABasicDataObjectView createDataObjectView(IADataObject dataObject)
	{
		return new ABasicDataObjectView(dataObject);				
	}

	public static ABasicParameterView createParameterView(ABasicParameter dataObject)
	{
		return new ABasicParameterView(dataObject);				
	}

	
}
