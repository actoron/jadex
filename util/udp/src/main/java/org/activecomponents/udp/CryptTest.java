/**
 * 
 */
package org.activecomponents.udp;

import java.io.UnsupportedEncodingException;

import org.activecomponents.udp.symciphers.GcmAesCipher;
import org.activecomponents.udp.symciphers.Nonce;

/**
 * @author jander
 *
 */
public class CryptTest
{
	public static void main(String[] args)
	{
		String valstr = "Hello";
		String valstr2 = "World";
		byte[] val = null;
		byte[] val2 = null;
		try
		{
			val = valstr.getBytes("UTF-8");
			val2 = valstr2.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		byte[] key = new byte[32];
		SUdpUtil.getSecRandom().nextBytes(key);
		
		GcmAesCipher sc = new GcmAesCipher(key, new Nonce(SUdpUtil.getSecRandom()));
		
		byte[] ct1 = sc.encrypt(val);
		byte[] ct2 = sc.encrypt(val2);
		
		val = null;
		val2 = null;
		
		val2 = sc.decrypt(ct2);
		
		val = sc.decrypt(ct1);
		
		try
		{
			System.out.print(new String(val, "UTF-8"));
			System.out.print(" ");
			System.out.println(new String(val2, "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		byte[] small = new byte[32];
		byte[] medium = new byte[1024];
		byte[] large = new byte[8192];
		byte[] huge = new byte[32768];
		SUdpUtil.getSecRandom().nextBytes(small);
		SUdpUtil.getSecRandom().nextBytes(medium);
		SUdpUtil.getSecRandom().nextBytes(large);
		SUdpUtil.getSecRandom().nextBytes(huge);
		
		long ts = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i)
		{
			sc.encrypt(small);
		}
		System.out.println("Small: " + ((System.currentTimeMillis() - ts) / 10000.0));
		
		ts = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i)
		{
			sc.encrypt(medium);
		}
		System.out.println("Medium: " + ((System.currentTimeMillis() - ts) / 10000.0));
		
		ts = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i)
		{
			sc.encrypt(large);
//			byte[] enc = sc.encrypt(large);
//			sc.decrypt(enc);
		}
		System.out.println("Large: " + ((System.currentTimeMillis() - ts) / 10000.0));
		
		ts = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i)
		{
			sc.encrypt(huge);
		}
		System.out.println("Huge: " + ((System.currentTimeMillis() - ts) / 10000.0));
	}
}
