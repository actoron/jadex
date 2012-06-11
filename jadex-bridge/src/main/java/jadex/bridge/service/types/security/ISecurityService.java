package jadex.bridge.service.types.security;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
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
@GuiClassNames({
	@GuiClassName("jadex.tools.security.gui.SecuritySettings"),
	@GuiClassName("jadex.android.controlcenter.settings.SecuritySettings")
})
public interface ISecurityService
{
	//-------- password management --------
	
	/**
	 *  Check if password protection is enabled.
	 *  @return	True, if password protection is enabled.
	 */
	public IFuture<Boolean>	isUsePassword();

	/**
	 *  Enable / disable password protection.
	 *  @param enable	If true, password protection is enabled, otherwise disabled.
	 *  @throws Exception, when enable is true and no password is set.
	 */
	public IFuture<Void>	setUsePassword(boolean enable);

	/**
	 *  Get the local password.
	 *  @return	The password of the local platform (if set).
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getLocalPassword();

	/**
	 *  Set the local password.
	 *  @param password	The password of the local platform.
	 *  @throws  Exception, when a null password is provided and use password is true.
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
	public IFuture<String>	getPlatformPassword(IComponentIdentifier target);

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
	public IFuture<Void>	setPlatformPassword(IComponentIdentifier target, String password);
	
	/**
	 *  Get the password for a network.
	 *  @param target	The id of the target component.
	 *  @return	The stored password. Returns null if no password is stored, unless the
	 *    component is a local component in which case the local password (if any) is returned.
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getNetworkPassword(String network);

	/**
	 *  Set the password for a network.
	 *  @param network	The id of the network.
	 *  @param password	The password or null if no password should be used.
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setNetworkPassword(String network, String password);

	/**
	 *  Get all stored platform passwords.
	 *  @return A map containing the stored passwords as pairs (platform name -> password).
	 */
	// Todo: passwords are transferred in plain text unless transport uses encryption.
	public IFuture<Map<String, String>>	getPlatformPasswords();
	
	/**
	 *  Get all stored network passwords.
	 *  @return A map containing the stored passwords as pairs (network name -> password).
	 */
	// Todo: passwords are transferred in plain text unless transport uses encryption.
	public IFuture<Map<String, String>>	getNetworkPasswords();

	/**
	 *  Set the trusted lan mode.
	 *  @param allowed The flag if it is allowed.
	 */
	public IFuture<Void> setTrustedLanMode(boolean allowed);
	
	/**
	 *  Get the trusted lan mode.
	 *  @return True if is in trusted lan mode.
	 */
	public IFuture<Boolean> isTrustedLanMode();
	
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
