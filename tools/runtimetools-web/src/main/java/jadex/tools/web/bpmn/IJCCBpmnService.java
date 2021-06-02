package jadex.tools.web.bpmn;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.tools.web.jcc.IJCCPluginService;
import jadex.tools.web.security.JCCSecurityPluginAgent.SecurityState;

/**
 *  Interface for the jcc security service.
 */
@Service(system=true)
public interface IJCCBpmnService extends IJCCPluginService
{
}
