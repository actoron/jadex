package jadex.simulation.analysis.buildingBlocks.simulation.desmoJ;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bdi.runtime.ICapability;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IModelInspectionService;
import jadex.simulation.analysis.common.dataObjects.parameter.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DesmoJModelService extends BasicService implements IModelInspectionService {

	public DesmoJModelService(ICapability cap) {
		super(cap.getServiceProvider().getId(), IModelInspectionService.class, null);
		Map prop = getPropertyMap();
		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.buildingBlocks.execution.ExecutionServiceView");
		setPropertyMap(prop);
	}

	@Override
	public IFuture inputParamter(String name) {
		Future res = new Future();
		
		// TODO: Search right Model (DesmoJ Reflect)
		IAParameterEnsemble paramters = new AParameterEnsemble();
		paramters.addParameter(new ABasicParameter("vcNumber", Integer.class, new Integer(2)));
		res.setResult(paramters);
		return res;
	}
	
	@Override
	public IFuture outputParamter(String name) {
		Future res = new Future();
		
		// TODO: Search right Model (DesmoJ Reflect)
		IAParameterEnsemble paramters = new AParameterEnsemble();
		paramters.addParameter(new ABasicParameter("zeit", Double.class, 0d));
		res.setResult(paramters);
		return res;
	}

	@Override
	public Set<String> supportedModels() {
		Set<String> result = new HashSet<String>();
		result.add("desmoJ");
		return result;
	}
}
