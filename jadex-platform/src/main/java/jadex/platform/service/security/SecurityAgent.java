package jadex.platform.service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.security.handshake.BasicSecurityMessage;
import jadex.platform.service.security.handshake.HandshakeRejectionMessage;
import jadex.platform.service.security.handshake.InitialHandshakeMessage;
import jadex.platform.service.security.handshake.InitialHandshakeReplyMessage;
import jadex.platform.service.security.impl.Blake2bX509AuthenticationSuite;
import jadex.platform.service.security.impl.IAuthenticationSuite;

/**
 *  Agent that provides the security service.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Arguments({
	@Argument(name="cryptosuites", clazz=String[].class),
	@Argument(name="usepass", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="printpass", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="trustedlan", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="networkname", clazz=String[].class),
	@Argument(name="networkpass", clazz=String[].class),
	@Argument(name="virtualnames", clazz=String[].class),
	@Argument(name="validityduration", clazz=long.class)
})
@ProvidedServices(@ProvidedService(type=ISecurityService.class))
@Properties(value=@NameValue(name="system", value="true"))
public class SecurityAgent implements ISecurityService
{
	/** Header property for security messages. */
	protected static final String SECURITY_MESSAGE = "__securitymessage__";
	
	/** Component access. */
	@Agent
	protected IInternalAccess agent;
	
	/** Local platform authentication secrets. */
	protected List<AbstractAuthenticationSecret> platformsecrets;
	
	/** Remote platform authentication secrets. */
	protected Map<IComponentIdentifier, AbstractAuthenticationSecret> remoteplatformsecrets;
	
	/** Available virtual networks. */
	protected Map<String, AbstractAuthenticationSecret> networks;
	
	/** Available crypt suites. */
	protected Map<String, Class<?>> allowedcryptosuites;
	
	/** CryptoSuites currently initializing, value=Handshake state. */
	protected Map<String, HandshakeState> initializingcryptosuites;
	
	/** CryptoSuites currently in use. */
	protected Map<String, ICryptoSuite> currentcryptosuites;
	
	/** CryptoSuites that are expiring with expiration time. */
	protected Map<String, Tuple2<ICryptoSuite, Long>> expiringcryptosuites;
	
	/** The default authentication suite. */
	protected IAuthenticationSuite defaultauthenticationsuite;
	
	/** Available authentication suites. */
	protected Map<Integer, IAuthenticationSuite> authenticationsuites;
	
	/**
	 *  Initializiation.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		if (agent.getComponentIdentifier().getLocalName() != "security")
			agent.getLogger().warning("Security agent running as \"" + agent.getComponentIdentifier().getLocalName() +"\" instead of \"security\".");
		
		platformsecrets = new ArrayList<AbstractAuthenticationSecret>();
		remoteplatformsecrets = new HashMap<IComponentIdentifier, AbstractAuthenticationSecret>();
		networks = new HashMap<String, AbstractAuthenticationSecret>();
		
		initializingcryptosuites = new HashMap<String, HandshakeState>();
		currentcryptosuites = new HashMap<String, ICryptoSuite>();
		expiringcryptosuites = new HashMap<String, Tuple2<ICryptoSuite,Long>>();
		
		defaultauthenticationsuite = new Blake2bX509AuthenticationSuite();
		authenticationsuites = new HashMap<Integer, IAuthenticationSuite>();
		authenticationsuites.put(defaultauthenticationsuite.getId(), defaultauthenticationsuite);
		
		String[] cryptsuites = (String[]) agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("cryptosuites");
		if (cryptsuites == null)
		{
			cryptsuites = new String[] { "jadex.platform.service.security.impl.Curve448ChaCha20Poly1305Suite" };
		}
		allowedcryptosuites = new HashMap<String, Class<?>>();
		for (String cryptsuite : cryptsuites)
		{
			try
			{
				Class<?> clazz = Class.forName(cryptsuite, true, agent.getClassLoader());
				allowedcryptosuites.put(cryptsuite, clazz);
			}
			catch (Exception e)
			{
				return new Future<Void>(e);
			}
		}
		agent.getComponentFeature0(IMessageFeature.class).setAllowUntrusted(true);
		agent.getComponentFeature0(IMessageFeature.class).addMessageHandler(new SecurityMessageHandler());
		return IFuture.DONE;
	}
	
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param receiver The receiver.
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public IFuture<byte[]> encryptAndSign(Map<String, Object> header, final byte[] content)
	{
		final Future<byte[]> ret = new Future<byte[]>();
		
		if (isSecurityMessage(header))
		{
			byte[] newcontent = new byte[content.length + 1];
			newcontent[0] = -1;
			System.arraycopy(content, 0, newcontent, 1, content.length);
			ret.setResult(newcontent);
		}
		else
		{
			String rplat = ((IComponentIdentifier) header.get(MessageComponentFeature.RECEIVER)).getRoot().toString();
			ICryptoSuite cs = currentcryptosuites.get(rplat);
			if (cs != null)
			{
				try
				{
					ret.setResult(cs.encryptAndSign(content));
				}
				catch (Exception e)
				{
					ret.setException(e);
				}
			}
			else
			{
				HandshakeState hstate = initializingcryptosuites.get(rplat);
				if (hstate == null)
				{
					String convid = SUtil.createUniqueId(agent.getComponentIdentifier().getRoot().toString());
					hstate = new HandshakeState();
					hstate.setConversationId(convid);
					hstate.setResultfut(new Future<ICryptoSuite>());
					
					initializingcryptosuites.put(rplat, hstate);
					
					String[] csuites = allowedcryptosuites.keySet().toArray(new String[allowedcryptosuites.size()]);
					InitialHandshakeMessage ihm = new InitialHandshakeMessage(agent.getComponentIdentifier(), convid, csuites);
					BasicComponentIdentifier rsec = new BasicComponentIdentifier("security@" + rplat);
					sendSecurityHandshakeMessage(rsec, ihm);
				}
				
				hstate.getResultFuture().addResultListener(new ExceptionDelegationResultListener<ICryptoSuite, byte[]>(ret)
				{
					public void customResultAvailable(ICryptoSuite result) throws Exception
					{
						try
						{
							ret.setResult(result.encryptAndSign(content));
						}
						catch (Exception e)
						{
							ret.setException(e);
						}
					}
				});
			}
		}
		
		return ret;
	}
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param sender The sender.
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public IFuture<Tuple2<IMsgSecurityInfos,byte[]>> decryptAndAuth(IComponentIdentifier sender, byte[] content)
	{
		final Future<Tuple2<IMsgSecurityInfos, byte[]>> ret = new Future<Tuple2<IMsgSecurityInfos,byte[]>>();
		
		if (content.length > 0 && content[0] == -1)
		{
			// Security message
			byte[] newcontent = new byte[content.length - 1];
			System.arraycopy(content, 1, newcontent, 0, newcontent.length);
			MsgSecurityInfos secinfos = new MsgSecurityInfos();
			Tuple2<IMsgSecurityInfos,byte[]> tup = new Tuple2<IMsgSecurityInfos, byte[]>(secinfos, newcontent);
			ret.setResult(tup);
		}
		else
		{
			final IComponentIdentifier splat = sender.getRoot();
			ICryptoSuite cs = currentcryptosuites.get(splat);
			byte[] cleartext = null;
			
			if (cs != null)
			{
				cleartext = cs.decryptAndAuth(content);
			}
			
			if (cleartext == null)
			{
				Tuple2<ICryptoSuite, Long> tup = expiringcryptosuites.get(splat);
				if (tup != null)
				{
					cs = tup.getFirstEntity();
					cleartext = cs.decryptAndAuth(content);
				}
			}
			
			if (cleartext == null)
			{
				HandshakeState hstate = initializingcryptosuites.get(splat);
				if (hstate != null)
				{
					final byte[] fcontent = content;
					hstate.getResultFuture().addResultListener(new IResultListener<ICryptoSuite>()
					{
						public void resultAvailable(ICryptoSuite result)
						{
							byte[] cleartext = result.decryptAndAuth(fcontent);
							if (cleartext != null)
							{
								ret.setResult(new Tuple2<IMsgSecurityInfos, byte[]>(result.getSecurityInfos(), cleartext));
							}
							else
							{
								ret.setException(new SecurityException("Could not establish secure communication with: " + splat.toString()));
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
				else
				{
					ret.setException(new SecurityException("Could not establish secure communication with: " + splat.toString()));
				}
			}
			
			if (cleartext != null)
			{
				ret.setResult(new Tuple2<IMsgSecurityInfos, byte[]>(cs.getSecurityInfos(), cleartext));
			}
		}
		return ret;
	}
	
	//-------- Information access -------
	
	public IComponentIdentifier getComponentIdentifier()
	{
		return agent.getComponentIdentifier();
	}
	
	/**
	 *  Get access to the stored virtual network configurations.
	 * 
	 *  @return The stored virtual network configurations.
	 */
	public Map<String, AbstractAuthenticationSecret> getNetworks()
	{
		return networks;
	}
	
	//-------- Utility functions -------
	
	/**
	 *  Creates a crypto suite of a particular name.
	 * 
	 *  @param name Name of the suite.
	 *  @return The suite, null if not found.
	 */
	protected ICryptoSuite createCryptoSuite(String name)
	{
		ICryptoSuite ret = null;
		try
		{
			Class<?> clazz = allowedcryptosuites.get(name);
			if (clazz != null)
			{
				ret = (ICryptoSuite) clazz.newInstance();
			}
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Sends a security handshake message.
	 * 
	 *  @param receiver Receiver of the message.
	 *  @param message The message.
	 *  @return Null, when sent.
	 */
	public void sendSecurityHandshakeMessage(final IComponentIdentifier receiver, Object message)
	{
		sendSecurityMessage(receiver, message).addResultListener(new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				HandshakeState state = initializingcryptosuites.remove(receiver.getRoot());
				if (state != null)
				{
					state.getResultFuture().setException(new SecurityException("Could not reach " + receiver + " for handshake."));
				}
			}
			
			public void resultAvailable(Void result)
			{	
			}
		});
	}
	
	/**
	 *  Sends a security message.
	 * 
	 *  @param receiver Receiver of the message.
	 *  @param message The message.
	 *  @return Null, when sent.
	 */
	protected IFuture<Void> sendSecurityMessage(IComponentIdentifier receiver, Object message)
	{
		Map<String, Object> addheader = new HashMap<String, Object>();
		addheader.put(SECURITY_MESSAGE, Boolean.TRUE);
		
		return agent.getComponentFeature(IMessageFeature.class).sendMessage(receiver, message, addheader);
	}
	
	/**
	 *  Checks if a message is a security message.
	 *  
	 *  @param header The message header.
	 *  @return True, if security message.
	 */
	protected static final boolean isSecurityMessage(Map<String, Object> header)
	{
		return Boolean.TRUE.equals(header.get(SECURITY_MESSAGE));
	}
	
	
	//-------- Message Handler -------
	
	/**
	 *  Security service message handler.
	 *
	 */
	protected class SecurityMessageHandler implements IMessageHandler
	{
		/**
		 *  Test if handler should handle a message.
		 *  @return True if it should handle the message. 
		 */
		public boolean isHandling(IMsgSecurityInfos secinfos, Map<String, Object> header, Object msg)
		{
			return isSecurityMessage(header);
		}
		
		/**
		 *  Test if handler should be removed.
		 *  @return True if it should be removed. 
		 */
		public boolean isRemove()
		{
			return false;
		}
		
		/**
		 *  Handle the message.
		 *  @param header The header.
		 *  @param msg The message.
		 */
		public void handleMessage(IMsgSecurityInfos secinfos, Object messageid, Object msg)
		{
			if (msg instanceof InitialHandshakeMessage)
			{
				final InitialHandshakeMessage imsg = (InitialHandshakeMessage) msg;
				System.out.println("Got initial handshake: " + imsg.getConversationId());
				
				final Future<ICryptoSuite> fut = new Future<ICryptoSuite>();
				
				HandshakeState state = initializingcryptosuites.remove(imsg.getSender().getRoot().toString());
				
				// Check if handshake is already happening. 
				if (state != null)
				{
					if (getComponentIdentifier().getRoot().toString().compareTo(imsg.getSender().getRoot().toString()) < 0)
						fut.addResultListener(new DelegationResultListener<ICryptoSuite>(state.getResultFuture()));
					else
						return;
				}
				
				if (imsg.getCryptoSuites() == null || imsg.getCryptoSuites().length < 1)
					return;
				
				Set<String> offeredsuites = new HashSet<String>(Arrays.asList(imsg.getCryptoSuites()));
				
				String chosensuite = null;
				for (String suite : allowedcryptosuites.keySet())
				{
					if (offeredsuites.contains(suite))
					{
						chosensuite = suite;
						break;
					}
				}
				
				if (chosensuite == null)
					return;
				
				state = new HandshakeState();
				state.setResultfut(fut);
				state.setConversationId(imsg.getConversationId());
				initializingcryptosuites.put(imsg.getSender().toString(), state);
				
				
				InitialHandshakeReplyMessage reply = new InitialHandshakeReplyMessage(getComponentIdentifier(), state.getConversationId(), chosensuite);
				
				sendSecurityHandshakeMessage(imsg.getSender(), reply);
			}
			else if (msg instanceof InitialHandshakeReplyMessage)
			{
				InitialHandshakeReplyMessage rm = (InitialHandshakeReplyMessage) msg;
				HandshakeState state = initializingcryptosuites.get(rm.getSender().getRoot().toString());
				if (state != null)
				{
					String convid = state.getConversationId();
					if (convid != null && convid.equals(rm.getConversationId()))
					{
						ICryptoSuite suite = createCryptoSuite(rm.getChosenCryptoSuite());
						
						if (suite == null)
						{
							initializingcryptosuites.remove(rm.getSender().getRoot().toString());
							state.getResultFuture().setException(new SecurityException("Handshake with remote platform " + rm.getSender().getRoot().toString() + " failed."));
						}
						else
						{
							state.setCryptoSuite(suite);
							if (!suite.handleHandshake(SecurityAgent.this, rm))
							{
								System.out.println("Finished handshake: " + rm.getSender());
								state.getResultFuture().setResult(state.getCryptoSuite());
							}
						}
					}
				}
			}
			else if (msg instanceof BasicSecurityMessage)
			{
				BasicSecurityMessage secmsg = (BasicSecurityMessage) msg;
				HandshakeState state = initializingcryptosuites.get(secmsg.getSender().getRoot().toString());
				if (state != null && state.getConversationId().equals(secmsg.getConversationId()) && state.getCryptoSuite() != null)
				{
					try
					{
						if (!state.getCryptoSuite().handleHandshake(SecurityAgent.this, secmsg))
						{
							System.out.println("Finished handshake: " + secmsg.getSender());
							state.getResultFuture().setResult(state.getCryptoSuite());
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						state.getResultFuture().setException(e);
						initializingcryptosuites.remove(secmsg.getSender().getRoot());
					}
				}
			}
		}
	}
}
