package jadex.platform.service.filetransfer;


import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the file transfer service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IFileTransferService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(FileTransferService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class FileTransferAgent
{
}
