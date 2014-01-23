package jadex.platform.service.library;

import jadex.bridge.IResourceIdentifier;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.DexClassLoader;

public class DexDelegationClassLoader extends DelegationURLClassLoader
{
	
	private DexClassLoader dexCl;

	public DexDelegationClassLoader(IResourceIdentifier rid, ClassLoader basecl, DelegationURLClassLoader[] delegates, File dexOutputPath)
	{
		super(rid, basecl, delegates);
		URL url = rid.getLocalIdentifier().getUrl();
		dexCl = new DexClassLoader(url.getPath(),dexOutputPath.getAbsolutePath(), null, basecl);
	}
	
	public DexDelegationClassLoader(IResourceIdentifier rid, ClassLoader basecl, DexClassLoader delegate) {
		super(rid, basecl, null);
		this.dexCl = delegate;
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		return dexCl.loadClass(name);
	}

	@Override
	protected Class<?> loadDirectClass(String name, boolean resolve) throws ClassNotFoundException
	{
		return dexCl.loadClass(name);
	}

	@Override
	protected URL findDirectResource(String name)
	{
		return dexCl.getResource(name);
	}

	@Override
	protected Enumeration<URL> findDirectResources(String name) throws IOException
	{
		return dexCl.getResources(name);
	}
	
}
