package jadex.platform.service.message.relaytransport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.future.IFuture;
import jadex.micro.features.impl.MicroMessageComponentFeature;

/**
 *  Message feature for relay component, skips body decryption on forwarded messages.
 *
 */
public class RelayMessageComponentFeature extends MicroMessageComponentFeature
{
	/** Transport cache for the relay, excludes itself. */
	public Map<IComponentIdentifier, Tuple2<ITransportService, Integer>> relaytransportcache = Collections.synchronizedMap(new LRU<IComponentIdentifier, Tuple2<ITransportService,Integer>>(100));
	
	/**
	 *  Create the feature.
	 */
	public RelayMessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	public IFuture<Void> sendToTransports(IMsgHeader header, byte[] encryptedbody)
	{
//		if (header.getProperty(RelayTransportAgent.FORWARD_DEST) == null)
//		if (header.getProperty(IMsgHeader.RECEIVER).equals(((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot()))
//		{
//			(new RuntimeException("msg to pf component: " + Arrays.toString(((MsgHeader) header).getProperties().keySet().toArray()))).printStackTrace();
//		}
		return super.sendToTransports(header, encryptedbody);
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
//		System.out.println("RMCF CALLED: " + Arrays.toString(((MsgHeader) header).getProperties().keySet().toArray()));
		if (header.getProperty(RelayTransportAgent.FORWARD_DEST) != null)
			messageArrived(null, header, bodydata);
		else
			super.messageArrived(header, bodydata);
	}
	
	/**
	 *  Gets the transport cache services.
	 *  
	 *  @param platformid The platform ID.
	 *  @return The transport cache.
	 */
	protected Map<IComponentIdentifier, Tuple2<ITransportService, Integer>> getTransportCache(IComponentIdentifier platformid)
	{
		return relaytransportcache;
	}
	
	/**
	 *  Gets all transports on the platform except the relay.
	 *  
	 *  @return All transports.
	 */
	protected Collection<ITransportService> getAllTransports()
	{
		List<ITransportService> ret = new ArrayList<ITransportService>();
		Collection<ITransportService> all = ((IInternalRequiredServicesFeature)component.getFeature(IRequiredServicesFeature.class)).getRawServices(ITransportService.class);
		if (all != null)
		{
			for (Iterator<ITransportService> it = all.iterator(); it.hasNext(); )
			{
				IService serv = (IService) it.next();
				if (!component.getIdentifier().equals(serv.getServiceIdentifier().getProviderId()))
					ret.add((ITransportService) serv);
			}
		}
		return ret;
	}
}
