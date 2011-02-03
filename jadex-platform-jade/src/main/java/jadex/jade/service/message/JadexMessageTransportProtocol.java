package jadex.jade.service.message;

import jade.core.Profile;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.mtp.MTP;
import jade.mtp.MTPException;
import jade.mtp.TransportAddress;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IMessageService;
import jadex.commons.IFuture;
import jadex.commons.service.SServiceProvider;
import jadex.jade.ComponentAdapterFactory;


/**
 * A JADE message transport protocol that supports Jadex standalone transports.
 */
public class JadexMessageTransportProtocol implements MTP
{
	/**
	 * Activates an MTP handler for incoming messages on a default address.
	 * 
	 * @parameter p is the Profile from which the configuration parameters for
	 *            this instance of JADE container can be retrieved
	 * @return A <code>TransportAddress</code>, corresponding to the chosen
	 *         default address.
	 * @exception MTPException Thrown if some MTP initialization error occurs.
	 */
	public TransportAddress activate(Dispatcher disp, Profile p)
			throws MTPException
	{
		IFuture	future	= SServiceProvider.getService(ComponentAdapterFactory.getInstance().getRootComponent().getServiceProvider(), IMessageService.class);
		assert future.isDone();
		MessageService	ms	= (MessageService)future.get(null);
		ITransport	trans	= ms.getTransport();
		System.out.println("JadexMessageTransportProtocol.activate(): "+trans);		
		return null;
	}

	/**
	 * Activates an MTP handler for incoming messages on a specific address.
	 * 
	 * @param ta A <code>TransportAddress</code> object, representing the
	 *        transport address to listen to.
	 * @parameter p is the Profile from which the configuration parameters for
	 *            this instance of JADE container can be retrieved
	 * @exception MTPException Thrown if some MTP initialization error occurs.
	 */
	public void activate(Dispatcher disp, TransportAddress ta, Profile p)
			throws MTPException
	{
		System.out.println("JadexMessageTransportProtocol.activate(ta)");
	}

	/**
	 * Deactivates the MTP handler listening at a given transport address.
	 * 
	 * @param ta The <code>TransportAddress</code> object the handle to close is
	 *        listening to.
	 * @exception MTPException Thrown if some MTP cleanup error occurs.
	 */
	public void deactivate(TransportAddress ta) throws MTPException
	{
		System.out.println("JadexMessageTransportProtocol.deactivate(ta)");
	}

	/**
	 * Deactivates all the MTP handlers.
	 * 
	 * @exception MTPException Thrown if some MTP cleanup error occurs.
	 */
	public void deactivate() throws MTPException
	{
		System.out.println("JadexMessageTransportProtocol.deactivate()");
	}

	/**
	 * Delivers to the specified address an ACL message, encoded in some
	 * concrete message representation, using the given envelope as a
	 * transmission header.
	 * 
	 * @param ta The transport address to deliver the message to. It must be a
	 *        valid address for this MTP.
	 * @param env The message envelope, containing various fields related to
	 *        message recipients, encoding, and timestamping.
	 * @payload The byte sequence that contains the encoded ACL message.
	 * @exception MTPException Thrown if some MTP delivery error occurs.
	 */
	public void deliver(String addr, Envelope env, byte[] payload)
			throws MTPException
	{
		System.out.println("JadexMessageTransportProtocol.deliver()");
	}

	/**
	 * Converts a string representing a valid address in this MTP to a
	 * <code>TransportAddress</code> object.
	 * 
	 * @param rep The string representation of the address.
	 * @return A <code>TransportAddress</code> object, created from the given
	 *         string.
	 * @exception MTPException If the given string is not a valid address
	 *            according to this MTP.
	 */
	public TransportAddress strToAddr(String rep) throws MTPException
	{
		System.out.println("JadexMessageTransportProtocol.strToAddr()");
		return null;
	}

	/**
	 * Converts a <code>TransportAddress</code> object into a string
	 * representation.
	 * 
	 * @param ta The <code>TransportAddress</code> object.
	 * @return A string representing the given address.
	 * @exception MTPException If the given <code>TransportAddress</code> is not
	 *            a valid address for this MTP.
	 */
	public String addrToStr(TransportAddress ta) throws MTPException
	{
		System.out.println("JadexMessageTransportProtocol.addrToStr()");
		return null;
	}

	/**
	 * Reads the name of the message transport protocol managed by this MTP. The
	 * FIPA standard message transport protocols have a name starting with
	 * <code><b>"fipa.mts.mtp"</b></code>.
	 * 
	 * @return A string, that is the name of this MTP.
	 */
	public String getName()
	{
		System.out.println("JadexMessageTransportProtocol.getName()");
		return null;
	}

	/**
	 * Get the supported protocols, i.e. prefixes like 'http'.
	 */
	public String[] getSupportedProtocols()
	{
		System.out.println("JadexMessageTransportProtocol.getSupportedProtocols()");
		return null;
	}
}
