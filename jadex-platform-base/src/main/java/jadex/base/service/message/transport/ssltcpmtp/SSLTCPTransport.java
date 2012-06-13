package jadex.base.service.message.transport.ssltcpmtp;

import jadex.base.service.message.transport.tcpmtp.TCPTransport;
import jadex.bridge.service.IServiceProvider;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 *  The ssl based transport.
 *  
 *  It requires a keystore being present in the start directory.
 *  This keystore can be generated manually with:
 *  
 *  keytool -genkey -keystore keystore -keyalg RSA
 *  
 *  Currently the transport does not check the certificates as this
 *  would require to install all trusted platforms (servers) in all
 *  platforms.
 */
public class SSLTCPTransport extends TCPTransport
{
	/** The schema name. */
	public final static String[] SCHEMAS = new String[]{"ssltcp-mtp://"};

	/** The ssl context. */
	protected SSLContext context;
	
	//-------- constructors --------

	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public SSLTCPTransport(final IServiceProvider container, int port)
	{
		super(container, port, true);
	}

	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public SSLTCPTransport(final IServiceProvider container, int port, final boolean async)
	{
		super(container, port, async);
	}
	
	/**
	 *  Get the ssl context.
	 */
	public SSLContext getSSLContext()
	{
		if(context==null)
		{
			try
			{
				KeyManagerFactory kmf;
				KeyStore ks;
				char[] storepass = "keystore".toCharArray();
				char[] keypass = "keystore".toCharArray();
				String storename = "./keystore";
				
				context = SSLContext.getInstance("TLS");
				kmf = KeyManagerFactory.getInstance("SunX509");
				FileInputStream fin = new FileInputStream(storename);
				ks = KeyStore.getInstance("JKS");
				ks.load(fin, storepass);
				
				kmf.init(ks, keypass);
				
				// This allows ssl handshake without signed certificates.
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(ks);				
				NaiveTrustManager tm = new NaiveTrustManager();
				context.init(kmf.getKeyManagers(), new TrustManager[]{tm}, new SecureRandom());

//				context.init(kmf.getKeyManagers(), null, null);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return context;
	}
	
	//-------- methods --------

	/**
	 *  Create a server socket.
	 *  @return The server socket.
	 */
	public ServerSocket createServerSocket() throws Exception
	{
		SSLServerSocketFactory fac = getSSLContext().getServerSocketFactory();
        SSLServerSocket ret = (SSLServerSocket)fac.createServerSocket(port);
        return ret;
	}
	
	/**
	 *  Create a client socket.
	 *  @return The client socket.
	 */
	public Socket createClientSocket(String host, int port) throws Exception
	{
		SSLSocketFactory fac = getSSLContext().getSocketFactory();
        SSLSocket ret = (SSLSocket)fac.createSocket(host, port);
        return ret;
	}
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String[] getServiceSchemas()
	{
		return SCHEMAS;
	}
	
	/**
	 *  Trust manager that does not check certificates. 
	 */
	protected static class NaiveTrustManager implements X509TrustManager
	{
		public X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
		{
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
		{
		}
	}
	
}
