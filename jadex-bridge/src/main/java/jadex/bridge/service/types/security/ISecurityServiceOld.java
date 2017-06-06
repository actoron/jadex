package jadex.bridge.service.types.security;

import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.commons.ChangeEvent;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

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
	@GuiClassName("jadex.tools.security.SecuritySettingsPanel"),
	@GuiClassName("jadex.android.controlcenter.settings.SecuritySettings")
})
public interface ISecurityServiceOld
{
	public static final String CERTIFICATE = "certificate";
	public static final String TRUSTED_CERTIFICATE = "trusted_certificate";
	public static final String KEYPAIR = "keypair";
	
	/** The event types. */
	public static final String PROPERTY_USEPASS = "usepass";
	
	/** The trusted lan property. */
	public static final String PROPERTY_TRUSTEDLAN = "trustedlan";

	/** The localpass property. */
	public static final String PROPERTY_LOCALPASS = "localpass";

	/** The platformpass property. */
	public static final String PROPERTY_PLATFORMPASS = "platformpass";

	/** The networkpass property. */
	public static final String PROPERTY_NETWORKPASS = "networkpass";

	/** The keystore settings property. */
	public static final String PROPERTY_KEYSTORESETTINGS = "keystoresettings";
	
	/** The keystore entries property. */
	public static final String PROPERTY_KEYSTOREENTRIES = "keystoreentries";

	/** The acquisition mechanism. */
	public static final String PROPERTY_SELECTEDMECHANISM = "selmechanism";
	
	/** A mechanism parameter changed. */
	public static final String PROPERTY_MECHANISMPARAMETER = "mechanismparameter";
	
	/** The validity duration changed. */
	public static final String PROPERTY_VALIDITYDURATION = "validityduration";

	/** The validity duration changed. */
	public static final String PROPERTY_VIRTUALS = "virtuals";

	
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
	@SecureTransmission
	public IFuture<Void>	setUsePassword(boolean enable);

	/**
	 *  Get the local password.
	 *  @return	The password of the local platform (if set).
	 */
	@SecureTransmission
	public IFuture<String>	getLocalPassword();

	/**
	 *  Set the local password.
	 *  @param password	The password of the local platform.
	 *  @throws  Exception, when a null password is provided and use password is true.
	 */
	@SecureTransmission
	public IFuture<Void>	setLocalPassword(String password);

	
	/**
	 *  Get the password for a target component.
	 *  @param target	The id of the target component.
	 *  @return	The stored password. Returns null if no password is stored, unless the
	 *    component is a local component in which case the local password (if any) is returned.
	 */
	@SecureTransmission
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
	@SecureTransmission
	public IFuture<Void>	setPlatformPassword(IComponentIdentifier target, String password);
	
	/**
	 *  Get the password for a network.
	 *  @param target	The id of the target component.
	 *  @return	The stored password. Returns null if no password is stored, unless the
	 *    component is a local component in which case the local password (if any) is returned.
	 */
	@SecureTransmission
	public IFuture<String>	getNetworkPassword(String network);

	/**
	 *  Set the password for a network.
	 *  @param network	The id of the network.
	 *  @param password	The password or null if no password should be used.
	 */
	@SecureTransmission
	public IFuture<Void>	setNetworkPassword(String network, String password);

	/**
	 *  Get all stored platform passwords.
	 *  @return A map containing the stored passwords as pairs (platform name -> password).
	 */
	@SecureTransmission
	public IFuture<Map<String, String>>	getPlatformPasswords();
	
	/**
	 *  Get all stored network passwords.
	 *  @return A map containing the stored passwords as pairs (network name -> password).
	 */
	@SecureTransmission
	public IFuture<Map<String, String>>	getNetworkPasswords();

	/**
	 *  Get the validity duration.
	 *  @return The validityduration.
	 */
	@SecureTransmission
	public IFuture<Long> getValidityDuration();

	/**
	 *  Set the validity duration.
	 *  @param validityduration The validityduration to set.
	 */
	@SecureTransmission
	public IFuture<Void> setValidityDuration(long validityduration);
	
	/**
	 *  Set the trusted lan mode.
	 *  @param allowed The flag if it is allowed.
	 */
	@SecureTransmission
	public IFuture<Void> setTrustedLanMode(boolean allowed);
	
	/**
	 *  Get the trusted lan mode.
	 *  @return True if is in trusted lan mode.
	 */
	public IFuture<Boolean> isTrustedLanMode();
	
	/**
	 *  Set the keystore info.
	 *  @return The path to the keystore. The password of the store. The password of the key.
	 */
	public IFuture<String[]> getKeystoreInfo();
	
	/**
	 *  Set the keystore info.
	 *  @param path The path to the keystore.
	 *  @param storepass The password of the store.
	 *  @param keypass The password of the key.
	 */
	@SecureTransmission
	public IFuture<Void> setKeystoreInfo(String path, String storepass, String keypass);
	
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
	 *  @param dur The request validity duration.
	 */
	public IFuture<Void>	preprocessRequest(IAuthorizable request, IComponentIdentifier target);
	
