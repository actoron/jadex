package jadex.platform.service.remote;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.Tuple2;
import jadex.micro.features.impl.MicroMessageComponentFeature;

/**
 *  Message feature of the RMS.
 *
 */
public class RmsMessageFeature extends MicroMessageComponentFeature
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
	@Override
	@SuppressWarnings("unchecked")
	protected Tuple2<Map<String, Object>, Object> deserializeMessage(IMsgHeader header, byte[] serializedmsg)
	{
		IComponentIdentifier callreceiver = (IComponentIdentifier) ((MsgHeader) header).getProperty(RemoteServiceManagementAgent.CALL_RECEIVER);
		ClassLoader cl = callreceiver == null ? component.getClassLoader() : SComponentManagementService.getLocalClassLoader(callreceiver);		
		return  (Tuple2<Map<String, Object>, Object>) getSerializationServices(platformid).decode(header, cl, serializedmsg);
	}
}
