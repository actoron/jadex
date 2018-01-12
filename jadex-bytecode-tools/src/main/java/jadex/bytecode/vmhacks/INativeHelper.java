package jadex.bytecode.vmhacks;

import java.lang.reflect.AccessibleObject;

/**
 *  Interface for classes implementing native helper functions.
 *	
 *	Used for utility.
 *
 *	Various methods are used to get around restrictions, this is
 *  one of them. Therefore, do not use this directly,
 *  use VmHacks.get().
 */
public interface INativeHelper
{
	/**
	 *  Attempts to change the user of the process to the given name.
	 *  If set to null, the user name "nobody" is tried.
	 *  
	 *  @param username The target user name, set to null for "nobody".
	 *  @return True, if successful, false if the attempt probably failed.
	 */
	public boolean tryChangeUser(String username);
	
	/**
	 *  Sets reflective object accessible without checks.
	 *  
	 *  @param accobj The accessible object.
	 *  @param flag The flag value.
	 */
	public void setAccessible(String flagname, AccessibleObject accobj, boolean flag);
	
	/**
	 *  Tests if the setAccessible() method can be used.
	 *  @return True, if method can be used.
	 */
	public boolean canSetAccessible();
	
	/**
	 *  Gets a pointer to the VM.
	 *  
	 *  @return Pointer to VM.
	 */
	public long getVm();
	
	/**
     * Define a class in any ClassLoader.
     */
	public Class<?> defineClass(String name, byte[] b, ClassLoader loader);
}
