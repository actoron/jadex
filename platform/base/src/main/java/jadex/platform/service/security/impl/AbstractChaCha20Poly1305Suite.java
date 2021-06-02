package jadex.platform.service.security.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.commons.ByteArrayWrapper;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;
import jadex.platform.service.security.auth.AbstractX509PemSecret;
import jadex.platform.service.security.auth.AuthToken;
import jadex.platform.service.security.auth.Blake2bX509AuthenticationSuite;
import jadex.platform.service.security.auth.IAuthenticationSuite;
import jadex.platform.service.security.auth.X509AuthToken;
import jadex.platform.service.security.auth.X509PemStringsSecret;
import jadex.platform.service.security.handshake.BasicSecurityMessage;
import jadex.platform.service.security.handshake.InitialHandshakeFinalMessage;

/**
 *  Crypto suite based on Curve448 and ChaCha20-Poly1305 AEAD.
 *
 */
public abstract class AbstractChaCha20Poly1305Suite extends AbstractCryptoSuite
{
	//--------------- Handshake state -------------------
	
	
	/** The ephemeral key. */
	protected Object ephemeralkey;
	
	/** The remote public key */
	protected byte[] remotepublickey;
	
	/** Agreed-on random state / challenge. */
	protected byte[] challenge;
	
	/** The authentication suite. */
	protected IAuthenticationSuite authsuite;
	
	/** Next step in the handshake protocol. */
	protected int nextstep;
	
	/** Hashed network names reverse lookup */
	//protected MultiCollection<ByteArrayWrapper, Tuple2<String, AbstractAuthenticationSecret>> hashednetworks;
	protected Map<ByteArrayWrapper, String> hashednetworknames;
	
	// -------------- Operational state -----------------
	
	/** The ChaCha20 key. */
	protected int[] key = new int[8];
	
	/** The current message ID. */
	protected AtomicLong msgid = new AtomicLong(AbstractCryptoSuite.MSG_ID_START);
	
	/** Prefix used for the ChaCha20 nonce. */
	protected int nonceprefix;
	
	/**
	 *  Creates the suite.
	 */
	public AbstractChaCha20Poly1305Suite()
	{
	}
	
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param receiver The receiver.
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public byte[] encryptAndSign(byte[] content)
	{
		return chacha20Poly1305Enc(content, key, nonceprefix, msgid.getAndIncrement());
	}
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param sender The sender.
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public byte[] decryptAndAuth(byte[] content)
	{
		byte[] ret = chacha20Poly1305Dec(content, key, ~nonceprefix);
		if (ret != null && !isValid(Pack.littleEndianToLong(content, 8)))
			ret = null;
		return ret;
	}
	
	/**
	 *  Decrypt and authenticates a locally encrypted message.
	 *  
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public byte[] decryptAndAuthLocal(byte[] content)
	{
		byte[] ret = chacha20Poly1305Dec(content, key, nonceprefix);
		return ret;
	}
	
	/**
	 *  Gets the security infos related to the authentication state.
	 *  
	 *  @return The security infos for decrypted messages.
	 */
	public ISecurityInfo getSecurityInfos()
	{
		return secinf;
	}
	
	/**
	 *  Returns if the suite is expiring and should be replaced.
	 *  
	 *  @return True, if the suite is expiring and should be replaced.
	 */
	public boolean isExpiring()
	{
		return msgid.get() < AbstractCryptoSuite.MSG_ID_START;
	}
	
