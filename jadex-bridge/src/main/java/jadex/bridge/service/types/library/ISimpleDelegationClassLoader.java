package jadex.bridge.service.types.library;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Interface for DexClassLoaders that are capable of delegating classloading
 * requests.
 */
public interface ISimpleDelegationClassLoader
{
	/**
	 * Set the delegation ClassLoader which will respond to all classloading
	 * requests.
	 * 
	 * @param del
	 */
	public void setDelegate(ISimpleDelegationClassLoader del);
	public ISimpleDelegationClassLoader getDelegate();
	
	// ----- delegate methods -----
	
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException;
	public URL getResource(String name);
	public InputStream getResourceAsStream(String name);
	public Enumeration<URL> getResources(String name) throws IOException;


}
