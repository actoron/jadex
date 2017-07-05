package jadex.platform.service.security.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.MsgSecurityInfos;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;
import jadex.platform.service.security.auth.Blake2bX509AuthenticationSuite;
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
	
	/** The locally-generated authentication challenge. */
	protected byte[] localauthchallenge;
	
	/** The remote-generated authentication challenge. */
	protected byte[] remoteauthchallenge;
	
	/** Next step in the handshake protocol. */
	protected int nextstep;
	
	/** Hashed network names reverse lookup */
	protected Map<ByteArrayWrapper, Tuple2<String, AbstractAuthenticationSecret>> hashednetworks;
	
	// -------------- Operational state -----------------
	
	/** The authentication state. */
	protected MsgSecurityInfos secinf;
	
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
	 *  Gets the security infos related to the authentication state.
	 *  
	 *  @return The security infos for decrypted messages.
	 */
	public IMsgSecurityInfos getSecurityInfos()
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
		
		if (nextstep == 0 && incomingmessage instanceof InitialHandshakeFinalMessage)
		{
			StartExchangeMessage sem = new StartExchangeMessage(agent.getComponentIdentifier(), incomingmessage.getConversationId());
			localauthchallenge = new byte[32];
			SSecurity.getSecureRandom().nextBytes(localauthchallenge);
			sem.setChallenge(localauthchallenge);
			hashednetworks = getHashedNetworks(agent.getNetworks(), localauthchallenge);
			sem.setHashedNetworkNames(hashednetworks.keySet());
			
			agent.sendSecurityHandshakeMessage(incomingmessage.getSender(), sem);
			nextstep = 1;
		}
		else if (nextstep == 0 && incomingmessage instanceof StartExchangeMessage)
		{
			StartExchangeMessage sem = (StartExchangeMessage) incomingmessage;
			remoteauthchallenge = sem.getChallenge();
			
			if (remoteauthchallenge.length < 16)
				throw new SecurityException("Remote authentication challenge too short.");
			
			ephemeralkey = createEphemeralKey();
			byte[] pubkey = getPubKey();
			
			localauthchallenge = new byte[32];
			SSecurity.getSecureRandom().nextBytes(localauthchallenge);
			
			hashednetworks = getHashedNetworks(agent.getNetworks(), remoteauthchallenge);
			
			Map<ByteArrayWrapper, byte[]> networksigs = getNetworkSignatures(pubkey, sem.getHashedNetworkNames());
			
			KeyExchangeMessage em = new KeyExchangeMessage(agent.getComponentIdentifier(), sem.getConversationId());
			em.setPublicKey(pubkey);
			em.setChallenge(localauthchallenge);
			em.setPlatformSecretSigs(getPlatformSignatures(pubkey, agent, sem.getSender().getRoot()));
			em.setNetworkSigs(networksigs);
			
			agent.sendSecurityHandshakeMessage(sem.getSender(), em);
			nextstep = 2;
		}
		else if (nextstep == 1 && incomingmessage instanceof KeyExchangeMessage)
		{
			KeyExchangeMessage incmsg = (KeyExchangeMessage) incomingmessage;
			
			remoteauthchallenge = incmsg.getChallenge();
			
			remotepublickey = incmsg.getPublicKey();
			
			ephemeralkey = createEphemeralKey();
			byte[] pubkey = getPubKey();
			
			Map<ByteArrayWrapper, byte[]> networksigs = getNetworkSignatures(pubkey, incmsg.getNetworkSigs().keySet());
			
			boolean platformtrusted = verifyPlatformSignatures(remotepublickey, incmsg.getPlatformSecretSigs(), agent.getPlatformSecret());
			
			List<String> authnets = verifyNetworkSignatures(remotepublickey, incmsg.getNetworkSigs());
			
			secinf = new MsgSecurityInfos();
			secinf.setAuthenticatedPlatform(authnets.size() > 0 || platformtrusted);
			secinf.setTrustedPlatform(platformtrusted);
			secinf.setNetworks(authnets.toArray(new String[authnets.size()]));
			
			nonceprefix = Pack.littleEndianToInt(localauthchallenge, 0);
			nonceprefix ^= Pack.littleEndianToInt(remoteauthchallenge, 0);
			
			KeyExchangeMessage exmsg = new KeyExchangeMessage(agent.getComponentIdentifier(), incmsg.getConversationId());
			exmsg.setNetworkSigs(networksigs);
			exmsg.setPublicKey(pubkey);
			exmsg.setPlatformSecretSigs(getPlatformSignatures(pubkey, agent, incmsg.getSender().getRoot()));
			
			agent.sendSecurityHandshakeMessage(incmsg.getSender(), exmsg);
			
			nextstep = 3;
		}
		else if (nextstep == 2 && incomingmessage instanceof KeyExchangeMessage)
		{
			KeyExchangeMessage incmsg = (KeyExchangeMessage) incomingmessage;
			
			remotepublickey = incmsg.getPublicKey();
			
			boolean platformtrusted = verifyPlatformSignatures(remotepublickey, incmsg.getPlatformSecretSigs(), agent.getPlatformSecret());
			
			List<String> authnets = verifyNetworkSignatures(remotepublickey, incmsg.getNetworkSigs());
			
			secinf = new MsgSecurityInfos();
			secinf.setAuthenticatedPlatform(authnets.size() > 0 || platformtrusted);
			secinf.setTrustedPlatform(platformtrusted);
			secinf.setNetworks(authnets.toArray(new String[authnets.size()]));
			
			nonceprefix = Pack.littleEndianToInt(localauthchallenge, 0);
			nonceprefix ^= Pack.littleEndianToInt(remoteauthchallenge, 0);
			nonceprefix = ~nonceprefix;
			key = generateChaChaKey();
			System.out.println("Shared Key1: " + Arrays.toString(key));
			
			// Delete handshake state
			ephemeralkey = null;
			localauthchallenge = null;
			remoteauthchallenge = null;
			hashednetworks = null;
			remotepublickey = null;
			
			nextstep = 4;
			
			ReadyMessage rdy = new ReadyMessage(agent.getComponentIdentifier(), incmsg.getConversationId());
			agent.sendSecurityHandshakeMessage(incmsg.getSender(), rdy);
			
			ret = false;
		}
		else if (nextstep == 3 && incomingmessage instanceof ReadyMessage)
		{
			key = generateChaChaKey();
			System.out.println("Shared Key2: " + Arrays.toString(key));
			
			// Delete handshake state
			ephemeralkey = null;
			remotepublickey = null;
			localauthchallenge = null;
			remoteauthchallenge = null;
			hashednetworks = null;
			
			ret = false;
			nextstep = 4;
		}
		else
		{
			throw new SecurityException("Protocol violation detected.");
		}
		
		return ret;
	}
	
	/**
	 *  Destroy information.
	 */
	public void destroy()
	{
		ephemeralkey = null;
		localauthchallenge = null;
		remoteauthchallenge = null;
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
	protected Tuple2<byte[], byte[]> getPlatformSignatures(byte[] pubkey, SecurityAgent agent, IComponentIdentifier remoteid)
	{
		byte[] local = null;
		AbstractAuthenticationSecret ps = agent.getPlatformSecret();
		if (ps != null && ps.canSign())
			local = signKey(remoteauthchallenge, pubkey, ps);
		
		byte[] remote = null;
		ps = agent.getPlatformSecret(remoteid);
		if (ps != null && ps.canSign())
			remote = signKey(remoteauthchallenge, pubkey, ps);
		
		if (local != null || remote != null)
			return new Tuple2<byte[], byte[]>(local, remote);
		
		return null;
	}
	
	/**
	 *  Verifies platform signatures of a key.
	 *  
	 *  @param key The key.
	 *  @param networksigs The signatures.
	 *  @return List of network that authenticated the key.
	 */
	protected boolean verifyPlatformSignatures(byte[] key, Tuple2<byte[], byte[]> sigs, AbstractAuthenticationSecret localsecret)
	{
		boolean ret = false;
		if (sigs != null)
		{
			if (sigs.getFirstEntity() != null)
			{
				ret = verifyKey(localauthchallenge, key, localsecret, sigs.getFirstEntity());
			}
			
			if (!ret && sigs.getSecondEntity() != null)
			{
				ret = verifyKey(localauthchallenge, key, localsecret, sigs.getSecondEntity());
			}
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
	protected Map<ByteArrayWrapper, byte[]> getNetworkSignatures(byte[] key, Set<ByteArrayWrapper> remotehnets)
	{
		Map<ByteArrayWrapper, byte[]> networksigs = new HashMap<ByteArrayWrapper, byte[]>();
		if (remotehnets != null && hashednetworks.size() > 0)
		{
			for (ByteArrayWrapper hnwname : remotehnets)
			{
				Tuple2<String, AbstractAuthenticationSecret> tup = hashednetworks.get(hnwname);
				if (tup != null)
					networksigs.put(hnwname, signKey(remoteauthchallenge, key, tup.getSecondEntity()));
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
	protected List<String> verifyNetworkSignatures(byte[] key, Map<ByteArrayWrapper, byte[]> networksigs)
	{
		List<String> ret = new ArrayList<String>();
		if (networksigs != null)
		{
			for (Map.Entry<ByteArrayWrapper, byte[]> nwsig : networksigs.entrySet())
			{
				Tuple2<String, AbstractAuthenticationSecret> tup = hashednetworks.get(nwsig.getKey());
				if (tup != null)
				{
					if (verifyKey(localauthchallenge, key, tup.getSecondEntity(), nwsig.getValue()))
						ret.add(tup.getFirstEntity());
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Finalize.
	 */
	protected void finalize() throws Throwable
	{
		destroy();
	};
	
	/**
	 *  Signs a key for authentication.
	 *  
	 *  @param challenge Nonce / challenge received from remote.
	 *  @param key The key to sign.
	 *  @param secret Secret used for authentication.
	 *  @return Signature.
	 */
	protected static final byte[] signKey(byte[] challenge, byte[] key, AbstractAuthenticationSecret secret)
	{
		byte[] sigmsg = new byte[key.length + challenge.length];
		System.arraycopy(key, 0, sigmsg, 0, key.length);
		System.arraycopy(challenge, 0, sigmsg, key.length, challenge.length);
		Blake2bX509AuthenticationSuite authsuite = new Blake2bX509AuthenticationSuite();
		return authsuite.createAuthenticationToken(sigmsg, secret);
	}
	
	/**
	 *  Verifies a key for authentication.
	 *  
	 *  @param challenge Nonce / challenge received from remote.
	 *  @param key The key to verify.
	 *  @param secret Secret used for authentication.
	 *  @return Signature.
	 */
	protected static final boolean verifyKey(byte[] challenge, byte[] key, AbstractAuthenticationSecret secret, byte[] authtoken)
	{
		byte[] sigmsg = new byte[key.length + challenge.length];
		System.arraycopy(key, 0, sigmsg, 0, key.length);
		System.arraycopy(challenge, 0, sigmsg, key.length, challenge.length);
		Blake2bX509AuthenticationSuite authsuite = new Blake2bX509AuthenticationSuite();
		return authsuite.verifyAuthenticationToken(sigmsg, secret, authtoken);
	}
	
	/**
	 *  Hashes the network name.
	 *  
	 *  @param nwname The network name.
	 *  @param salt The salt.
	 *  @return Hashed network name.
	 */
	protected static final byte[] hashNetworkName(String nwname, byte[] salt)
	{
		Blake2bDigest dig = new Blake2bDigest(nwname.getBytes(SUtil.UTF8));
		byte[] hnwname = new byte[dig.getDigestSize()];
		dig.update(salt, 0, salt.length);
		dig.doFinal(hnwname, 0);
		return hnwname;
	}
	
	/**
	 *  Creates a reverse look-up map of hashed network names.
	 *  
	 *  @param networks The networks.
	 *  @param salt Salt to use.
	 *  @return Reverse look-up map.
	 */
	protected static final Map<ByteArrayWrapper, Tuple2<String, AbstractAuthenticationSecret>> getHashedNetworks(Map<String, AbstractAuthenticationSecret> networks, byte[] salt)
	{
		Map<ByteArrayWrapper, Tuple2<String, AbstractAuthenticationSecret>> ret = new HashMap<ByteArrayWrapper, Tuple2<String,AbstractAuthenticationSecret>>();
		if (networks != null)
		{
			for (Map.Entry<String, AbstractAuthenticationSecret> nw : networks.entrySet())
			{
				Tuple2<String, AbstractAuthenticationSecret> tup;
				tup = new Tuple2<String, AbstractAuthenticationSecret>(nw.getKey(), nw.getValue());
				ret.put(new ByteArrayWrapper(hashNetworkName(nw.getKey(), salt)), tup);
			}
		}
		return ret;
	}
	
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
		
		byte[] ret = new byte[contentlen];
		System.arraycopy(content, 16, ret, 0, ret.length);
		
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
		
		/** The available hashed network names */
		protected Set<ByteArrayWrapper> hashednetworknames;
		
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
		 *  Gets the network names.
		 *
		 *  @return The network names.
		 */
		public Set<ByteArrayWrapper> getHashedNetworkNames()
		{
			return hashednetworknames;
		}

		/**
		 *  Sets the hashed network names.
		 *
		 *  @param hashednetworknames The hashed network names.
		 */
		public void setHashedNetworkNames(Set<ByteArrayWrapper> hashednetworknames)
		{
			this.hashednetworknames = hashednetworknames;
		}
	}
	
	protected static class KeyExchangeMessage extends BasicSecurityMessage
	{
		/** Public key of the exchange. */
		protected byte[] publickey;
		
		/** Signatures based on the local and remote platform access secrets. */
		protected Tuple2<byte[], byte[]> platformsecretsigs;
		
		/** Network signatures of the public key. */
		protected Map<ByteArrayWrapper, byte[]> networksigs;
		
		/** Challenge for the exchange authentication. */
		protected byte[] challenge;
		
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
		public Map<ByteArrayWrapper, byte[]> getNetworkSigs()
		{
			return networksigs;
		}
		
		/**
		 *  Sets the network signatures of the public key.
		 *  
		 *  @param networksigs The network signatures of the public key.
		 */
		public void setNetworkSigs(Map<ByteArrayWrapper, byte[]> networksigs)
		{
			this.networksigs = networksigs;
		}

		/**
		 *  Gets the platform secret signatures.
		 *
		 *  @return The platform secret signatures.
		 */
		public Tuple2<byte[], byte[]> getPlatformSecretSigs()
		{
			return platformsecretsigs;
		}

		/**
		 *  Sets the platform secret signatures.
		 *
		 *  @param platformsecretsigs The platform secret signatures.
		 */
		public void setPlatformSecretSigs(Tuple2<byte[], byte[]> platformsecretsigs)
		{
			this.platformsecretsigs = platformsecretsigs;
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
	
	/**
	 *  Wrapper to allow byte arrays as hash keys.
	 *
	 */
	protected static final class ByteArrayWrapper
	{
		/** The wrapped byte array. */
		protected byte[] array;
		
		/**
		 *  Creates the wrapper.
		 */
		public ByteArrayWrapper()
		{
		}
		
		/**
		 *  Creates the wrapper.
		 */
		public ByteArrayWrapper(byte[] array)
		{
			this.array = array;
		}

		/**
		 *  Gets the array.
		 *
		 *  @return The array.
		 */
		public byte[] getArray()
		{
			return array;
		}

		/**
		 *  Sets the array.
		 *
		 *  @param array The array.
		 */
		public void setArray(byte[] array)
		{
			this.array = array;
		}
		
		/**
		 *  Creates a hash code.
		 */
		public int hashCode()
		{
			if (array == null)
				return 0;
			
			return Arrays.hashCode(array);
		}
		
		/**
		 *  Compares two arrays.
		 */
		public boolean equals(Object obj)
		{
			boolean ret = false;
			if (obj instanceof ByteArrayWrapper)
			{
				ByteArrayWrapper other = (ByteArrayWrapper) obj;
				if (other.getArray() == null && array == null)
				{
					ret = true;
				}
				else
				{
					ret = Arrays.equals(array, other.getArray());
				}
			}
			return ret;
		}
	}
}
