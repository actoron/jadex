package jadex.simulation.analysis.buildingBlocks.simulation.desmoJ;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bdi.runtime.ICapability;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IModelInspectionService;
import jadex.simulation.analysis.common.dataObjects.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.AParameterCollection;
import jadex.simulation.analysis.common.dataObjects.IAParameterCollection;

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
	public IFuture modelParamter(String name) {
		Future res = new Future();
		
		// TODO: Search right Model (DesmoJ Reflect)
		IAParameterCollection paramters = new AParameterCollection();
		paramters.add(new ABasicParameter("vcNumber", "2",String.class,false,false));
		res.setResult(paramters);
		return res;
	}
	
	@Override
	public IFuture resultParamter(String name) {
		Future res = new Future();
		
		// TODO: Search right Model (DesmoJ Reflect)
		IAParameterCollection paramters = new AParameterCollection();
		paramters.add(new ABasicParameter("zeit", null,String.class,true,true));
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
