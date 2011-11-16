package jadex.bridge.service.types.security;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  The security service is responsible for
 *  validating (remote) requests.
 *  Currently only platform level authentication
 *  is provided. More fine grained control on
 *  service/method level based on user/group
 *  access rights is planned for the mid-term future.
 */
// Safe to be allowed remotely, as it can only be called, when platform access is granted.
// Putting method in service allows security settings to be administered using remote JCCs.
public interface ISecurityService
{
	//-------- password management --------
	
	/**
	 *  Get the local password.
	 *  @return	The password of the local platform (if any).
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getLocalPassword();

	/**
	 *  Set the local password.
	 *  If the password is set to null, acces is granted to all requests.
	 *  @param password	The password of the local platform (if any). 
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setLocalPassword(String password);

	/**
	 *  Get the password for a target component.
	 *  @param target	The id of the target component.
	 *  @return	The stored password. Returns null if no password is stored, unless the
	 *    component is a local component in which case the local password (if any) is returned.
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getTargetPassword(IComponentIdentifier target);

	/**
	 *  Set the password for a target component.
	 *  Note that passwords are currently stored on a per platform basis,
	 *  i.e. there is only one stored password for all components of the same platform.
	 *  Moreover, the security service strips the auto-generated extension from the platform
	 *  name and therefore can reuse the password for different instances of the same platform.
	 *  @param target	The id of the target component.
	 *  @param password	The password or null if no password should be used.
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setTargetPassword(IComponentIdentifier target, String password);
	
	/**
	 *  Get all stored passwords.
	 *  @return A map containing the stored passwords as pairs (platform name -> password).
	 */
	public IFuture<Map<String, String>>	getStoredPasswords();
	
	//-------- request validation --------
	
	/**
	 *  Validate a request.
	 *  @param request	The request to be validated.
	 *  @throws	SecurityException, when request is not valid.
	 */
	public IFuture<Void>	validateRequest(IAuthorizable request);
	
	/**
	 *  Preprocess a request.
	 *  Adds authentication data to the request, if required by the intended target.
	 *  @param request	The request to be preprocessed.
	 *  @param target	The target to which the request should be sent later.
	 */
	public IFuture<Void>	preprocessRequest(IAuthorizable request, IComponentIdentifier target);
}
