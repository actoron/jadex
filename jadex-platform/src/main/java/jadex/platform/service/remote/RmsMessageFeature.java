package jadex.platform.service.remote;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.service.types.cms.SComponentManagementService;

public class RmsMessageFeature extends MessageComponentFeature
{
	/**
	 *  Create the feature.
	 */
	public RmsMessageFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Deserialize the message.
	 *  
	 *  @param header The message header.
	 *  @param serializedmsg The serialized message.
	 *  @return The deserialized message.
	 */
	protected Object deserializeMessage(Map<String, Object> header, byte[] serializedmsg)
	{
		IComponentIdentifier callreceiver = (IComponentIdentifier) header.get(RemoteServiceManagementAgent.CALL_RECEIVER);
		ClassLoader cl = callreceiver == null ? component.getClassLoader() : SComponentManagementService.getLocalClassLoader(callreceiver);		
		return getSerializationServices(platformid).decode(cl, serializedmsg);
	}
}
