package jadex.platform.service.security.auth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.util.Pack;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.ByteArrayWrapper;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.SecurityAgent;

/**
 *  Symmetric authentication based on Blake2b MACs.
 */
public class Blake2bX509AuthenticationSuite implements IAuthenticationSuite
{
	/** Authentication Suite ID. */
	protected static final int AUTH_SUITE_ID = 93482103;
	
	/** Size of the MAC. */
	protected static final int MAC_SIZE = 64;
	
	/** Size of the derived key. */
	protected static final int DERIVED_KEY_SIZE = 64;
	
	/** Size of the salt. */
	protected static final int SALT_SIZE = 32;
	
//	protected static final EdDSAParameterSpec ED25519 = EdDSANamedCurveTable.getByName("Ed25519");
	
	/** State for password-authenticated key exchange. */
	protected Map<PasswordSecret, JadexJPakeParticipant> pakestate;
	
	/** Special pake participant for negotiating with platforms who have a remote password. */
	protected Tuple2<PasswordSecret, JadexJPakeParticipant> remotepwpake;
	
	/**
	 *  Creates the suite.
	 */
	public Blake2bX509AuthenticationSuite()
	{
		pakestate = new HashMap<PasswordSecret, JadexJPakeParticipant>();
	}
	
	/**
	 *  Gets the authentication suite ID.
	 *  
	 *  @return The authentication suite ID.
	 */
	public int getId()
	{
		return AUTH_SUITE_ID;
	}
	
	/** 
	 *  Gets the first round of the password-authenticated key-exchange.
	 *  
	 *  @return First round payload.
	 */
	public byte[] getPakeRound1(SecurityAgent agent, IComponentIdentifier remoteid)
	{
		byte[] idsalt = new byte[64];
		SSecurity.getSecureRandom().nextBytes(idsalt);
//		System.out.println("ID SALT 1: " + Arrays.toString(idsalt));
		
		String pid = agent.getComponentIdentifier().getRoot().toString();
		
		byte[] local = new byte[0];
		byte[] remoteremote = new byte[0];
		if (agent.getInternalPlatformSecret() instanceof PasswordSecret && agent.getInternalUsePlatformSecret())
		{
			PasswordSecret secret = (PasswordSecret) agent.getInternalPlatformSecret();
			JadexJPakeParticipant jpake = createJPakeParticipant(pid, secret.getPassword());
			pakestate.put(secret, jpake);
			JPAKERound1Payload r1pl = jpake.createRound1PayloadToSend();
			
			local = round1ToBytes(r1pl);
			
			remotepwpake = new Tuple2<PasswordSecret, JadexJPakeParticipant>(secret, createJPakeParticipant(pid, secret.getPassword()));
			remoteremote = round1ToBytes(remotepwpake.getSecondEntity().createRound1PayloadToSend());
		}
		
		byte[] localremote = new byte[0];
		if (agent.getInternalPlatformSecret(remoteid) instanceof PasswordSecret)
		{
			PasswordSecret secret = (PasswordSecret) agent.getInternalPlatformSecret(remoteid);
			JadexJPakeParticipant jpake = createJPakeParticipant(pid, secret.getPassword());
			pakestate.put((PasswordSecret) agent.getInternalPlatformSecret(remoteid), jpake);
			JPAKERound1Payload r1pl = jpake.createRound1PayloadToSend();
			
			localremote = round1ToBytes(r1pl);
		} 
		
		List<byte[]> networks = new ArrayList<byte[]>();
		if (agent.getInternalNetworks() != null && agent.getInternalNetworks().size() > 0)
		{
			for (Map.Entry<String, Collection<AbstractAuthenticationSecret>> entry : agent.getInternalNetworks().entrySet())
			{
				if (entry.getValue() != null)
				{
					for (AbstractAuthenticationSecret secret : entry.getValue())
					{
						if (secret instanceof PasswordSecret)
						{
							JadexJPakeParticipant jpake = createJPakeParticipant(pid,((PasswordSecret) secret).getPassword());
							pakestate.put((PasswordSecret) secret, jpake);
							JPAKERound1Payload r1pl = jpake.createRound1PayloadToSend();
							
							networks.add(createSaltedId(entry.getKey(), idsalt));
							networks.add(round1ToBytes(r1pl));
						}
					}
				}
			}
		}
		
		byte[] nwbytes = new byte[0];
		if (networks.size() > 0)
			nwbytes = SUtil.mergeData(networks.toArray(new byte[networks.size()][]));
		
		return SUtil.mergeData(idsalt, local, localremote, remoteremote, nwbytes);
	}
	
