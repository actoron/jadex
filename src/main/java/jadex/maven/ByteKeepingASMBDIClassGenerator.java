package jadex.maven;

import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.ASMBDIClassGenerator;

public class ByteKeepingASMBDIClassGenerator extends ASMBDIClassGenerator
{
	Map<String, byte[]> classBytes;
	
	public ByteKeepingASMBDIClassGenerator() {
		classBytes = new HashMap<String, byte[]>();
	}
	
	@Override
	public Class<?> toClass(String name, byte[] data, ClassLoader loader, ProtectionDomain domain)
	{
		classBytes.put(name, data);
		return super.toClass(name, data, loader, domain);
	}

	public byte[] getClassBytes(String name)
	{
		return classBytes.get(name);
	}
}
