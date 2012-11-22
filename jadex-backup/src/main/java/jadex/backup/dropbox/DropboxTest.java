package jadex.backup.dropbox;
import jadex.backup.resource.FileData;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.RESTUtility;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

/**
 * 
 */
public class DropboxTest
{
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{
			String akey = "g60rq4ty063ap3q";
			String asecret = "2t6ipcy6of4g00o";
			String skey = "rhif2e2h0qtx8lr";
			String ssecret = "wm4yffhot70h4rn";
			
			DropboxAPI<WebAuthSession> api = createSession(akey, asecret, skey, ssecret);
			
			Map<String, Tuple2<FileData, List<String>>> fis = new HashMap<String, Tuple2<FileData,List<String>>>();
			getFileDatas(api, "/", fis);
			System.out.println(fis);
			
//			System.out.print("Uploading file...");
//			String str = "Hello World 2!";
//			ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());
//			Entry entry = api.putFileOverwrite("/testing.txt", is, str.length(), null);
//			Entry entry = api.putFile("/testing.txt", is, str.length(), null, null);
//			System.out.println("Done. \nRevision of file: " + entry.rev);
//			is.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
//		try
//		{
//			String key = "g60rq4ty063ap3q";
//			String secret = "2t6ipcy6of4g00o";
//			AppKeyPair keys = new AppKeyPair(key, secret);
//			WebAuthSession session = new WebAuthSession(keys, AccessType.DROPBOX);
//			WebAuthInfo authInfo = session.getAuthInfo();
//			RequestTokenPair pair = authInfo.requestTokenPair;
//			String url = authInfo.url;
//			Desktop.getDesktop().browse(new URL(url).toURI());
//			JOptionPane.showMessageDialog(null, "Press ok to continue once you have authenticated.");
//			session.retrieveWebAccessToken(pair);
//			AccessTokenPair tokens = session.getAccessTokenPair();
//			
//			System.out.println("Use this token pair in future so you don't have to re-authenticate each time:");
//			System.out.println("Key token: " + tokens.key);
//			System.out.println("Secret token: " + tokens.secret);
//	
//			DropboxAPI<WebAuthSession> api = new DropboxAPI<WebAuthSession>(session);
//			System.out.println();
//			System.out.print("Uploading file...");
//			String str = "Hello World!";
//			ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());
//			Entry entry = api.putFile("/testing.txt", is, str.length(), null, null);
//			System.out.println("Done. \nRevision of file: " + entry.rev);
//			is.close();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 
	 */
	public static void createInitialSession(String key, String secret) throws Exception
	{
		AppKeyPair keys = new AppKeyPair(key, secret);
		WebAuthSession session = new WebAuthSession(keys, AccessType.DROPBOX);
		WebAuthInfo authInfo = session.getAuthInfo();
		RequestTokenPair pair = authInfo.requestTokenPair;
		String url = authInfo.url;
		Desktop.getDesktop().browse(new URL(url).toURI());
		JOptionPane.showMessageDialog(null, "Press ok to continue once you have authenticated.");
		session.retrieveWebAccessToken(pair);
		AccessTokenPair tokens = session.getAccessTokenPair();
		System.out.println("Use this token pair in future so you don't have to re-authenticate each time:");
		System.out.println("Key token: " + tokens.key);
		System.out.println("Secret token: " + tokens.secret);
	}
	
	/**
	 * 
	 */
	public static DropboxAPI<WebAuthSession> createSession(String akey, String asecret, String skey, String ssecret) //throws Exception
	{
		AppKeyPair keys = new AppKeyPair(akey, asecret);
		WebAuthSession session = new WebAuthSession(keys, AccessType.DROPBOX);
		session.setAccessTokenPair(new AccessTokenPair(skey, ssecret));
		return new DropboxAPI<WebAuthSession>(session);
	}
	
//	/**
//	 * 
//	 */
//	public static <E extends Tuple2<FileInfo, List<E>>> List<E> 
//		listContent(DropboxAPI<WebAuthSession> api, String path) throws Exception
//	{
//		List<E> ret = new ArrayList<E>();
//		
//		Entry entry = api.metadata(path, -1, null, true, null);
//		if(entry.isDir)
//		{
//			List<Entry> files = entry.contents;
//			if(files!=null)
//			{
//				for(Entry e: files)
//				{
//					// why does conditional not work with generics?
//					List<E> lis = null;
//					if(e.isDir)
//						lis = listContent(api, e.path);
//					Tuple2<FileInfo, List<E>> tup = new Tuple2<FileInfo, List<E>>(createFileData(e), lis);
//					ret.add((E)tup);
//				}
//			}
//		}
//		
////		System.out.println("entries: "+entry.path+" "+entry.fileName()+" "+entry.isDir+" "+entry.contents);
//	
//		return ret;
//	}
	
	/**
	 * 
	 */
	public static void getFileDatas(DropboxAPI<WebAuthSession> api, String path, Map<String, Tuple2<FileData, List<String>>> fis) throws Exception
	{
		Entry entry = api.metadata(path, -1, null, true, null);
		
		List<String> nchildren = null;
		List<Entry> children = entry.contents;
		if(children!=null)
		{
			nchildren = new ArrayList<String>();
			for(Entry child: children)
			{
				nchildren.add(child.path);
			}
		}
		
		// add info for this file
		Tuple2<FileData, List<String>> tup = new Tuple2<FileData, List<String>>(createFileData(entry), nchildren);
		fis.put(path, tup);
			
		// recursive call for children
		if(children!=null)
		{
			for(Entry child: children)
			{
				getFileDatas(api, child.path, fis);
			}
		}
		
//		System.out.println("entries: "+entry.path+" "+entry.fileName()+" "+entry.isDir+" "+entry.contents);
	}
	
	/**
	 * 
	 */
	public static byte[] getFileContent(DropboxAPI<WebAuthSession> api, String path)
	{
		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			api.getFile(path, null, os, null);
			return os.toByteArray();
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}	
	
	/**
	 * 
	 */
	public static FileData getFileData(DropboxAPI<WebAuthSession> api, String path)
	{
		try
		{
			return createFileData(api.metadata(path, 0, null, false, null));
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}	
	
	/**
	 * 
	 */
	public static List<FileData> getChildren(DropboxAPI<WebAuthSession> api, String path)
	{
		return getChildren(api, path, null);
	}
	
	/**
	 * 
	 */
	public static List<FileData> getChildren(DropboxAPI<WebAuthSession> api, String path, IFilter<FileData> filter)
	{
		try
		{
			List<FileData> ret = null;
			Entry e = api.metadata(path, -1, null, true, null);
			List<Entry> cs = e.contents;
			if(cs!=null)
			{
				ret = new ArrayList<FileData>();
				for(Entry c: cs)
				{
					FileData cfd = createFileData(c);
					if(filter==null || filter.filter(cfd))
					{
						ret.add(cfd);
					}
				}
			}
			return ret;
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public static void overwriteFileData(DropboxAPI<WebAuthSession> api, String path, InputStream is, long length)
	{
		try
		{
			api.putFileOverwrite(path, is, length, null);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public static void putFileData(DropboxAPI<WebAuthSession> api, String path, InputStream is, long length)
	{
		try
		{
			api.putFile(path, is, length, null, null);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public static void setLastModified(DropboxAPI<WebAuthSession> api, String path, long date)
	{
		try
		{
			System.out.println("todo: set last modified in dropbox file");
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public static FileData createFileData(Entry entry)
	{
		// todo: what about other file meta data stored in entries?
		return new FileData(entry.path, entry.isDir, !entry.isDeleted, entry.bytes, entry.modified!=null? RESTUtility.parseDate(entry.modified).getTime(): 0);
	}
}
