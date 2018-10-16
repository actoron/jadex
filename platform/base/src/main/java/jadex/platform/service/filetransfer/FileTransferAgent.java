package jadex.platform.service.filetransfer;


import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that provides the file transfer service.
 */
@Agent(autostart=Boolean3.FALSE)
@ProvidedServices(@ProvidedService(type=IFileTransferService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(FileTransferService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class FileTransferAgent
{
}
