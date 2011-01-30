package jadex.simulation.analysis.buildingBlocks.simulation.netLogo;

import jadex.bdi.runtime.ICapability;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IModelInspectionService;
import jadex.simulation.analysis.common.dataObjects.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.AParameterCollection;
import jadex.simulation.analysis.common.dataObjects.IAParameterCollection;

import java.util.HashSet;
import java.util.Set;

public class NetLogoModelService extends BasicService implements IModelInspectionService {

	public NetLogoModelService(ICapability cap) {
		super(cap.getServiceProvider().getId(), IModelInspectionService.class, null);
//		Map prop = getPropertyMap();
//		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.buildingBlocks.execution.ExecutionServiceView");
//		setPropertyMap(prop);
	}

	@Override
	public IFuture modelParamter(String name) {
		Future res = new Future();
		
		// TODO: Search right Model (globals in netLogo model)
		IAParameterCollection paramters = new AParameterCollection();
		paramters.add(new ABasicParameter("population", "100",String.class,false,false));
		paramters.add(new ABasicParameter("diffusion-rate", "40",String.class,false,false));
		paramters.add(new ABasicParameter("evaporation-rate", "10",String.class,true,false));
		res.setResult(paramters);
		return res;
	}
	
	@Override
	public IFuture resultParamter(String name) {
		Future res = new Future();
		
		// TODO: Search right Model
		IAParameterCollection paramters = new AParameterCollection();
		paramters.add(new ABasicParameter("ticks", null, String.class,true,true));
		res.setResult(paramters);
		return res;
	}

	@Override
	public Set<String> supportedModels() {
		Set<String> result = new HashSet<String>();
		result.add("netLogo");
		return result;
	}
}
