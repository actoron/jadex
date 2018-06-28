package jadex.platform.service.library;

import jadex.bridge.service.types.library.ISimpleDelegationClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class DelegationClassLoader extends ChangeableURLClassLoader implements ISimpleDelegationClassLoader
{

	private ISimpleDelegationClassLoader delegate;

	public DelegationClassLoader(ClassLoader parent)
	{
		super(new URL[0], parent);
	}
	
	public void setDelegate(ISimpleDelegationClassLoader del) {
		this.delegate = del;
	}
	
	public ISimpleDelegationClassLoader getDelegate()
	{
		return delegate;
	}

	@Override
	public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		if (delegate != null) {
			return delegate.loadClass(name,resolve);
		} else {
			return super.loadClass(name, resolve);
		}
	}
	
	@Override
	public URL getResource(String name)
	{
		if (delegate != null) {
			return delegate.getResource(name);
		} else {
			return super.getResource(name);
		}
	}
	
	@Override
	public InputStream getResourceAsStream(String name)
	{
		if (delegate != null) {
			return delegate.getResourceAsStream(name);
		} else {
			return super.getResourceAsStream(name);
		}
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException
	{
		if (delegate != null) {
			return delegate.getResources(name);
		} else {
			return super.getResources(name);
		}
	}
	
	
	
	
}
