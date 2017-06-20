package jadex.bridge.service.types.message;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Service;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  The interface for the message service. It is responsible for
 *  managing the transports and sending/delivering messages.
 */
@Service(system=true)
public interface IMessageService extends IService
{
	/**
	 *  Send a message.
	 *  @param message The message as key value pairs.
	 *  @param msgtype The message type.
	 *  @param sender The sender component identifier.
	 *  @param rid The resource identifier used by the sending component (i.e. corresponding to classes of objects in the message map).
	 *  @param realrec The real receiver if different from the message receiver (e.g. message to rms encapsulating service call to other component).
	 *  *  @param serializerid ID of the serializer for encoding the message.
	 *  @param codecids The codecs to use for encoding (if different from default).
	 *  @param nonfunc The non functional properties that need to be preserved.
	 *  @return Future that indicates an exception when messages could not be delivered to components. 
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> origmsg, final MessageType type, 
			final IComponentIdentifier osender, final IResourceIdentifier rid, 
			final IComponentIdentifier realrec, final Byte serializerid, final byte[] codecids);//, Map<String, Object> nonfunc);
	
//	/**
//	 *  Deliver a message to some components.
//	 */
//	public void deliverMessage(Map<String, Object> msg, String type, IComponentIdentifier[] receivers);

	/**
	 *  Deliver a raw message to some components.
	 *  @param msg	Byte array containing a message in the form {prolog}{data}.
	 */
	public void deliverMessage(byte[] msg);
	
//	/**
//	 *  Create a virtual output connection.
//	 */
//	public IFuture<IOutputConnection> createOutputConnection(ITransportComponentIdentifier sender, ITransportComponentIdentifier receiver, Map<String, Object> nonfunc);
//
//	/**
//	 *  Create a virtual input connection.
//	 */
//	public IFuture<IInputConnection> createInputConnection(ITransportComponentIdentifier sender, ITransportComponentIdentifier receiver, Map<String, Object> nonfunc);
		
	/**
	 *  Create a virtual output connection.
	 */
	public IFuture<IOutputConnection> createOutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc);

	/**
	 *  Create a virtual input connection.
	 */
	public IFuture<IInputConnection> createInputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc);
	
	/**
	 *  Add a message listener.
	 *  @param listener The change listener.
	 *  @param filter An optional filter to only receive notifications for matching messages. 
	 */
	public IFuture<Void> addMessageListener(IMessageListener listener, IFilter filter);
	
	/**
	 *  Remove a message listener.
	 *  @param listener The change listener.
	 */
	public IFuture<Void> removeMessageListener(IMessageListener listener);
	
	/** 
	 *  Adds preprocessors to the encoding stage.
	 *  @param Preprocessors.
	 */
	IFuture<Void> addPreprocessors(ITraverseProcessor[] processors);
	
	/** 
	 *  Adds postprocessors to the encoding stage.
	 *  @param Postprocessors.
	 */
	IFuture<Void> addPostprocessors(ITraverseProcessor[] processors);
	
	/**
	 *  Add message codec.
	 *  @param codec The codec.
	 */
	public IFuture<Void> addBinaryCodec(ICodec codec);
	
	/**
	 *  Remove message codec.
	 *  @param codec The codec.
	 */
	public IFuture<Void> removeBinaryCodec(ICodec codec);
	
	/**
	 *  Announce that addresses of transports might have changed.
	 */
	public IFuture<Void>	refreshAddresses();
	
//	/**
//	 *  Update component identifier with current addresses.
//	 *  @param cid The component identifier.
//	 *  @return The component identifier.
//	 */
//	public IFuture<ITransportComponentIdentifier> updateComponentIdentifier(ITransportComponentIdentifier cid);
	
	/**
	 *  Get addresses of all transports.
	 *  @return The addresses of all transports.
	 */
	public IFuture<String[]> getAddresses();
	
	/**
	 *  Get addresses of all transports.
	 *  @return The address schemes of all transports.
	 */
	public String[] getAddressSchemes();

	// todo: remove these method or futurize the first
	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	@Excluded
	public MessageType getMessageType(String type);
	
	/**
	 *  Get the serializers.
	 *  @return The serializer.
	 */
	public IFuture<Map<Byte, ISerializer>> getAllSerializers();
	
	/**
	 *  Get the codecs with message codecs.
	 *  @return The codec factory.
	 */
	public IFuture<Map<Byte, ICodec>> getAllCodecs();
	
	/**
	 *  Get the serializers and codecs.
	 *  @return The serializer and codecs.
	 */
	public IFuture<Tuple2<Map<Byte, ISerializer>, Map<Byte, ICodec>>> getAllSerializersAndCodecs();
	
	/**
	 *  Get the default codecs.
	 *  @return The default codecs.
	 */
	public IFuture<ICodec[]> getDefaultCodecs();
}
