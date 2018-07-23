package jadex.commons.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jadex.commons.Base64;
import jadex.commons.SUtil;

/**
 *  Class for loading / saving the certificate store.
 *
 */
public class SCertStore
{
	/** Prefix for encoded names. */
	protected static final String ENCODED_NAME_PREFIX = "___encodedname___"; 
	
	/**
	 *  Loads the cert store.
	 *  
	 *  @param storedata Cert store as binary.
	 *  @return return Certs.
	 */
	public static final Map<String, PemKeyPair> loadCertStore(byte[] storedata)
	{
		ZipInputStream zis = null;
		Map<String, PemKeyPair> ret = new HashMap<>();
		
		if (storedata != null && storedata.length > 0)
		{
			try
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(storedata);
				zis = new ZipInputStream(bais);
				ret = new HashMap<>();
				
				ZipEntry entry = null;
				while((entry = zis.getNextEntry()) != null)
				{
					if (entry.getName().endsWith(".crt") || entry.getName().endsWith(".key"))
					{
						String basename = entry.getName().substring(0, entry.getName().length() - 4);
						if (basename.startsWith(ENCODED_NAME_PREFIX))
						{
							basename = basename.substring(ENCODED_NAME_PREFIX.length());
							basename = new String(Base64.decodeNoPadding(basename.getBytes(SUtil.UTF8)), SUtil.UTF8);
						}
						
						PemKeyPair keypair = ret.get(basename);
						if (keypair == null)
						{
							keypair = new PemKeyPair();
							ret.put(basename, keypair);
						}
						
						if (entry.getName().endsWith(".crt"))
						{
							String crt = new String(SUtil.readStream(zis), SUtil.UTF8);
							keypair.setCertificate(crt);
						}
						else if (entry.getName().endsWith(".key"))
						{
							String key = new String(SUtil.readStream(zis), SUtil.UTF8);
							keypair.setKey(key);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ret = null;
			}
			finally
			{
				SUtil.close(zis);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Saves the cert store.
	 *  
	 *  @param certs The certs.
	 *  @return Cert store as binary.
	 */
	public static byte[] saveCertStore(Collection<PemKeyPair> certs)
	{
		byte[] ret = null;
		ZipOutputStream zos = null;
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(baos);
			
			for (PemKeyPair cert : certs)
			{
				String name = SSecurity.getCommonName(SSecurity.readCertificateFromPEM(cert.getCertificate()).getSubject());
				
				if (!SUtil.ASCII.newEncoder().canEncode(name) || name.contains("/") || name.contains("."))
					name = ENCODED_NAME_PREFIX + new String(Base64.encodeNoPadding(name.getBytes(SUtil.UTF8)), SUtil.UTF8);
				
				ZipEntry entry = new ZipEntry(name + ".crt");
				zos.putNextEntry(entry);
				zos.write(cert.getCertificate().getBytes(SUtil.UTF8));
				zos.closeEntry();
				
				if (cert.getKey() != null)
				{
					entry = new ZipEntry(name + ".key");
					zos.putNextEntry(entry);
					zos.write(cert.getKey().getBytes(SUtil.UTF8));
					zos.closeEntry();
				}
			}
			
			ret = baos.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(zos);
		}
		
		return ret;
	}
	
	/**
	 *  Converts certificates to a lookup-manp
	 *  @param certs The certificates.
	 *  @return Lookup map.
	 */
//	public static final Map<String, PemKeyPair> convertToSubjectMap(Collection<PemKeyPair> certs)
//	{
//		Map<String, PemKeyPair> ret = new HashMap<>();
//		for (PemKeyPair cert : certs)
//		{
//			String key = SSecurity.readCertificateFromPEM(cert.getCertificate()).getSubject().toString();
//			ret.put(key, cert);
//		}
//		return ret;
//	}
}
