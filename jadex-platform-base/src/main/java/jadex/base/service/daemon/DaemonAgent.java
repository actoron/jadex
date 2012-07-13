package jadex.base.service.daemon;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 * Daemon agent provides functionalities for managing platforms.
 */
@Description("This agent offers the daemon service.")
@GuiClassName("jadex.tools.daemon.DaemonViewerPanel")
@RequiredServices(@RequiredService(name = "libservice", type = ILibraryService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
@ProvidedServices(@ProvidedService(type = IDaemonService.class, implementation = @Implementation(DaemonService.class)))
@Agent
public class DaemonAgent //extends MicroAgent
{
}