	/**
	 *  Handles handshake messages.
	 *  
	 *  @param agent The security agent object.
	 *  @param incomingmessage A message received from the other side of the handshake,
	 *  					   set to null for initial message.
	 *  @return True, if handshake continues, false when finished.
	 *  @throws SecurityException if handshake failed.
	 */
	public boolean handleHandshake(SecurityAgent agent, BasicSecurityMessage incomingmessage)
	{
		boolean ret = true;
		
//		System.out.println("got message " + incomingmessage.getClass().getName() + " " + incomingmessage.getConversationId() + " " + incomingmessage.getMessageId() + " " + nextstep + " " + System.identityHashCode(this));
		
		if (nextstep == 0 && incomingmessage instanceof InitialHandshakeFinalMessage)
		{
//			ts = System.currentTimeMillis();
			authsuite = new Blake2bX509AuthenticationSuite();
			StartExchangeMessage sem = new StartExchangeMessage(agent.getComponentIdentifier(), incomingmessage.getConversationId());
			challenge = new byte[32];
			SSecurity.getSecureRandom().nextBytes(challenge);
			sem.setChallenge(challenge);
			sem.setPakeRound1Data(authsuite.getPakeRound1(agent, incomingmessage.getSender().getRoot()));
			
			agent.sendSecurityHandshakeMessage(incomingmessage.getSender(), sem);
			nextstep = 1;
		}
		else if (nextstep == -1 && incomingmessage instanceof StartExchangeMessage)
		{
			StartExchangeMessage sem = (StartExchangeMessage) incomingmessage;
			
			AckExchangeMessage reply = new AckExchangeMessage(agent.getComponentIdentifier(), sem.getConversationId());
			challenge = new byte[32];
			SSecurity.getSecureRandom().nextBytes(challenge);
			reply.setChallenge(challenge);
			
			challenge = new byte[32];
			Blake2bDigest dig = new Blake2bDigest(256);
			dig.update(sem.getChallenge(), 0, sem.getChallenge().length);
			dig.update(reply.getChallenge(), 0, reply.getChallenge().length);
			dig.doFinal(challenge, 0);
			
			authsuite = new Blake2bX509AuthenticationSuite();
			
			IComponentIdentifier remoteid = sem.getSender().getRoot();
			try
			{
				reply.setPakeRound1Data(authsuite.getPakeRound1(agent, remoteid));
				reply.setPakeRound2Data(authsuite.getPakeRound2(agent, remoteid, sem.getPakeRound1Data()));
			}
			catch (Exception e)
			{
			}
			
			agent.sendSecurityHandshakeMessage(incomingmessage.getSender(), reply);
			nextstep = -2;
			
			hashednetworknames = getHashedNetworkNames(agent.getInternalNetworks().keySet(), challenge);
		}
		else if (nextstep == 1 && incomingmessage instanceof AckExchangeMessage)
		{
			AckExchangeMessage ack = (AckExchangeMessage) incomingmessage;
			
			Blake2bDigest dig = new Blake2bDigest(256);
			dig.update(challenge, 0, challenge.length);
			dig.update(ack.getChallenge(), 0, ack.getChallenge().length);
			challenge = new byte[32];
			dig.doFinal(challenge, 0);
			
			hashednetworknames = getHashedNetworkNames(agent.getInternalNetworks().keySet(), challenge);
			
			IComponentIdentifier remoteid = ack.getSender().getRoot();
			
			KeyExchangeMessage reply = new KeyExchangeMessage(agent.getComponentIdentifier(), ack.getConversationId());
			
			try
			{
				reply.setPakeRound2Data(authsuite.getPakeRound2(agent, remoteid, ack.getPakeRound1Data()));
				authsuite.finalizePake(agent, remoteid, ack.getPakeRound2Data());
			}
			catch (Exception e)
			{
			}
			
			ephemeralkey = createEphemeralKey();
			byte[] pubkey = getPubKey();
			reply.setPublicKey(pubkey);
			
			if (agent.getInternalUsePlatformSecret())
				reply.setPlatformSecretSigs(getPlatformSignatures(pubkey, agent, remoteid));
			reply.setNetworkSigs(getNetworkSignatures(pubkey, agent.getInternalNetworks()));
			
			reply.setPlatformNameSig(getPlatformNameSignature(pubkey, agent.getInternalPlatformNameCertificate()));
			
			agent.sendSecurityHandshakeMessage(incomingmessage.getSender(), reply);
			nextstep = 2;
		}
		else if (nextstep == -2 && incomingmessage instanceof KeyExchangeMessage)
		{
			KeyExchangeMessage kx = (KeyExchangeMessage) incomingmessage;
			
			IComponentIdentifier remoteid = kx.getSender().getRoot();
			
			try
			{
				authsuite.finalizePake(agent, remoteid, kx.getPakeRound2Data());
			}
			catch (Exception e)
			{
			}
			
			remotepublickey = kx.getPublicKey();
			
			String remotepfname = kx.getSender().getRoot().toString();
			String authenticatedpfname = null;
			if (verifyPlatformNameSignature(remotepublickey, kx.getPlatformNameSig(), agent.getInternalNameAuthorities(), remotepfname))
				authenticatedpfname = remotepfname;
			
			boolean platformauth = verifyPlatformSignatures(remotepublickey, kx.getPlatformSecretSigs(), agent.getInternalPlatformSecret());
			platformauth &= agent.getInternalUsePlatformSecret();
			List<String> authnets = verifyNetworkSignatures(remotepublickey, kx.getNetworkSigs(), agent.getInternalNetworks());
			setupSecInfos(remoteid, authnets, platformauth, authenticatedpfname, agent);
			
			ephemeralkey = createEphemeralKey();
			byte[] pubkey = getPubKey();
			
			nonceprefix = Pack.littleEndianToInt(challenge, 0);
			
			KeyExchangeMessage reply = new KeyExchangeMessage(agent.getComponentIdentifier(), kx.getConversationId());
			reply.setPublicKey(pubkey);
			if (agent.getInternalUsePlatformSecret())
					reply.setPlatformSecretSigs(getPlatformSignatures(pubkey, agent, remoteid));
			reply.setNetworkSigs(getNetworkSignatures(pubkey, agent.getInternalNetworks()));
			
			reply.setPlatformNameSig(getPlatformNameSignature(pubkey, agent.getInternalPlatformNameCertificate()));
			
			agent.sendSecurityHandshakeMessage(incomingmessage.getSender(), reply);
			nextstep = -3;
		}
		else if (nextstep == 2 && incomingmessage instanceof KeyExchangeMessage)
		{
			KeyExchangeMessage kx = (KeyExchangeMessage) incomingmessage;
			
			IComponentIdentifier remoteid = kx.getSender().getRoot();
			
			remotepublickey = kx.getPublicKey();
			
			String remotepfname = kx.getSender().getRoot().toString();
			String authenticatedpfname = null;
			if (verifyPlatformNameSignature(remotepublickey, kx.getPlatformNameSig(), agent.getInternalNameAuthorities(), remotepfname))
				authenticatedpfname = remotepfname;
			
			boolean platformauth = verifyPlatformSignatures(remotepublickey, kx.getPlatformSecretSigs(), agent.getInternalPlatformSecret());
			platformauth &= agent.getInternalUsePlatformSecret();
			List<String> authnets = verifyNetworkSignatures(remotepublickey, kx.getNetworkSigs(), agent.getInternalNetworks());
			setupSecInfos(remoteid, authnets, platformauth, authenticatedpfname, agent);
			
			// Removed, checked during setupsecinf
//			if (agent.getInternalRefuseUnauth() && (secinf.getRoles() == null || secinf.getRoles().isEmpty()))
//				throw new SecurityException("Unauthenticated connection not allowed.");
			
			nonceprefix = Pack.littleEndianToInt(challenge, 0);
			nonceprefix = ~nonceprefix;
			key = generateChaChaKey();
//			System.out.println("Shared Key1: " + Arrays.toString(key) + " " + secinf.isAuthenticated());
			
			// Delete handshake state
			ephemeralkey = null;
			challenge = null;
			hashednetworknames = null;
			remotepublickey = null;
			authsuite = null;
			
			ReadyMessage rdy = new ReadyMessage(agent.getComponentIdentifier(), kx.getConversationId());
			agent.sendSecurityHandshakeMessage(kx.getSender(), rdy);
			
			ret = false;
			nextstep = Integer.MAX_VALUE;
//			System.out.println("Handshake took: " + (System.currentTimeMillis() - ts));
		}
		else if (nextstep == -3 && incomingmessage instanceof ReadyMessage)
		{
			key = generateChaChaKey();
//			System.out.println("Shared Key2: " + Arrays.toString(key) + " " + secinf.isAuthenticated());
			
			// Delete handshake state
			ephemeralkey = null;
			remotepublickey = null;
			challenge = null;
			hashednetworknames = null;
			authsuite = null;
			
			ret = false;
			nextstep = Integer.MIN_VALUE;
		}
		else
		{
			throw new SecurityException("Protocol violation detected.");
		}
		
		return ret;
	}
	