	//-------- message-level encryption/authentication -------
	
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param receiver The receiver.
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public IFuture<byte[]> encryptAndSign(Map<String, Object> header, byte[] content);
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param sender The sender.
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public IFuture<Tuple2<IMsgSecurityInfos,byte[]>> decryptAndAuth(IComponentIdentifier sender, byte[] content);
	
	/**
	 *  Creates an authenticator for the platform the security service is running on.
	 *  
	 *  @param receiver Receiver of the authentication, if available.
	 *  @return The authenticator.
	 */
	public IFuture<byte[]> createPlatformAuthenticator(IComponentIdentifier receiver);
	
	/**
	 *  Verifies a platform authenticator.
	 *  
	 *  @return Platform identifier if the platform was authenticated.
	 */
	public IFuture<IComponentIdentifier> verifyPlatformAuthenticator(byte[] authenticator);
	
	//-------- service call authentication --------
	
	/**
	 *  Sign a byte[] with the platform key that is stored in the
	 *  keystore under the platform prefix name.
	 */
	public IFuture<byte[]> signCall(byte[] content);
	
	/**
	 *  Verify an authenticated service call.
	 *  @param content The content that should be checked.
	 *  @param signed The desired output hash.
	 *  @param name The callers name (used to find the certificate and public key). 
	 */
	@SecureTransmission
	public IFuture<Void> verifyCall(final byte[] content, final byte[] signed, final String name);
	
	/**
	 *  Check if the name belongs to the mappings of one
	 *  of the virtual names.
	 *  @param virtuals The virtual names.
	 *  @param name The name to check.
	 */
	@SecureTransmission
	public IFuture<Void> checkVirtual(String[] virtuals, String name);
	
	/**
	 *  Add a name to the mappings of a virtual name.
	 *  @param virtual The virtual name.
	 *  @param name The name to add.
	 */
	@SecureTransmission
	public IFuture<Void> addVirtual(String virtual, String name);
	
	/**
	 *  Remove a name from the mappings of a virtual name.
	 *  @param virtual The virtual name.
	 *  @param name The name to remove.
	 */
	@SecureTransmission
	public IFuture<Void> removeVirtual(String virtual, String name);
	
	/**
	 *  Get the virtual names and their contents.
	 *  @return The map of virtual names and their platform mappings.
	 */
	@SecureTransmission
	public IFuture<Map<String, Set<String>>> getVirtuals();
	
	//-------- keystore handling --------
	
	//todo: add 
	// IFuture<Certificate> getPlatformCertificate(String type, String val);
	// IFuture<Set<Certificate>> getPlatformCertificates(); // allow more than 1
	/**
	 *  Get the certificate of a platform.
	 *  @param cid The platform component identifier (null for own certificate).
	 *  @return The certificate.
	 */
	public IFuture<Certificate> getPlatformCertificate(IComponentIdentifier cid);
	
	/**
	 *  Add a trusted certificate of a platform.
	 *  @param name The entry name.
	 *  @param cert The certificate.
	 */
	public IFuture<Void> addPlatformCertificate(IComponentIdentifier cid, Certificate cert);
	
	/**
	 *  Create a key pair entry.
	 *  @param cid The entry name.
	 *  @param algorithm The algorithm.
	 *  @param keysize The key size (in bits).
	 */
	@SecureTransmission // Protect password
	public IFuture<Void> createKeyPair(IComponentIdentifier cid, String algorithm, int keysize, String password, int validity);
	
	/**
	 *  Remove a key store entry.
	 *  @param String alias The alias name.
	 */
	public IFuture<Void> removeKeyStoreEntry(String alias);
	
	/**
	 *  Get info about the current keystore that is used.
	 *  @return Info about the keystore content.
	 */
	public IFuture<Map<String, KeyStoreEntry>> getKeystoreDetails();
	
	//-------- certificate acquisition mechanism methods --------
	
	/**
	 *  Get the supported certificate acquisition mechanism infos.
	 *  @return The available acquisition mechanisms.
	 */
	public IFuture<List<MechanismInfo>> getAcquisitionMechanisms();
	
	/**
	 *  Set a mechanism parameter.
	 *  @param type The mechanism identifier.
	 *  @param name The parameter name.
	 *  @param value The parameter value.
	 */
	public IFuture<Void> setAcquisitionMechanismParameterValue(Class<?> type, String name, Object value);

	/**
	 *  Set the acquisition mechanism.
	 *  @param type The acquisition mechanism class.
	 */
	public IFuture<Void> setAcquisitionMechanism(Class<?> type);

	/**
	 *  Get the active acquisition mechanism.
	 *  @return The number of the currently selected mechanism.
	 */
	public IFuture<Integer> getSelectedAcquisitionMechanism();
	
	//-------- subscription to changes --------
	
	/**
	 *  Subscribe to changes.
	 */
	@SecureTransmission // Sends configuration changes with passwords etc.
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<ChangeEvent<Object>> subscribeToEvents();
	
}