	/** 
	 *  Gets the second round of the password-authenticated key-exchange.
	 *  
	 *  @return Second round payload.
	 */
	public byte[] getPakeRound2(SecurityAgent agent, IComponentIdentifier remoteid, byte[] round1data)
	{
		List<byte[]> r1list = SUtil.splitData(round1data);
		if (r1list.size() != 5)
			throw new IllegalArgumentException("Illegal round 1 data.");
		
		byte[] idsalt = r1list.get(0);
//		System.out.println("ID SALT 2: " + Arrays.toString(idsalt));
		
		byte[] local = new byte[0];
		if (r1list.get(1).length > 0 && agent.getInternalUsePlatformSecret())
		{
			if (agent.getInternalPlatformSecret() instanceof PasswordSecret)
			{
				JPAKERound1Payload r1 = bytesToRound1(r1list.get(1));
				PasswordSecret secret = (PasswordSecret) agent.getInternalPlatformSecret();
				JadexJPakeParticipant part = pakestate.get(secret);
				
				try
				{
					part.validateRound1PayloadReceived(r1);
					local = round2ToBytes(part.createRound2PayloadToSend());
				}
				catch (Exception e)
				{
				}
			}
		}
		
		byte[] localremote = new byte[0];
		if (r1list.get(2).length > 0 && agent.getInternalUsePlatformSecret())
		{
			JPAKERound1Payload r1 = bytesToRound1(r1list.get(2));
			if (agent.getInternalPlatformSecret() instanceof PasswordSecret)
			{
//				PasswordSecret secret = (PasswordSecret) agent.getPlatformSecret();
				try
				{
					remotepwpake.getSecondEntity().validateRound1PayloadReceived(r1);
					localremote = round2ToBytes(remotepwpake.getSecondEntity().createRound2PayloadToSend());
				}
				catch (Exception e)
				{
				}
			}
		}
		
		byte[] remoteremote = new byte[0];
		if (agent.getInternalPlatformSecret(remoteid) instanceof PasswordSecret)
		{
			PasswordSecret secret = (PasswordSecret) agent.getInternalPlatformSecret(remoteid);
			JPAKERound1Payload r1 = bytesToRound1(r1list.get(3));
			JadexJPakeParticipant part = pakestate.get(secret);
			
			try
			{
				part.validateRound1PayloadReceived(r1);
				remoteremote = round2ToBytes(part.createRound2PayloadToSend());
			}
			catch (Exception e)
			{
			}
		}
		
		List<byte[]> networks = new ArrayList<byte[]>();
		if (r1list.get(4).length > 0 && agent.getInternalNetworks().size() > 0)
		{ 
			MultiCollection<ByteArrayWrapper, PasswordSecret> reversemap = new MultiCollection<ByteArrayWrapper, PasswordSecret>();
			
			for (Map.Entry<String, Collection<AbstractAuthenticationSecret>> entry : agent.getInternalNetworks().entrySet())
			{
				if (entry.getValue() != null)
				{
					for (AbstractAuthenticationSecret secret : entry.getValue())
					{
						if (secret instanceof PasswordSecret)
							reversemap.add(new ByteArrayWrapper(createSaltedId(entry.getKey(), idsalt)), (PasswordSecret) secret);
					}
				}
			}
			
			List<byte[]> nwloads = SUtil.splitData(r1list.get(4));
			if (nwloads.size() % 2 > 0)
				throw new IllegalArgumentException("Illegal round 1 data.");
			
			for (int i = 0; i < nwloads.size(); i = i + 2)
			{
				ByteArrayWrapper key = new ByteArrayWrapper(nwloads.get(i));
				Collection<PasswordSecret> secrets = reversemap.get(key);
				if (secrets != null)
				{
					for (PasswordSecret secret : secrets)
					{
						JadexJPakeParticipant part = pakestate.get(secret);
						
						if (part != null)
						{
							JPAKERound1Payload r1 = bytesToRound1(nwloads.get(i + 1));
							
							try
							{
								part.validateRound1PayloadReceived(r1);
								networks.add(key.getArray());
								networks.add(round2ToBytes(part.createRound2PayloadToSend()));
							}
							catch (Exception e)
							{
								pakestate.remove(agent.getInternalPlatformSecret());
							}
						}
					}
				}
			}
		}
		
		byte[] nwbytes = new byte[0];
		if (networks.size() > 0)
			nwbytes = SUtil.mergeData(networks.toArray(new byte[networks.size()][]));
		
		return SUtil.mergeData(idsalt, local, localremote, remoteremote, nwbytes);
	}
	