	/**
	 *  Sets if the suite represents the initializer.
	 * @param initializer True, if initializer.
	 */
	public void setInitializer(boolean initializer)
	{
		if (initializer)
			nextstep = -1;
	}
	
	/**
	 *  Destroy information.
	 */
	public void destroy()
	{
		ephemeralkey = null;
		challenge = null;
		authsuite = null;
		if (key != null)
		{
			byte[] raw = new byte[key.length << 2];
			SSecurity.getSecureRandom().nextBytes(raw);
			Pack.littleEndianToInt(raw, 0, key);
		}
		key = null;
		nonceprefix = 0;
		msgid.set(0);
		secinf = null;
	}
	
	/**
	 *  Signs a key with platform secrets.
	 *  
	 *  @param key The key (public key).
	 *  
	 *  @return Tuple of signatures local/remote.
	 */
	protected Tuple2<AuthToken, AuthToken> getPlatformSignatures(byte[] pubkey, SecurityAgent agent, IComponentIdentifier remoteid)
	{
		AuthToken local = null;
		AbstractAuthenticationSecret ps = agent.getInternalPlatformSecret();
		if (ps != null && ps.canSign())
			local = signKey(challenge, pubkey, ps);
		
		AuthToken remote = null;
		ps = agent.getInternalPlatformSecret(remoteid);
		if (ps != null && ps.canSign())
			remote = signKey(challenge, pubkey, ps);
		
		if (local != null || remote != null)
			return new Tuple2<>(local, remote);
		
		return null;
	}
	
