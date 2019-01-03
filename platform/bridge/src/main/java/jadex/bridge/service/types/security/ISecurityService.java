package jadex.bridge.service.types.security;

import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.JadexVersion;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.IFuture;

/**
 *  The security service is responsible for
 *  validating (remote) requests.
 *  Currently only platform level authentication
 *  is provided. More fine grained control on
 *  service/method level based on user/group
 *  access rights is planned for the mid-term future.
 */
//Safe to be allowed remotely, as it can only be called, when platform access is granted.
//Putting method in service allows security settings to be administered using remote JCCs.
@GuiClassNames({
	@GuiClassName("jadex.tools.security.SecuritySettingsPanel"),
	@GuiClassName("jadex.android.controlcenter.settings.SecuritySettings")
})
@Service(system=true)
public interface ISecurityService
{
	//-------- message-level encryption/authentication -------
	
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param receiver The receiver.
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public IFuture<byte[]> encryptAndSign(IMsgHeader header, byte[] content);
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param sender The sender.
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public IFuture<Tuple2<ISecurityInfo,byte[]>> decryptAndAuth(IComponentIdentifier sender, byte[] content);
	
	/**
	 *  Checks if platform secret is used.
	 *  
	 *  @return True, if so.
	 */
	public IFuture<Boolean> isUsePlatformSecret();
	
	/**
	 *  Sets whether the platform secret should be used.
	 *  
	 *  @param useplatformsecret The flag.
	 *  @return Null, when done.
	 */
	public IFuture<Void> setUsePlatformSecret(boolean useplatformsecret);
	
	/**
	 *  Checks if platform secret is printed.
	 *  
	 *  @return True, if so.
	 */
	public IFuture<Boolean> isPrintPlatformSecret();
	
	/**
	 *  Sets whether the platform secret should be printed.
	 *  
	 *  @param printplatformsecret The flag.
	 *  @return Null, when done.
	 */
	public IFuture<Void> setPrintPlatformSecret(boolean printplatformsecret);
	
	/**
	 *  Sets a new network.
	 * 
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove.
	 *  @return Null, when done.
	 */
	public IFuture<Void> setNetwork(String networkname, String secret);
	
	/**
	 *  Remove a network.
	 * 
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove the network completely.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeNetwork(String networkname, String secret);
	
	/**
	 *  Gets the current known networks and secrets. 
	 *  
	 *  @return The current networks and secrets.
	 */
	public IFuture<MultiCollection<String, String>> getAllKnownNetworks();
	
	/** 
	 *  Adds an authority for authenticating platform names.
	 *  
	 *  @param secret The secret, only X.509 secrets allowed.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addNameAuthority(String cert);
	
	/** 
	 *  Remvoes an authority for authenticating platform names.
	 *  
	 *  @param secret The secret, only X.509 secrets allowed.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeNameAuthority(String cert);
	
	/** 
	 *  Adds a name of an authenticated platform to allow access.
	 *  
	 *  @param name The platform name, name must be authenticated with certificate.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addTrustedPlatform(String name);
	
	/** 
	 *  Adds a name of an authenticated platform to allow access.
	 *  
	 *  @param name The platform name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeTrustedPlatform(String name);
	
	/**
	 *  Gets the trusted platforms that are specified by names. 
	 *  @return The trusted platforms and their roles.
	 */
	public IFuture<Set<String>> getTrustedPlatforms();
	
	/** 
	 *  Gets all authorities for authenticating platform names.
	 *  
	 *  @return List of all name authorities.
	 */
	public IFuture<Set<String>> getNameAuthorities();
	
	/** 
	 *  Gets all authorities not defined in the Java trust store for authenticating platform names.
	 *  
	 *  @return List of name authorities.
	 */
	public IFuture<Set<String>> getCustomNameAuthorities();
	
	/**
	 *  Gets the secret of a platform if available.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Encoded secret or null.
	 */
	public IFuture<String> getPlatformSecret(IComponentIdentifier cid);
	
	/**
	 *  Sets the secret of a platform.
	 * 
	 *  @param cid ID of the platform.
	 *  @param secret Encoded secret or null to remove.
	 */
	public IFuture<Void> setPlatformSecret(IComponentIdentifier cid, String secret);
	
	/**
	 *  Adds a role for an entity (platform or network name).
	 *  
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addRole(String entity, String role);
	
	/**
	 *  Adds a role of an entity (platform or network name).
	 *  
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeRole(String entity, String role);
	
	/**
	 *  Gets a copy of the current role map.
	 *  
	 *  @return Copy of the role map.
	 */
	public IFuture<Map<String, Set<String>>> getRoleMap();
	
	/**
	 *  Gets the current network names. 
	 *  @return The current networks names.
	 */
	public IFuture<Set<String>> getNetworkNames();
	
	/**
	 *  Opportunistically returns the remote Jadex version if known.
	 *  
	 *  @param remoteid ID of the remote platform.
	 *  @return Null, if the version is cannot be determined, a JadexVersion otherwise.
	 *  		Note that the JadexVersion can still be an unknown version (as determined by isUnknown),
	 *  		which means that the platform itself reported an unknown version.
	 */
	public JadexVersion getJadexVersion(IComponentIdentifier remoteid);
	
//	/**
//	 *  Gets the current network names. 
//	 *  @return The current networks names.
//	 */
//	@Excluded
//	public Set<String> getNetworkNamesSync();
}
