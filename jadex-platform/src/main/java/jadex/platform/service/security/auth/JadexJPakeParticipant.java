package jadex.platform.service.security.auth;

import java.lang.reflect.Field;
import java.math.BigInteger;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;

import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;

/**
 *  Extends the JPAKEParticipant of Bouncy with some functionality.
 *
 */
public class JadexJPakeParticipant extends JPAKEParticipant
{
	/** Field access for setting JPAKE password late. */
	protected static final Field JPAKE_PW_FIELD;
	static
	{
		Field pwfield = null;
		try
		{
			pwfield = JPAKEParticipant.class.getDeclaredField("password");
			pwfield.setAccessible(true);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		JPAKE_PW_FIELD = pwfield;
	}
	
	/** Empty password for initializing. */
	protected static final char[] DUMMY_PW = new char[1];
	
	/** Digest. */
    protected Digest digest;
    
    /** The derived key. */
    protected byte[] derivedkey;
	
	/**
	 *  Create a participant.
	 */
	public JadexJPakeParticipant(String participantid, Digest digest)
	{
		super(participantid, DUMMY_PW, JPAKEPrimeOrderGroups.NIST_3072, digest, SSecurity.getSecureRandom());
		this.digest = digest;
	}
	
	/**
	 *  Create a participant.
	 */
	public JadexJPakeParticipant(String participantid, String password, Digest digest)
	{
		super(participantid, password.toCharArray(), JPAKEPrimeOrderGroups.NIST_3072, digest, SSecurity.getSecureRandom());
		this.digest = digest;
	}
	
	/**
	 *  Calculates keying material and derives key.
	 */
	public BigInteger calculateKeyingMaterial()
	{
		BigInteger ret = super.calculateKeyingMaterial();
		
		byte[] retasarr = ret.toByteArray();
		derivedkey = new byte[digest.getDigestSize()];
		
		digest.reset();
		digest.update(retasarr, 0, retasarr.length);
		digest.doFinal(derivedkey, 0);
		digest.reset();
		
		return ret;
	}
	
	/**
	 *  Returns the derived key.
	 *  
	 *  @return The derived key.
	 */
	public byte[] getDerivedKey()
	{
		return derivedkey;
	}
	
	/**
	 *  Sets the password after round 1.
	 *  
	 *  @param password The password.
	 */
	public void setPassword(byte[] password)
	{
		char[] chararr = (new String(Base64.encodeNoPadding(password), SUtil.ASCII).toCharArray());
		try
		{
			JPAKE_PW_FIELD.set(this, chararr);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Disposes the exchange.
	 */
	public void dispose()
	{
		if (derivedkey != null)
			SSecurity.getSecureRandom().nextBytes(derivedkey);
	}
	
	/**
	 *  Override.
	 */
	protected void finalize() throws Throwable
	{
		super.finalize();
		dispose();
	}
}
