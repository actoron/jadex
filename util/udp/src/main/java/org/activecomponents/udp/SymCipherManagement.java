package org.activecomponents.udp;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicLong;

import org.activecomponents.udp.asymciphers.IKeyExchange;
import org.activecomponents.udp.asymciphers.KeyExchangeGenerator;
import org.activecomponents.udp.symciphers.ISymCipher;
import org.activecomponents.udp.symciphers.Nonce;

/**
 *  Management for keying and maintaining the symmetric encryption.
 *
 */
public class SymCipherManagement
{
	/** Available cipher suites, order is important, only append please. */
	protected static final String[] CIPHERS = new String[] { "com.actoron.udp.symciphers.NullCipher",
																"com.actoron.udp.symciphers.GcmAesCipher",
																"com.actoron.udp.symciphers.EaxAesCipher" };
	
	/** ID of class used as cipher for symmetric encryption. */
	protected int cipherclassid;
	
	/** Constructor for creating new ISymCipher objects. */
	protected Constructor<?> ciphercon;
	
	/** Generator for key exchanges. */
	protected KeyExchangeGenerator keyexgen;
	
	/** The currently held symmetric ciphers */
	SymCipherSuite[] symciphersuites = new SymCipherSuite[256];
	
	/** Counters for packet IDs, must be greater than Long.MIN_VALUE to allow replay identification to work. */
	protected AtomicLong packetidcounter = new AtomicLong(Long.MIN_VALUE + 1);
	
	/** Counters for message IDs. */
	protected AtomicLong messageidcounter = new AtomicLong(Long.MIN_VALUE);
	
	/** Bytes remaining before rekeying. */
	protected long remainingbytes;
	
	/** Currently active symcipher. */
	protected byte activesymcipher;
	
	/** The key exchange. */
	protected IKeyExchange kx;
	
	/**
	 *  Creates the management object.
	 *  @param symcipherclass The symmetric cipher class used for encryption.
	 *  @param keyexgen Generator for key exchange objects.
	 */
	public SymCipherManagement(int cipherclassid, KeyExchangeGenerator keyexgen)
	{
		this.keyexgen = keyexgen;
		this.cipherclassid = cipherclassid;
	}
	
	/**
	 *  Returns the ID of the selected cipher class.
	 *  @return The ID.
	 */
	public int getCipherClassId()
	{
		return cipherclassid;
	}
	
	/**
	 *  Returns current key exchange object.
	 *  @return Current key exchange object.
	 */
	public IKeyExchange getKx()
	{
		return kx;
	}
	
	/**
	 *  Creates a key exchange object.
	 */
	public void createKx()
	{
		this.kx = keyexgen.getKeyExchange();
	}
	
	/**
	 *  Destroys the key exchange object.
	 */
	public void destroyKx()
	{
		this.kx = null;
	}

	/**
	 *  Creates a symmetric cipher suite.
	 *  @param cipherid ID of the key used by the cipher.
	 *  @param symkey The key.
	 */
	public void createSymCipherSuite(byte cipherid, byte[] symkey)
	{
		try
		{
			if (ciphercon == null)
			{
				Class<?> scco = Class.forName(CIPHERS[cipherclassid]);
				ciphercon = scco.getConstructor(new Class<?>[] {byte[].class, Nonce.class});
			}
			ISymCipher symcipher = (ISymCipher) ciphercon.newInstance(new Object[] {symkey, new Nonce(SUdpUtil.getSecRandom())});
			symciphersuites[cipherid & 0xFF] = new SymCipherSuite(symcipher);
		}
		catch (Exception e)
		{
			if (e instanceof RuntimeException)
			{
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Gets a symmetric cipher suite.
	 *  @param cipherid ID of the key used by the cipher.
	 */
	public SymCipherSuite getSymCipherSuite(byte cipherid)
	{
		return symciphersuites[cipherid & 0xFF];
	}
	
	/**
	 *  Gets the currently active symmetric cipher suite.
	 */
	public SymCipherSuite getActiveSymCipherSuite()
	{
		return getSymCipherSuite(activesymcipher);
	}
	
	/**
	 *  Gets the currently active symmetric cipher ID.
	 */
	public byte getActiveSymCipherId()
	{
		return activesymcipher;
	}
	
	/**
	 *  Sets the currently active symmetric cipher ID.
	 *  @param cipherid The ID.
	 */
	public void setActiveSymCipherId(byte cipherid)
	{
		activesymcipher = cipherid;
	}
	
	/**
	 *  Destroys a symmetric cipher.
	 *  @param cipherid ID of the key used by the cipher.
	 */
	public void destroySymCipherSuite(byte cipherid)
	{
		symciphersuites[cipherid & 0xFF] = null;
	}
	
	/** 
	 *  Returns bytes remaining before rekeying.
	 *  @return Bytes remaining before rekeying.
	 */
	public long getRemainingBytes()
	{
		return remainingbytes;
	}
	
	/**
	 *  Subtracts a number of bytes from the remaining bytes.
	 *  @param bytes Bytes to subtract.
	 */
	public void subtractRemainingBytes(long bytes)
	{
		remainingbytes -= bytes;
	}
	
	/**
	 *  Refreshes the remaining bytes after rekeying.
	 */
	public void refreshRemainingBytes()
	{
		long halflife = STunables.MAX_KEY_LIFETIME >> 1;
		remainingbytes = (long) (halflife + (Math.random() * halflife));
	}
}