	/**
	 *  Verifies platform signatures of a key.
	 *  
	 *  @param key The key.
	 *  @param networksigs The signatures.
	 *  @return List of network that authenticated the key.
	 */
	protected boolean verifyPlatformSignatures(byte[] key, Tuple2<AuthToken, AuthToken> sigs, AbstractAuthenticationSecret localsecret)
	{
		boolean ret = false;
		if (sigs != null)
		{
			if (sigs.getFirstEntity() != null)
			{
				ret = verifyKey(challenge, key, localsecret, sigs.getFirstEntity());
			}
			
			if (!ret && sigs.getSecondEntity() != null)
			{
				ret = verifyKey(challenge, key, localsecret, sigs.getSecondEntity());
			}
		}
		return ret;
	}
	
	/**
	 *  Generates a token verifying the platform name.
	 *  
	 *  @param key The key.
	 *  @param secret The name certificate.
	 *  @return The token.
	 */
	public AuthToken getPlatformNameSignature(byte[] key, AbstractX509PemSecret secret)
	{
		AuthToken ret = null;
		if (secret != null)
		{
			ret = signKey(challenge, key, secret);
		}
		return ret;
	}
	
	/**
	 *  Verifies a token verifying the platform name.
	 */
	public boolean verifyPlatformNameSignature(byte[] key, AuthToken platformnamesig, Set<X509CertificateHolder> nameauthorities, String platformname)
	{
		boolean ret = false;
		try
		{
			if (platformnamesig instanceof X509AuthToken)
			{
				X509AuthToken sig = (X509AuthToken) platformnamesig;
				for (X509CertificateHolder nameauthority : nameauthorities)
				{
//					X509PemStringsSecret nasecret = new X509PemStringsSecret(SSecurity.writeCertificateAsPEM(nameauthority), null, null);
					X509PemStringsSecret nasecret = new X509PemStringsSecret(SSecurity.writeCertificateAsPEM(nameauthority), null);
					boolean verified = verifyKey(challenge, key, nasecret, sig);
					if (verified)
					{
						ret = SSecurity.checkEntity(SSecurity.readCertificateFromPEM(sig.getCertificate()), platformname);
						if (ret)
							break;
					}
				}
			}
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Signs a key with network secrets.
	 *  
	 *  @param key The key (public key).
	 *  @param remotehnets The hashed names of remote networks.
	 *  
	 *  @return Map hashed network name -> signature.
	 */
	protected MultiCollection<ByteArrayWrapper, AuthToken> getNetworkSignatures(byte[] key, MultiCollection<String, AbstractAuthenticationSecret> networks)
	{
		MultiCollection<ByteArrayWrapper, AuthToken> networksigs = new MultiCollection<>();
		
		if (hashednetworknames.size() > 0)
		{
			for (Map.Entry<ByteArrayWrapper, String> entry : hashednetworknames.entrySet())
			{
				Collection<AbstractAuthenticationSecret> secrets = networks.get(entry.getValue());
				
				if (secrets != null && secrets.size() > 0)
				{
					for (AbstractAuthenticationSecret secret : secrets)
					{
						if (secret.canSign())
						{
							AuthToken sig = signKey(challenge, key, secret);
							if (sig != null)
								networksigs.add(entry.getKey(), sig);
						}
					}
				}
			}
		}
		return networksigs;
	}
	
	/**
	 *  Verifies network signatures of a key.
	 *  
	 *  @param key The key.
	 *  @param networksigs The signatures.
	 *  @return List of network that authenticated the key.
	 */
	protected List<String> verifyNetworkSignatures(byte[] key, MultiCollection<ByteArrayWrapper, AuthToken> networksigs, MultiCollection<String, AbstractAuthenticationSecret> networks)
	{
		List<String> ret = new ArrayList<String>();
		
		if (networksigs != null)
		{
			for (Map.Entry<ByteArrayWrapper, Collection<AuthToken>> nwsig : networksigs.entrySet())
			{
				String networkname = hashednetworknames.get(nwsig.getKey());
				if (networkname != null)
				{
					Collection<AbstractAuthenticationSecret> secrets = networks.get(networkname);
					if (secrets != null)
					{
						boolean authenticated = false;
						sigcheck:
						for (AbstractAuthenticationSecret secret : secrets)
						{
							for (AuthToken sig : nwsig.getValue())
							{
								authenticated = verifyKey(challenge, key, secret, sig);
								if (authenticated)
									break sigcheck;
							}
						}
						if (!authenticated)
							throw new SecurityException("Remote platform presented unverifiable network signature for network " + networkname + ", handshake terminated.");
						ret.add(networkname);
					}
				}
			}
		}
		Logger.getLogger("security").fine("Remote networks verified: " + Arrays.toString(ret.toArray()));
		return ret;
	}
	
	/**
	 *  Signs a key for authentication.
	 *  
	 *  @param challenge Nonce / challenge received from remote.
	 *  @param key The key to sign.
	 *  @param secret Secret used for authentication.
	 *  @return Signature.
	 */
	protected AuthToken signKey(byte[] challenge, byte[] key, AbstractAuthenticationSecret secret)
	{
		byte[] sigmsg = new byte[key.length + challenge.length];
		System.arraycopy(key, 0, sigmsg, 0, key.length);
		System.arraycopy(challenge, 0, sigmsg, key.length, challenge.length);
		return authsuite.createAuthenticationToken(sigmsg, secret);
	}
	
	/**
	 *  Verifies a key for authentication.
	 *  
	 *  @param challenge Nonce / challenge received from remote.
	 *  @param key The key to verify.
	 *  @param secret Secret used for authentication.
	 *  @return True, if authenticated.
	 */
	protected boolean verifyKey(byte[] challenge, byte[] key, AbstractAuthenticationSecret secret, AuthToken authtoken)
	{
		byte[] sigmsg = new byte[key.length + challenge.length];
		System.arraycopy(key, 0, sigmsg, 0, key.length);
		System.arraycopy(challenge, 0, sigmsg, key.length, challenge.length);
		return authsuite.verifyAuthenticationToken(sigmsg, secret, authtoken);
	}
	
	/**
	 *  Finalize.
	 */
	protected void finalize() throws Throwable
	{
		destroy();
	};
	
	/**
	 *  Hashes the network name.
	 *  
	 *  @param nwname The network name.
	 *  @param salt The salt.
	 *  @return Hashed network name.
	 */
	protected static final ByteArrayWrapper hashNetworkName(String nwname, byte[] salt)
	{
		Blake2bDigest dig = new Blake2bDigest(nwname.getBytes(SUtil.UTF8));
		byte[] hnwname = new byte[dig.getDigestSize()];
		dig.update(salt, 0, salt.length);
		dig.doFinal(hnwname, 0);
		return new ByteArrayWrapper(hnwname);
	}
	
	/**
	 *  Creates a reverse look-up map of hashed network names.
	 *  
	 *  @param networks The networks.
	 *  @param salt Salt to use.
	 *  @return Reverse look-up map.
	 */
	protected static final Map<ByteArrayWrapper, String> getHashedNetworkNames(Set<String> names, byte[] salt)
	{
		Map<ByteArrayWrapper, String> ret = new HashMap<>();
		for (String name : names)
			ret.put(hashNetworkName(name, salt), name);
		return ret;
	}
//	protected static final MultiCollection<ByteArrayWrapper, Tuple2<String, AbstractAuthenticationSecret>> getHashedNetworks(MultiCollection<String, AbstractAuthenticationSecret> networks, byte[] salt)
//	{
//		MultiCollection<ByteArrayWrapper, Tuple2<String, AbstractAuthenticationSecret>> ret = new MultiCollection<ByteArrayWrapper, Tuple2<String,AbstractAuthenticationSecret>>();
//		if (networks != null)
//		{
//			for (Map.Entry<String, Collection<AbstractAuthenticationSecret>> nw : networks.entrySet())
//			{
//				if (nw.getValue() != null)
//				{
//					ByteArrayWrapper namehash = new ByteArrayWrapper(hashNetworkName(nw.getKey(), salt));
//					for (AbstractAuthenticationSecret secret : nw.getValue())
//					{
//						Tuple2<String, AbstractAuthenticationSecret> tup;
//						tup = new Tuple2<String, AbstractAuthenticationSecret>(nw.getKey(), secret);
//						ret.add(namehash, tup);
//					}
//				}
//			}
//		}
//		return ret;
//	}
	
	/**
	 *  Encrypts content using an RFC 7539-like AEAD construction.
	 *  
	 *  @param content Clear text being encrypted.
	 *  @param key Key used for encryption/authentication.
	 *  @param nonceprefix Local nonce prefix used.
	 *  @param msgid Current message ID.
	 *  @return 
	 */
	protected static final byte[] chacha20Poly1305Enc(byte[] content, int[] key, int nonceprefix, long msgid)
	{
		int[] state = new int[16];
		int blockcount = 0;
		
		// Generate key for Poly1305 authentication (first chacha block).
		byte[] polykey = new byte[32];
		setupChaChaState(state, key, blockcount, nonceprefix, msgid);
		ChaChaEngine.chachaCore(20, state, state);
		for (int i = 0; i < 8; ++i)
			Pack.intToLittleEndian(state[i], polykey, i << 2);
		Poly1305KeyGenerator.clamp(polykey);
		++blockcount;
		
		// Pad content, leave 4 byte for clear text length.
		// Output: 
		// padding (8) / marker
		// message id (8)
		// padded and encrypted content (last encrypted 4 byte: clear text length)
		// Authenticator (16) 
		int retlen = pad16Size(content.length + 4);
		byte[] ret = new byte[retlen + 32];
		
		// Copy clear text for encryption
		System.arraycopy(content, 0, ret, 16, content.length);
		
		// Write clear text length
		Pack.intToLittleEndian(content.length, ret, ret.length - 20);
		
		// Write message ID.
		Pack.longToLittleEndian(msgid, ret, 8);
		
		// Encrypt content
		int pos = 16;
		while (pos < ret.length - 16)
		{
			setupChaChaState(state, key, blockcount, nonceprefix, msgid);
			ChaChaEngine.chachaCore(20, state, state);
			++blockcount;
			
			for (int i = 0; i < state.length && pos < ret.length - 16; ++i)
			{
				int val = Pack.littleEndianToInt(ret, pos);
				val ^= state[i];
				Pack.intToLittleEndian(val, ret, pos);
				pos += 4;
			}
		}
		
		// Generate and write authenticator.
		Poly1305 poly1305 = new Poly1305();
		poly1305.init(new KeyParameter(polykey));
		poly1305.update(ret, 0, ret.length - 16);
		poly1305.doFinal(ret, 16 + retlen);
		
		return ret;
	}
	
	/**
	 *  Decrypts content using an RFC 7539-like AEAD construction.
	 *  
	 *  @param content Clear text being encrypted.
	 *  @param key Key used for encryption/authentication.
	 *  @param nonceprefix Local nonce prefix used.
	 *  @param msgid Current message ID.
	 *  @return Clear text or null if authentication failed.
	 */
	protected static final byte[] chacha20Poly1305Dec(byte[] content, int[] key, int nonceprefix)
	{
		byte[] ret = null;
		
		try
		{
			long msgid = Pack.littleEndianToLong(content, 8);
			
			int[] state = new int[16];
			int blockcount = 0;
			
			byte[] polykey = new byte[32];
			setupChaChaState(state, key, blockcount, nonceprefix, msgid);
			ChaChaEngine.chachaCore(20, state, state);
			for (int i = 0; i < 8; ++i)
				Pack.intToLittleEndian(state[i], polykey, i << 2);
			Poly1305KeyGenerator.clamp(polykey);
			++blockcount;
			
			// Check authentication.
			byte[] checkpoly = new byte[16];
			Poly1305 poly1305 = new Poly1305();
			poly1305.init(new KeyParameter(polykey));
			poly1305.update(content, 0, content.length - 16);
			poly1305.doFinal(checkpoly, 0);
			for (int i = 0; i < checkpoly.length; ++i)
				if (checkpoly[i] != content[content.length - 16 + i])
					return null; // Authentication failed.
			
			// Decrypt content
			int pos = 16;
			while (pos < content.length - 16)
			{
				setupChaChaState(state, key, blockcount, nonceprefix, msgid);
				ChaChaEngine.chachaCore(20, state, state);
				++blockcount;
				
				for (int i = 0; i < state.length && pos < content.length - 16; ++i)
				{
					int val = Pack.littleEndianToInt(content, pos);
					val ^= state[i];
					Pack.intToLittleEndian(val, content, pos);
					pos += 4;
				}
			}
			
			int contentlen = Pack.littleEndianToInt(content, content.length - 20);
			
			// Sanity check
			if (contentlen < 0 || contentlen > content.length - 36)
				return null;
			
			ret = new byte[contentlen];
			System.arraycopy(content, 16, ret, 0, ret.length);
		}
		catch (Exception e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Sets up ChaCha20.
	 */
	protected static final void setupChaChaState(int[] state, int[] key, int blockcount, int nonceprefix, long msgid)
	{
		state[0] = 0x61707865;
		state[1] = 0x3320646e;
		state[2] = 0x79622d32;
		state[3] = 0x6b206574;
		System.arraycopy(key, 0, state, 4, key.length);
		state[12] = blockcount;
		state[13] = nonceprefix;
		state[14] = (int)(msgid >>> 32);
		state[15] = (int) msgid;
	}
	
	/**
	 *  Rounds up the
	 * @param size
	 * @return
	 */
	protected static final int pad16Size(int size)
	{
		return (size + 15) & ~15;
	}
	
	/**
	 *  Gets the encoded public key.
	 * 
	 *  @param ephemeralkey The ephemeral key.
	 */
	protected abstract byte[] getPubKey();
	
	/**
	 *  Creates the ephemeral key.
	 *  
	 *  @return The ephemeral key.
	 */
	protected abstract Object createEphemeralKey();
	
	/**
	 *  Generates the shared public key.
	 *  
	 *  @param remotepubkey The remote public key.
	 *  @return Shared key.
	 */
	protected abstract byte[] generateSharedKey();
	
	/**
	 *  Gets the shared ChaCha key.
	 * 
	 *  @param remotepubkey The remote public key.
	 */
	private int[] generateChaChaKey()
	{
		byte[] genkey = generateSharedKey();
		Blake2bDigest digest = new Blake2bDigest(256);
		digest.update(genkey, 0, genkey.length);
		genkey = new byte[32];
		digest.doFinal(genkey, 0);
		int[] ret = new int[8];
		Pack.littleEndianToInt(genkey, 0, ret);
		return ret;
	}
	
	/**
	 *  Message for starting the exchange.
	 *
	 */
	protected static class StartExchangeMessage extends BasicSecurityMessage
	{
		/** Challenge for the exchange authentication. */
		protected byte[] challenge;
		
		/** PAKE round 1 data. */
		protected byte[] pakeround1data;
		
		/**
		 *  Creates the message.
		 */
		public StartExchangeMessage()
		{
		}
		
		/**
		 *  Creates the message.
		 */
		public StartExchangeMessage(IComponentIdentifier sender, String conversationid)
		{
			super(sender, conversationid);
		}
		
		/**
		 *  Gets the challenge.
		 *
		 *  @return The challenge.
		 */
		public byte[] getChallenge()
		{
			return challenge;
		}

		/**
		 *  Sets the challenge.
		 *
		 *  @param challenge The challenge.
		 */
		public void setChallenge(byte[] challenge)
		{
			this.challenge = challenge;
		}
		
		/**
		 *  Gets the PAKE Round 1 data.
		 *  
		 *  @return The PAKE Round 1 data.
		 */
		public byte[] getPakeRound1Data()
		{
			return pakeround1data;
		}
		
		/**
		 *  Sets the PAKE Round 1 data.
		 *  
		 *  @param pakeround1data The PAKE Round 1 data.
		 */
		public void setPakeRound1Data(byte[] pakeround1data)
		{
			this.pakeround1data = pakeround1data;
		}
	}
	
	/**
	 *  Message for acknowledging the start of the exchange.
	 *
	 */
	protected static class AckExchangeMessage extends BasicSecurityMessage
	{
		/** Challenge for the exchange authentication. */
		protected byte[] challenge;
		
		/** PAKE round 1 data. */
		protected byte[] pakeround1data;
		
		/** PAKE round 2 data. */
		protected byte[] pakeround2data;
		
		/**
		 *  Creates the message.
		 */
		public AckExchangeMessage()
		{
		}
		
		/**
		 *  Creates the message.
		 */
		public AckExchangeMessage(IComponentIdentifier sender, String conversationid)
		{
			super(sender, conversationid);
		}
		
		/**
		 *  Gets the challenge.
		 *
		 *  @return The challenge.
		 */
		public byte[] getChallenge()
		{
			return challenge;
		}

		/**
		 *  Sets the challenge.
		 *
		 *  @param challenge The challenge.
		 */
		public void setChallenge(byte[] challenge)
		{
			this.challenge = challenge;
		}
		
		/**
		 *  Gets the PAKE Round 1 data.
		 *  
		 *  @return The PAKE Round 1 data.
		 */
		public byte[] getPakeRound1Data()
		{
			return pakeround1data;
		}
		
		/**
		 *  Sets the PAKE Round 1 data.
		 *  
		 *  @param pakeround1data The PAKE Round 1 data.
		 */
		public void setPakeRound1Data(byte[] pakeround1data)
		{
			this.pakeround1data = pakeround1data;
		}
		
		/**
		 *  Gets the PAKE Round 2 data.
		 *  
		 *  @return The PAKE Round 2 data.
		 */
		public byte[] getPakeRound2Data()
		{
			return pakeround2data;
		}
		
		/**
		 *  Sets the PAKE Round 2 data.
		 *  
		 *  @param pakeround1data The PAKE Round 2 data.
		 */
		public void setPakeRound2Data(byte[] pakeround2data)
		{
			this.pakeround2data = pakeround2data;
		}
	}
	
	protected static class KeyExchangeMessage extends BasicSecurityMessage
	{
		/** Public key of the exchange. */
		protected byte[] publickey;
		
		/** Signatures for verifying the platform name. */
		protected AuthToken platformnamesig;
		
		/** Signatures based on the local and remote platform access secrets. */
		protected Tuple2<AuthToken, AuthToken> platformsecretsigs;
		
		/** Network signatures of the public key. */
		protected MultiCollection<ByteArrayWrapper, AuthToken> networksigs;
		
		/** PAKE round 2 data. */
		protected byte[] pakeround2data;
		
		/**
		 *  Creates the message.
		 */
		public KeyExchangeMessage()
		{
		}
		
		/**
		 *  Creates the message.
		 */
		public KeyExchangeMessage(IComponentIdentifier sender, String conversationid)
		{
			super(sender, conversationid);
		}
		
		/**
		 *  Gets the PAKE Round 2 data.
		 *  
		 *  @return The PAKE Round 2 data.
		 */
		public byte[] getPakeRound2Data()
		{
			return pakeround2data;
		}
		
		/**
		 *  Sets the PAKE Round 2 data.
		 *  
		 *  @param pakeround1data The PAKE Round 2 data.
		 */
		public void setPakeRound2Data(byte[] pakeround2data)
		{
			this.pakeround2data = pakeround2data;
		}
		
		/**
		 *  Gets the public key.
		 *  
		 *  @return The public key.
		 */
		public byte[] getPublicKey()
		{
			return publickey;
		}
		
		/**
		 *  Sets the public key.
		 *  
		 *  @param publickey The public key.
		 */
		public void setPublicKey(byte[] publickey)
		{
			this.publickey = publickey;
		}
		
		/**
		 *  Gets the network signatures of the public key.
		 *  
		 *  @return The network signatures of the public key.
		 */
		public MultiCollection<ByteArrayWrapper, AuthToken> getNetworkSigs()
		{
			return networksigs;
		}
		
		/**
		 *  Sets the network signatures of the public key.
		 *  
		 *  @param networksigs The network signatures of the public key.
		 */
		public void setNetworkSigs(MultiCollection<ByteArrayWrapper, AuthToken> networksigs)
		{
			this.networksigs = networksigs;
		}

		/**
		 *  Gets the platform secret signatures.
		 *
		 *  @return The platform secret signatures.
		 */
		public Tuple2<AuthToken, AuthToken> getPlatformSecretSigs()
		{
			return platformsecretsigs;
		}

		/**
		 *  Sets the platform secret signatures.
		 *
		 *  @param platformsecretsigs The platform secret signatures.
		 */
		public void setPlatformSecretSigs(Tuple2<AuthToken, AuthToken> platformsecretsigs)
		{
			this.platformsecretsigs = platformsecretsigs;
		}
		
		/**
		 *  Gets the platform name signature.
		 *  
		 *  @return The signature.
		 */
		public AuthToken getPlatformNameSig()
		{
			return platformnamesig;
		}
		
		/**
		 *  Gets the platform name signature.
		 *  
		 *  @return The signature.
		 */
		public void setPlatformNameSig(AuthToken platformnamesig)
		{
			this.platformnamesig = platformnamesig;
		}
	}
	
	/**
	 *  Message signaling the handshake is done.
	 *
	 */
	protected static final class ReadyMessage extends BasicSecurityMessage
	{
		/**
		 *  Creates the message.
		 */
		public ReadyMessage()
		{
		}
		
		/**
		 *  Creates the message.
		 */
		public ReadyMessage(IComponentIdentifier sender, String conversationid)
		{
			super(sender, conversationid);
		}
	}
}
