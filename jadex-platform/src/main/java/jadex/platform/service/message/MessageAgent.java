package jadex.platform.service.message;

import jadex.bridge.service.types.message.IMessageService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the message service.
 */
@Agent
@Arguments({
	@Argument(name="localtransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="tcptransport", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="niotcptransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="ssltcptransport", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="relaytransport", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="tcpport", clazz=int.class),
	@Argument(name="niotcpport", clazz=int.class),
	@Argument(name="ssltcpport", clazz=int.class),
	@Argument(name="relayaddress", clazz=String.class),
	@Argument(name="relaysecurity", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="binarymessages", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="strictcom", clazz=boolean.class, defaultvalue="false"),
})
@ProvidedServices(@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new jadex.platform.service.message.MessageService($component, $component.getLogger(), new jadex.platform.service.message.transport.ITransport[]{$args.localtransport? new jadex.platform.service.message.transport.localmtp.LocalTransport($component): null, $args.tcptransport? new jadex.platform.service.message.transport.tcpmtp.TCPTransport($component, $args.tcpport): null, $args.niotcptransport? new jadex.platform.service.message.transport.niotcpmtp.NIOTCPTransport($component, $args.niotcpport, $component.getLogger()): null, $args.ssltcptransport? jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport.create($component, $args.ssltcpport): null, $args.relaytransport? new jadex.platform.service.message.transport.httprelaymtp.HttpRelayTransport($component, $args.relayaddress, $args.relaysecurity): null}, new jadex.bridge.service.types.message.MessageType[]{new jadex.bridge.fipa.FIPAMessageType()}, null, $args.binarymessages? jadex.bridge.fipa.SFipa.JADEX_BINARY: jadex.bridge.fipa.SFipa.JADEX_XML, $args.binarymessages? new jadex.platform.service.message.transport.codecs.CodecFactory(null, new Class[]{jadex.platform.service.message.transport.codecs.JadexBinaryCodec.class, jadex.platform.service.message.transport.codecs.GZIPCodec.class} ): new jadex.platform.service.message.transport.codecs.CodecFactory(), $args.strictcom)", proxytype=Implementation.PROXYTYPE_RAW)))
@Properties(value=@NameValue(name="system", value="true"))
public class MessageAgent
{
}