	/**
	 *  Finalizes the password-authenticated key exchange.
	 */
	public void finalizePake(SecurityAgent agent, IComponentIdentifier remoteid, byte[] round2data)
	{
		List<byte[]> r2list = SUtil.splitData(round2data);
		if (r2list.size() != 5)
			throw new IllegalArgumentException("Illegal finalization data.");
		
		byte[] idsalt = r2list.get(0);
		
		if (r2list.get(1).length > 0)
		{
			JadexJPakeParticipant part = pakestate.get(agent.getInternalPlatformSecret());
			JPAKERound2Payload r2 = bytesToRound2(r2list.get(1));
			if (part != null)
			{
				try
				{
					part.validateRound2PayloadReceived(r2);
					part.calculateKeyingMaterial();
				}
				catch (Exception e)
				{
				}
			}
		}
		
		if (r2list.get(2).length > 0)
		{
			JadexJPakeParticipant part = pakestate.get(agent.getInternalPlatformSecret(remoteid));
			JPAKERound2Payload r2 = bytesToRound2(r2list.get(2));
			try
			{
				part.validateRound2PayloadReceived(r2);
				part.calculateKeyingMaterial();
			}
			catch (Exception e)
			{
			}
		}
		
		if (r2list.get(3).length > 0)
		{
			JPAKERound2Payload r2 = bytesToRound2(r2list.get(3));
			try
			{
				remotepwpake.getSecondEntity().validateRound2PayloadReceived(r2);
				remotepwpake.getSecondEntity().calculateKeyingMaterial();
			}
			catch (Exception e)
			{
			}
		}
		
		if (r2list.get(4).length > 0 && agent.getInternalNetworks().size() > 0)
		{ 
			Map<ByteArrayWrapper, JadexJPakeParticipant> reversemap = new HashMap<ByteArrayWrapper, JadexJPakeParticipant>();
			
			for (Map.Entry<String, Collection<AbstractAuthenticationSecret>> entry : agent.getInternalNetworks().entrySet())
			{
				if (entry.getValue() != null)
				{
					for (AbstractAuthenticationSecret secret : entry.getValue())
					{
						if (entry.getValue() instanceof PasswordSecret)
						{
							JadexJPakeParticipant p = pakestate.get(secret);
							if (p != null)
								reversemap.put(new ByteArrayWrapper(createSaltedId(entry.getKey(), idsalt)), p);
						}
					}
				}
			}
			
			List<byte[]> nwloads = SUtil.splitData(r2list.get(4));
			if (nwloads.size() % 2 > 0)
				throw new IllegalArgumentException("Illegal finalization data.");
			
			for (int i = 0; i < nwloads.size(); i = i + 2)
			{
				ByteArrayWrapper key = new ByteArrayWrapper(nwloads.get(i));
				JadexJPakeParticipant part = reversemap.get(key);
				JPAKERound2Payload r2 = bytesToRound2(nwloads.get(i + 1));
				
				try
				{
					part.validateRound2PayloadReceived(r2);
					part.calculateKeyingMaterial();
				}
				catch (Exception e)
				{
					pakestate.remove(agent.getInternalPlatformSecret());
				}
			}
		}
		
		if (remotepwpake != null && remotepwpake.getSecondEntity().getDerivedKey() == null)
			remotepwpake = null;
		
		for (Iterator<Map.Entry<PasswordSecret, JadexJPakeParticipant>> it = pakestate.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<PasswordSecret, JadexJPakeParticipant> entry = it.next();
			if (entry.getValue() == null || entry.getValue().getDerivedKey() == null)
				it.remove();
		}
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param secret The secret used for authentication.
	 *  @return Authentication token.
	 */
	public byte[] createAuthenticationToken(byte[] msg, AbstractAuthenticationSecret secret)
	{
		byte[] ret = null;
		
		// Generate random salt.
		byte[] salt = new byte[SALT_SIZE];
		SSecurity.getSecureRandom().nextBytes(salt);
		
		// Hash the message.
		byte[] msghash = getMessageHash(msg, salt);
		
		if (secret instanceof SharedSecret)
		{
			SharedSecret ssecret = (SharedSecret) secret;
			
			byte[] dk = null;
			
			if (secret instanceof PasswordSecret)
			{
				JadexJPakeParticipant jpake = pakestate.get(secret);
				
				if (jpake != null)
					dk = jpake.getDerivedKey();
				else
					throw new SecurityException("J-PAKE key not found for password " + secret);
			}
			else
			{
				dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
			}
			
			if (dk == null)
				return ret;
			
			// Generate MAC used for authentication.
			Blake2bDigest blake2b = new Blake2bDigest(dk);
			ret = new byte[SALT_SIZE + MAC_SIZE + 4];
			Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
			System.arraycopy(salt, 0, ret, 4, salt.length);
			blake2b.update(msghash, 0, msghash.length);
			blake2b.doFinal(ret, salt.length + 4);
			
			/*if (ssecret instanceof PasswordSecret)
			{
				// Using Schnorr signature
				
//				byte[] randsecbytes = new byte[64];
//				SSecurity.getSecureRandom().nextBytes(randsecbytes);
//				BigInteger randsec = new BigInteger(1, randsecbytes);
//				BigInteger randpub = SCHNORR_GROUP.getG().modPow(randsec, SCHNORR_GROUP.getP());
//				
//				byte[] challengebytes = new byte[64];
//				Blake2bDigest dig = new Blake2bDigest(512);
//				byte[] tmp = randpub.toByteArray();
//				dig.update(tmp, 0, tmp.length);
//				dig.update(msghash, 0, msghash.length);
//				dig.doFinal(challengebytes, 0);
//				BigInteger challenge = new BigInteger(1, challengebytes);
//				
//				BigInteger dkbi = new BigInteger(1, dk);
//				
//				BigInteger s = dkbi.multiply(challenge).add(randsec).mod(SCHNORR_GROUP.getP());
//				
//				byte[] kdfparams = ((PasswordSecret) ssecret).getKdfParams();
//				
//				tmp = SUtil.mergeData(s.toByteArray(), randpub.toByteArray(), salt, kdfparams);
//				
//				ret = new byte[tmp.length + 4];
//				Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
//				System.arraycopy(tmp, 0, ret, 4, tmp.length);
				
				// Using ed25519 / EDDSA
				
				KeyPair pair = derivedkeyscache.get(ssecret);
				if (pair == null)
				{
					byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, combinedchallenge);
					pair = deriveEd25519KeyPair(dk);
					derivedkeyscache.put((PasswordSecret) ssecret, pair);
				}
				
				EdDSAEngine eddsa = new EdDSAEngine();
				byte[] eddsasig = null;
				try
				{
					eddsa.initSign(pair.getPrivate(), SSecurity.getSecureRandom());
					eddsasig = eddsa.signOneShot(msghash);
				}
				catch (Exception e)
				{
					throw SUtil.throwUnchecked(e);
				}
				
				byte[] kdfparams = ((PasswordSecret) ssecret).getKdfParams();
				
				byte[] tmp = SUtil.mergeData(eddsasig, salt, kdfparams);
				
				ret = new byte[tmp.length + 4];
				Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
				System.arraycopy(tmp, 0, ret, 4, tmp.length);
			}
			else
			{
				byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
				
				// Generate MAC used for authentication.
				Blake2bDigest blake2b = new Blake2bDigest(dk);
				ret = new byte[SALT_SIZE + MAC_SIZE + 4];
				Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
				System.arraycopy(salt, 0, ret, 4, salt.length);
				blake2b.update(msghash, 0, msghash.length);
				blake2b.doFinal(ret, salt.length + 4);
			}*/
		}
		else if (secret instanceof AbstractX509PemSecret)
		{
			AbstractX509PemSecret aps = (AbstractX509PemSecret) secret;
			if (!aps.canSign())
				throw new IllegalArgumentException("Secret cannot be used to sign: " + aps);
			
			byte[] sig = SSecurity.signWithPEM(msghash, aps.openCertificate(), aps.openPrivateKey());
			ret = new byte[sig.length + SALT_SIZE + 4];
			Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
			System.arraycopy(salt, 0, ret, 4, salt.length);
			System.arraycopy(sig, 0, ret, 4 + salt.length, sig.length);
		}
		else
		{
			throw new IllegalArgumentException("Unknown secret type: " + secret);
		}
		
		// Generate authenticator: Salt, MAC.
		return ret;
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param secret The secret used for authentication.
	 *  @param authtoken Authentication token.
	 *  @return True if authenticated, false otherwise.
	 */
	public boolean verifyAuthenticationToken(byte[] msg, AbstractAuthenticationSecret secret, byte[] authtoken)
	{
		boolean ret = false;
		try
		{
			if (Pack.littleEndianToInt(authtoken, 0) != AUTH_SUITE_ID)
				return false;
			
			if (secret instanceof SharedSecret)
			{
				SharedSecret ssecret = (SharedSecret) secret;
				
				if (authtoken.length != SALT_SIZE + MAC_SIZE + 4)
					return false;
				
				// Decode token.
				byte[] salt = new byte[SALT_SIZE];
				System.arraycopy(authtoken, 4, salt, 0, salt.length);
				
				byte[] msghash = getMessageHash(msg, salt);
				
				byte[] mac = new byte[MAC_SIZE];
				System.arraycopy(authtoken, SALT_SIZE + 4, mac, 0, mac.length);
				
				// Derive the  key.
				byte[] dk = null;
				if (ssecret instanceof PasswordSecret)
				{
					JadexJPakeParticipant jpake = pakestate.get(secret);
					
					if (jpake != null)
						dk = jpake.getDerivedKey();
				}
				else
				{
					dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
				}
				
				if (dk == null)
					return false;
				
				// Generate MAC
				Blake2bDigest blake2b = new Blake2bDigest(dk);
				byte[] gmac = new byte[MAC_SIZE];
				blake2b.update(msghash, 0, msghash.length);
				blake2b.doFinal(gmac, 0);
				ret = Arrays.equals(gmac, mac);
				
				if (!ret && remotepwpake != null && secret == remotepwpake.getFirstEntity())
				{
					dk = remotepwpake.getSecondEntity().getDerivedKey();
					
					blake2b.reset();
					gmac = new byte[MAC_SIZE];
					blake2b.update(msghash, 0, msghash.length);
					blake2b.doFinal(gmac, 0);
					ret = Arrays.equals(gmac, mac);
				}
				
				/*if (secret instanceof PasswordSecret)
				{
//					List<byte[]> authlist = SUtil.splitData(authtoken, 4, -1);
//					if (authlist.size() != 4)
//						return false;
//					
//					BigInteger s = new BigInteger(authlist.get(0));
//					BigInteger randpub = new BigInteger(authlist.get(1));
//					byte[] salt = authlist.get(2);
//					byte[] kdfparams = authlist.get(3);
//					
//					byte[] dk = ((PasswordSecret) ssecret).deriveKey(DERIVED_KEY_SIZE, salt, kdfparams);
//					
//					BigInteger sec = new BigInteger(1, dk);
//					BigInteger pub = SCHNORR_GROUP.getG().modPow(sec, SCHNORR_GROUP.getP());
//					
//					byte[] msghash = getMessageHash(msg, salt);
//					
//					byte[] challengebytes = new byte[64];
//					Blake2bDigest dig = new Blake2bDigest(512);
//					byte[] tmp = randpub.toByteArray();
//					dig.update(tmp, 0, tmp.length);
//					dig.update(msghash, 0, msghash.length);
//					dig.doFinal(challengebytes, 0);
//					BigInteger challenge = new BigInteger(1, challengebytes);
//					
//					BigInteger res0 = SCHNORR_GROUP.getG().modPow(s, SCHNORR_GROUP.getP());
//					BigInteger res1 = pub.modPow(challenge, SCHNORR_GROUP.getP()).multiply(randpub).mod(SCHNORR_GROUP.getP());
//					
////					System.out.println("Schnorr res0:" + res0);
////					System.out.println("Schnorr res1:" + res1);
//					
//					ret = res0.equals(res1);
					
					List<byte[]> authlist = SUtil.splitData(authtoken, 4, -1);
					if (authlist.size() != 3)
						return false;
					
					byte[] eddsasig = authlist.get(0);
					byte[] salt = authlist.get(1);
					byte[] kdfparams = authlist.get(2);
					
					byte[] msghash = getMessageHash(msg, salt);
					
					KeyPair pair = null;
					if (Arrays.equals(kdfparams, ((PasswordSecret) ssecret).getKdfParams()))
					{
						pair = derivedkeyscache.get(ssecret);
						if (pair == null)
						{
							long ts = System.currentTimeMillis();
							byte[] dk = ((PasswordSecret) ssecret).deriveKey(DERIVED_KEY_SIZE, combinedchallenge, kdfparams);
							System.out.println("VERIFY: " + (System.currentTimeMillis() - ts));
							pair = deriveEd25519KeyPair(dk);
							
							derivedkeyscache.put((PasswordSecret) ssecret, pair);
						}
					}
					else
					{
						byte[] dk = ((PasswordSecret) ssecret).deriveKey(DERIVED_KEY_SIZE, combinedchallenge, kdfparams);
						pair = deriveEd25519KeyPair(dk);
					}
					
					EdDSAEngine eddsa = new EdDSAEngine();
					eddsa.initVerify(pair.getPublic());
					ret = eddsa.verifyOneShot(msghash, eddsasig);
				}
				else
				{
					if (authtoken.length != SALT_SIZE + MAC_SIZE + 4)
						return false;
					
					// Decode token.
					byte[] salt = new byte[SALT_SIZE];
					System.arraycopy(authtoken, 4, salt, 0, salt.length);
					
					byte[] msghash = getMessageHash(msg, salt);
					
					byte[] mac = new byte[MAC_SIZE];
					System.arraycopy(authtoken, SALT_SIZE + 4, mac, 0, mac.length);
					
					// Decrypt the random key.
					byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
					
					// Generate MAC
					Blake2bDigest blake2b = new Blake2bDigest(dk);
					byte[] gmac = new byte[MAC_SIZE];
					blake2b.update(msghash, 0, msghash.length);
					blake2b.doFinal(gmac, 0);
					ret = Arrays.equals(gmac, mac);
				}*/
			}
			else if (secret instanceof AbstractX509PemSecret)
			{
				// Decode token.
				byte[] salt = new byte[SALT_SIZE];
				System.arraycopy(authtoken, 4, salt, 0, salt.length);
				
				byte[] msghash = getMessageHash(msg, salt);
				
				AbstractX509PemSecret aps = (AbstractX509PemSecret) secret;
				byte[] sig = new byte[authtoken.length - 4 - salt.length];
				System.arraycopy(authtoken, 4 + salt.length, sig, 0, sig.length);
				
				SSecurity.verifyWithPEM(msghash, sig, aps.openTrustAnchorCert());
			}
			else
			{
				Logger.getLogger("authentication").warning("Unknown secret type: " + secret);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Create message hash.
	 * 
	 *  @param msg The message.
	 *  @return Hashed message.
	 */
	protected static final byte[] getMessageHash(byte[] msg, byte[] salt)
	{
		Blake2bDigest blake2b = new Blake2bDigest(512);
		byte[] msghash = new byte[64];
		blake2b.update(msg, 0, msg.length);
		blake2b.update(salt, 0, salt.length);
		blake2b.doFinal(msghash, 0);
		return msghash;
	}
	
	/**
	 *  Creates a new participant for JPAKE.
	 *  
	 *  @param pid
	 *  @return
	 */
	protected static final JadexJPakeParticipant createJPakeParticipant(String pid, String password)
	{
		return new JadexJPakeParticipant(pid, password, new Blake2bDigest(512));
	}
	
	/**
	 *  Encodes JPAKE round 1.
	 *  
	 *  @param r1pl JPAKE round 1.
	 *  @return Encoded round.
	 */
	protected static final byte[] round1ToBytes(JPAKERound1Payload r1pl)
	{
		byte[] pid = r1pl.getParticipantId().getBytes(SUtil.UTF8);
		byte[] gx1 = r1pl.getGx1().toByteArray();
		byte[] gx2 = r1pl.getGx2().toByteArray();
		byte[] kpx1 = bigIntegerArrayToByteArray(r1pl.getKnowledgeProofForX1());
		byte[] kpx2 = bigIntegerArrayToByteArray(r1pl.getKnowledgeProofForX2());
		
		return SUtil.mergeData(pid, gx1, gx2, kpx1, kpx2);
	}
	
	/**
	 *  Decodes JPAKE round 1.
	 *  
	 *  @param bytes Encoded round.
	 *  @return JPAKE round 1.
	 */
	protected static final JPAKERound1Payload bytesToRound1(byte[] bytes)
	{
		List<byte[]> list = SUtil.splitData(bytes);
		
		if (list.size() != 5)
			throw new IllegalArgumentException("Failed to decode round 1 payload.");
		
		return new JPAKERound1Payload(new String(list.get(0), SUtil.UTF8),
									  new BigInteger(list.get(1)),
									  new BigInteger(list.get(2)),
									  byteArrayToBigIntegerArray(list.get(3)),
									  byteArrayToBigIntegerArray(list.get(4)));
	}
	
	/**
	 *  Encodes JPAKE round 2.
	 *  
	 *  @param r1pl JPAKE round 2.
	 *  @return Encoded round.
	 */
	protected static final byte[] round2ToBytes(JPAKERound2Payload r2pl)
	{
		byte[] pid = r2pl.getParticipantId().getBytes(SUtil.UTF8);
		byte[] a = r2pl.getA().toByteArray();
		byte[] kpx2 = bigIntegerArrayToByteArray(r2pl.getKnowledgeProofForX2s());
		
		return SUtil.mergeData(pid, a, kpx2);
	}
	
	/**
	 *  Decodes JPAKE round 2.
	 *  
	 *  @param bytes Encoded round.
	 *  @return JPAKE round 2.
	 */
	protected static final JPAKERound2Payload bytesToRound2(byte[] bytes)
	{
		List<byte[]> list = SUtil.splitData(bytes);
		
		if (list.size() != 3)
			throw new IllegalArgumentException("Failed to decode round 1 payload.");
		
		return new JPAKERound2Payload(new String(list.get(0), SUtil.UTF8),
									  new BigInteger(list.get(1)),
									  byteArrayToBigIntegerArray(list.get(2)));
	}
	
	/**
	 *  Hashes an id with a salt.
	 *  
	 *  @param id The clear id.
	 *  @param idsalt The salt.
	 *  @return Salted ID.
	 */
	protected byte[] createSaltedId(String id, byte[] idsalt)
	{
		byte[] idbytes = id.getBytes(SUtil.UTF8);
		Blake2bDigest digest = new Blake2bDigest(512);
		digest.update(idsalt, 0, idsalt.length);
		digest.update(idbytes, 0, idbytes.length);
		
		byte[] ret = new byte[64];
		digest.doFinal(ret, 0);
		
		return ret;
	}
	
	/**
	 *  Converts a big integer array to a byte array.
	 *  
	 *  @param bigintarr Big integer array.
	 *  @return Byte array.
	 */
	protected static final byte[] bigIntegerArrayToByteArray(BigInteger[] bigintarr)
	{
		byte[][] list = new byte[bigintarr.length][];
		
		for (int i = 0; i < list.length; ++i)
			list[i] = bigintarr[i].toByteArray();
		
		return SUtil.mergeData(list);
	}
	
	/**
	 *  Converts a byte array back into a big integer array.
	 *  
	 *  @param bytes The byte array.
	 *  @return The big integer array
	 */
	protected static final BigInteger[] byteArrayToBigIntegerArray(byte[] bytes)
	{
		List<byte[]> list = SUtil.splitData(bytes);
		BigInteger[] ret = new BigInteger[list.size()];
		
		for (int i = 0; i < ret.length; ++i)
			ret[i] = new BigInteger(list.get(i));
		
		return ret;
	}
}
