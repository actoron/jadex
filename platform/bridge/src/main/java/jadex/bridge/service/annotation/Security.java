package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Set the roles that would allow access to a service interface or service method.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Security
{
	//-------- constants --------
	
	/** The unrestricted role (access is granted to all), e.g. used for chat. */
	public static final String	UNRESTRICTED	= "__jadex-role-unrestricted__";

	/** The default role that is assigned to services without security annotation and granted in all authenticated networks. */
	public static final String	DEFAULT	= "__jadex-role-default__";

	/** The admin role that is required by all jadex system services, e.g. CMS. */
	public static final String	ADMIN	= "__jadex-role-admin__";
	
	//-------- properties --------
	
	/**
	 *  Use predefined role: see constants unrestricted, default and admin.
	 *  Custom role(s): Allow only authentication secrets (e.g. network or platform key)
	 *  that are locally given at least one of the requested roles.
	 */
	public String[] roles()	default DEFAULT;
}
