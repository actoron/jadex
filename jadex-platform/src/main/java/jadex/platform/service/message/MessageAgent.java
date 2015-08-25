package jadex.platform.service.message;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SReflect;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.message.transport.ITransport;

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
@ProvidedServices(@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new jadex.platform.service.message.MessageService($component, $component.getLogger(), new jadex.platform.service.message.transport.ITransport[]{$args.localtransport? new jadex.platform.service.message.transport.localmtp.LocalTransport($component): null, $args.tcptransport? new jadex.platform.service.message.transport.tcpmtp.TCPTransport($component, $args.tcpport): null, $args.niotcptransport? new jadex.platform.service.message.transport.niotcpmtp.NIOTCPTransport($component, $args.niotcpport, $component.getLogger()): null, $args.ssltcptransport? jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport.create($component, $args.ssltcpport): null, $args.relaytransport? new jadex.platform.service.message.transport.httprelaymtp.HttpRelayTransport($component, $args.relayaddress, $args.relaysecurity): null, MessageAgent.createTransport(\"com.actoron.platform.service.message.transport.udpmtp.UdpTransport\", $component)}, new jadex.bridge.service.types.message.MessageType[]{new jadex.bridge.fipa.FIPAMessageType()}, null, $args.binarymessages? jadex.bridge.fipa.SFipa.JADEX_BINARY: jadex.bridge.fipa.SFipa.JADEX_XML, $args.binarymessages? new jadex.platform.service.message.transport.codecs.CodecFactory(null, new Class[]{jadex.platform.service.message.transport.codecs.JadexBinaryCodec.class, jadex.platform.service.message.transport.codecs.GZIPCodec.class} ): new jadex.platform.service.message.transport.codecs.CodecFactory(), $args.strictcom)", proxytype=Implementation.PROXYTYPE_RAW)))
@Properties(value=@NameValue(name="system", value="true"))
public class MessageAgent
{
	/**
	 *  Attempts to instantiate a transport from a class name and arguments only.
	 *  
	 *  @param classname Fully-qualified class name.
	 *  @param arguments Constructor arguments to match as array or single object for a single object constructor, can be null.
	 *  @return Object of the class or null on failure.
	 */
	public static ITransport createTransport(String classname, Object arguments)
	{
		ITransport ret = null;
		
		try
		{
			Class<?> clazz = SReflect.classForName(classname, null);
			Constructor<?> con = null;
			Object[] args = null;
			if (arguments != null)
			{
				if (arguments.getClass().isArray())
				{
					int size = Array.getLength(arguments);
					args = new Object[size];
					for (int i = 0; i < size; ++i)
					{
						args[i] = Array.get(arguments, i);
					}
				}
				else
				{
					args = new Object[] {arguments};
				}
			}
			if (args == null || args.length == 0)
			{
				con = clazz.getConstructor((Class<?>) null);
			}
			else
			{
				Constructor<?>[] cons = clazz.getConstructors();
				for (int i = 0; i < cons.length && con == null; ++i)
				{
					Class<?>[] paramtypes = cons[i].getParameterTypes();
					if (paramtypes.length == args.length)
					{
						boolean match = true;
						for (int j = 0; j < paramtypes.length; ++j)
						{
							if (args[j] != null)
							{
								
								if (!SReflect.isSupertype(paramtypes[j], args[j].getClass()))
								{
									match = false;
									break;
								}
							}
						}
						if (match)
						{
							con = cons[i];
							break;
						}
					}
				}
			}
			ret = (ITransport) con.newInstance(args);
		}
		catch (Exception e)
		{
		}
		
		return ret;
	}
}
