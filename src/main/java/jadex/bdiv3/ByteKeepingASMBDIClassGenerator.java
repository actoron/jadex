package jadex.bdiv3;

import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.ASMBDIClassGenerator;

public class ByteKeepingASMBDIClassGenerator extends ASMBDIClassGenerator
{
	private Map<String, byte[]> classBytes;
	
	private Map<String, byte[]> recentClassBytes;
	
	public ByteKeepingASMBDIClassGenerator() {
		classBytes = new HashMap<String, byte[]>();
		recentClassBytes = new HashMap<String, byte[]>();
	}
	
	@Override
	public Class<?> toClass(String name, byte[] data, ClassLoader loader, ProtectionDomain domain)
	{
		Class<?> result = null;
		if (!classBytes.containsKey(name)) {
			// return null if this class has already been enhanced before to avoid
			// duplicates.
			result = super.toClass(name, data, loader, domain);
			if (result != null) {
				classBytes.put(name, data);
				recentClassBytes.put(name, data);
			}
		}
		return result;
	}

//	public byte[] getClassBytes(String name)
//	{
//		return classBytes.get(name);
//	}

	public Map<String, byte[]> getRecentClassBytes()
	{
		return recentClassBytes;
	}

	public void clearRecentClassBytes() {
		recentClassBytes.clear();
	}
	
	
}
