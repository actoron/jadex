package jadex.platform.service.message.relaytransport;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IMsgHeader;
import jadex.micro.features.impl.MicroMessageComponentFeature;

/**
 *  Message feature for relay component, skips body decryption on forwarded messages.
 *
 */
public class RelayMessageComponentFeature extends MicroMessageComponentFeature
{
	/**
	 *  Create the feature.
	 */
	public RelayMessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Inform the component that a message has arrived.
	 *  Called from transports (i.e. remote messages).
	 *  
	 *  @param header The message header.
	 *  @param bodydata The encrypted message that arrived.
	 */
	public void messageArrived(IMsgHeader header, byte[] bodydata)
	{
		if (header.getProperty(RelayTransportAgent.FORWARD_DEST) != null)
			messageArrived(null, header, bodydata);
		else
			super.messageArrived(header, bodydata);
	}
}
