package jadex.platform.service.message;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import jadex.bridge.IInternalAccess;
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
	@Argument(name="relayawaonly", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="binarymessages", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="strictcom", clazz=boolean.class, defaultvalue="false"),
})
//@ProvidedServices(@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new jadex.platform.service.message.MessageService($component, $component.getLogger(), new jadex.platform.service.message.transport.ITransport[]{$args.localtransport? new jadex.platform.service.message.transport.localmtp.LocalTransport($component): null, $args.tcptransport? new jadex.platform.service.message.transport.tcpmtp.TCPTransport($component, $args.tcpport): null, MessageAgent.createTransport(\"niotcpmtp.NIOTCPTransport\", new Object[] {$args.niotcptransport, $component, $args.niotcpport, $component.getLogger()}), $args.ssltcptransport? jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport.create($component, $args.ssltcpport): null, $args.relaytransport? new jadex.platform.service.message.transport.httprelaymtp.HttpRelayTransport($component, $args.relayaddress, $args.relaysecurity): null, MessageAgent.createTransport(\"udpmtp.UdpTransport\", $component)}, new jadex.bridge.service.types.message.MessageType[]{new jadex.bridge.fipa.FIPAMessageType()}, null, $args.binarymessages? jadex.bridge.fipa.SFipa.JADEX_BINARY: jadex.bridge.fipa.SFipa.JADEX_XML, $args.binarymessages? new jadex.platform.service.message.transport.codecs.CodecFactory(null, new Class[]{jadex.platform.service.message.transport.codecs.JadexBinaryCodec.class, jadex.platform.service.message.transport.codecs.GZIPCodec.class} ): new jadex.platform.service.message.transport.codecs.CodecFactory(), $args.strictcom)", proxytype=Implementation.PROXYTYPE_RAW)))
@ProvidedServices(@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new jadex.platform.service.message.MessageService($component, $component.getLogger(), MessageAgent.createTransports($component, $args), new jadex.bridge.service.types.message.MessageType[]{new jadex.bridge.fipa.FIPAMessageType()}, null, $args.binarymessages? jadex.bridge.fipa.SFipa.JADEX_BINARY: jadex.bridge.fipa.SFipa.JADEX_XML, $args.binarymessages? new jadex.platform.service.message.transport.codecs.CodecFactory(null, new Class[]{jadex.platform.service.message.transport.codecs.JadexBinaryCodec.class, jadex.platform.service.message.transport.codecs.GZIPCodec.class} ): new jadex.platform.service.message.transport.codecs.CodecFactory(), $args.strictcom)", proxytype=Implementation.PROXYTYPE_RAW)))
@Properties(value=@NameValue(name="system", value="true"))
public class MessageAgent
{
	/** Transport configuration
	 *  Format: <Fully-qualified Classname> <Enable Flag in Arguments> <Constructor arguments from component args or "component" for component itself or "componentlogger" for logger>
	 */
	public static final String[] TPCONF = new String[]
	{//  Class																		EnableArg			Constructor Args
		"jadex.platform.service.message.transport.localmtp.LocalTransport",			"localtransport",	"component",
		"jadex.platform.service.message.transport.tcpmtp.TCPTransport",				"tcptransport",		"component,tcpport",
		"jadex.platform.service.message.transport.niotcpmtp.NIOTCPTransport", 		"niotcptransport",	"component,niotcpport,componentlogger",
		"jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport", 		"ssltcptransport",	"component,ssltcpport",
		"jadex.platform.service.message.transport.httprelaymtp.HttpRelayTransport",	"relaytransport",	"component,relayaddress,relaysecurity,relayawaonly",
		"com.actoron.platform.service.message.transport.udpmtp.UdpTransport", 		"null",				"component"
	};
	
	/**
	 *  Initializes the transports.
	 *  
	 *  @param ia Component.
	 *  @param args The component arguments.
	 *  @return Set of transports.
	 */
	public static ITransport[] createTransports(IInternalAccess ia, LinkedHashMap<String, Object> args)
	{
		if (TPCONF.length % 3 != 0)
		{
			throw new IllegalArgumentException("Transport configuration broken.");
		}
		ITransport[] transports = new ITransport[TPCONF.length / 3];
		
		for (int i = 0; i < TPCONF.length; i = i + 3)
		{
			String clazz = TPCONF[i];
			if (!Boolean.FALSE.equals(args.get(TPCONF[i + 1])))
			{
				List<Object> conargs = new ArrayList<Object>();
				String[] tokens = TPCONF[i + 2].split(",");
				for (int j = 0; j < tokens.length; ++j)
				{
					if (tokens[j].length() > 0)
					{
						if ("component".equals(tokens[j].trim()))
							conargs.add(ia);
						else if ("componentlogger".equals(tokens[j].trim()))
							conargs.add(ia.getLogger());
						else
							conargs.add(args.get(tokens[j].trim()));
					}
				}
				
				transports[i / 3] = createTransport(ia, clazz, conargs.toArray());
			}
		}
		return transports;
	}
	
	/**
	 *  Attempts to instantiate a transport from a class name and arguments only.
	 *  
	 *  @param classname Fully-qualified class name.
	 *  @param arguments Constructor arguments to match as array or single object for a single object constructor, can be null.
	 *  @return Object of the class or null on failure.
	 */
	public static ITransport createTransport(IInternalAccess ia, String classname, Object arguments)
	{
		ITransport ret = null;
		
		try
		{
			Class<?> clazz = SReflect.classForName0(classname, ia.getClassLoader());
			if (clazz == null)
			{
				clazz = SReflect.classForName0("jadex.platform.service.message.transport." + classname, ia.getClassLoader());
			}
			if (clazz == null)
			{
				clazz = SReflect.classForName("com.actoron.platform.service.message.transport." + classname, ia.getClassLoader());
			}
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
//			e.printStackTrace();
		}
		
		return ret;
	}
}
