package jadex.bridge;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.message.IMessageListener;


/**
 *  Message service listener interface.
 */
@Reference
public interface IRemoteMessageListener	extends IMessageListener//, IRemotable
{
}