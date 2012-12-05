package jadex.bridge.service.types.security;

import java.util.List;

/**
 * 
 */
public class MechanismInfo
{
	/** The mechanism name. */
	protected String name;
	
	/** The type. */
//	protected ClassInfo classinfo;
	protected Class<?> clazz;
	
	/** The parameter infos. */
	protected List<ParameterInfo> parameterinfos;
	
	/**
	 * 
	 */
	public MechanismInfo(String name, Class<?> clazz, List<ParameterInfo> parameterinfos)
	{
		this.name = name;
//		this.classinfo = new ClassInfo(clazz);
		this.clazz = clazz;
		this.parameterinfos = parameterinfos;
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
//	/**
//	 *  Get the classinfo.
//	 *  @return The classinfo.
//	 */
//	public ClassInfo getClassInfo()
//	{
//		return classinfo;
//	}
//
//	/**
//	 *  Set the classinfo.
//	 *  @param classinfo The classinfo to set.
//	 */
//	public void setClassInfo(ClassInfo classinfo)
//	{
//		this.classinfo = classinfo;
//	}

	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public Class<?> getClazz()
	{
		return clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Get the parameterinfos.
	 *  @return The parameterinfos.
	 */
	public List<ParameterInfo> getParameterInfos()
	{
		return parameterinfos;
	}

	/**
	 *  Set the parameterinfos.
	 *  @param parameterinfos The parameterinfos to set.
	 */
	public void setParameterInfos(List<ParameterInfo> parameterinfos)
	{
		this.parameterinfos = parameterinfos;
	}
}
