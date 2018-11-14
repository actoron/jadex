package jadex.commons.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.security.SSecurity;

/**
 *  Class for loading / saving the certificate store.
 *
 */
public class SCertStore
{
	public static final List<Tuple2<String, String>> loadCertStore(String path)
	{
		File file = new File(path);
		List<Tuple2<String, String>> ret = null;
		
		if (file.exists())
		{
			ZipInputStream zis = null;
			try
			{
				zis = new ZipInputStream(new FileInputStream(file));
				Map<String, String[]> map = new HashMap<String, String[]>();
				ret = new ArrayList<Tuple2<String,String>>();
				
				ZipEntry entry = null;
				while((entry = zis.getNextEntry()) != null)
				{
					if (entry.getName().endsWith(".crt") || entry.getName().endsWith(".key"))
					{
						String basename = entry.getName().substring(0, entry.getName().length() - 4);
						String[] tup = map.get(basename);
						if (tup == null)
						{
							tup = new String[2];
							map.put(basename, tup);
						}
						
						if (entry.getName().endsWith(".crt"))
						{
							String crt = new String(SUtil.readStream(zis), SUtil.UTF8);
							tup[0] = crt;
						}
						else if (entry.getName().endsWith(".key"))
						{
							String key = new String(SUtil.readStream(zis), SUtil.UTF8);
							tup[1] = key;
						}
					}
				}
				
				for (String[] val : map.values())
				{
					if (val[0] != null)
						ret.add(new Tuple2<String, String>(val[0], val[1]));
				}
				
				zis.close();
				zis = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ret = null;
			}
			finally
			{
				try
				{
					if (zis != null)
						zis.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return ret;
	}
	
	public static void saveCertStore(String path, Collection<Tuple2<String, String>> certs)
	{
		ZipOutputStream zos = null;
		try
		{
			File tmpfile = File.createTempFile("certstore", ".zip");
			
			zos = new ZipOutputStream(new FileOutputStream(tmpfile));
			
			for (Tuple2<String, String> cert : certs)
			{
				String name = SSecurity.readCertificateFromPEM(cert.getFirstEntity()).getSubject().toString();
				
				ZipEntry entry = new ZipEntry(name + ".crt");
				zos.putNextEntry(entry);
				zos.write(cert.getFirstEntity().getBytes(SUtil.UTF8));
				zos.closeEntry();
				
				if (cert.getSecondEntity() != null)
				{
					entry = new ZipEntry(name + ".key");
					zos.putNextEntry(entry);
					zos.write(cert.getSecondEntity().getBytes(SUtil.UTF8));
					zos.closeEntry();
				}
			}
			
			zos.close();
			zos = null;
			
			File file = new File(path);
			SUtil.moveFile(tmpfile, file);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			try
			{
				if (zos != null)
					zos.close();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	public static final Map<String, Tuple2<String, String>> convertToSubjectMap(Collection<Tuple2<String, String>> certs)
	{
		Map<String, Tuple2<String, String>> ret = new HashMap<String, Tuple2<String,String>>();
		for (Tuple2<String, String> cert : certs)
		{
			String key = SSecurity.readCertificateFromPEM(cert.getFirstEntity()).getSubject().toString();
			ret.put(key, cert);
		}
		return ret;
	}
}
