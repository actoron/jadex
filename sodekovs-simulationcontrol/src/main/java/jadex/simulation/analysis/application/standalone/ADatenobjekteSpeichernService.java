package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.session.ADefaultSessionView;
import jadex.simulation.analysis.service.basic.view.session.IASessionView;
import jadex.simulation.analysis.service.dataBased.engineering.IADatenobjekteErstellenService;
import jadex.simulation.analysis.service.dataBased.parameterize.IADatenobjekteParametrisierenGUIService;
import jadex.simulation.analysis.service.dataBased.persist.IADatenobjekteSpeichernService;
import jadex.simulation.analysis.service.simulation.Modeltype;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ADatenobjekteSpeichernService extends ABasicAnalysisService implements IADatenobjekteSpeichernService
{
	Map<String, IADataObject> saveMap;
	

	public ADatenobjekteSpeichernService(IExternalAccess instance)
	{
		super(instance, IADatenobjekteSpeichernService.class);
		synchronized (mutex)
		{
			saveMap = Collections.synchronizedMap(new HashMap<String, IADataObject>());
			view = new ADatenobjekteSpeichernView(this);
		}
		
	}

	@Override
	public IFuture saveObject(IADataObject object)
	{
		saveMap.put(object.getName(), object);
		((ADatenobjekteSpeichernView)view).addObject(object.getName());
		
		return new Future(object);
	}

	@Override
	public IFuture loadObject(String name)
	{
		return new Future(saveMap.get(name));
	}
	
	public IFuture getElements()
	{
		return new Future(saveMap);
	}

}
