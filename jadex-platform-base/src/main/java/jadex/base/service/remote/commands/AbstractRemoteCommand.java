package jadex.base.service.remote.commands;

import jadex.base.service.remote.IRemoteCommand;
import jadex.base.service.remote.RemoteReferenceModule;
import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public abstract class AbstractRemoteCommand implements IRemoteCommand
{
	/**
	 *  Preprocess command and replace  if they are remote references.
	 */
	public void preprocessCommand(RemoteReferenceModule rrm, IComponentIdentifier target)
	{
	}
}
