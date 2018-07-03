/**
 * 
 */
package org.activecomponents.udp.asymciphers;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/**
 *  Interface for a public key cipher.
 *
 */
public interface IAsymCipher
{
	/**
	 *  Sets the public key of the communication partner.
	 * 
	 * 	@param foreignpublic The public key.
	 * 	@return False, if the key was invalid.
	 */
	public boolean setForeign(byte[] foreignpublic);
	
	/**
	 *  Encrypts a message using a public key.
	 *  
	 * 	@param plain The plain text.
	 *  @param foreign True, if the foreign public key should be used,
	 *  			   false, if the innate one should be used.
	 * @return The cipher text.
	 */
//	public byte[] encrypt(byte[] plain, boolean foreign);
	
	/**
	 *  Decrypts a message.
	 * 
	 *  @param ciphertext The cipher text.
	 * 	@return The plain text or null if decryption failed.
	 */
//	public byte[] decrypt(byte[] ciphertext);
	
	/**
	 *  Signs a message.
	 * 
	 *  @param msg The message.
	 *  @return The message with signature.
	 */
//	public byte[] sign(byte[] msg);
	
	/**
	 *  Verifies a message.
	 * 
	 *  @param msg The message with signature.
	 *  @param foreign True, if the foreign public key should be used,
	 *  			   false, if the innate one should be used.
	 *  @return The message or null if the verification failed.
	 */
//	public byte[] verify(byte[] msgwithsig, boolean foreign);
	
	/**
	 *  Signs a message and encrypts a message.
	 * 
	 *  @param msg The message.
	 *  @param encryptwithforeign True, if the foreign public key should be used for encryption,
	 *  						  false, if the innate one should be used.
	 *  @return The message with signature, encrypted.
	 */
	public byte[] signAndEncrypt(byte[] msg, boolean encryptwithforeign);
	
	/**
	 *  Decrypts a message and verifies the signature.
	 * 
	 *  @param msg The signed and encrypted message.
	 *  @param encryptwithforeign True, if the foreign public key should be used for verification,
	 *  						  false, if the innate one should be used.
	 *  @return The message, null if verification failed.
	 */
	public byte[] decryptAndVerify(byte[] ciphertext, boolean verifywithforeign);
	
	/**
	 *  Converts a key to binary form.
	 * 
	 * 	@param key The key.
	 * 	@return The binary form of the key.
	 */
	public byte[] keyToByteArray(AsymmetricKeyParameter key);
	
	/**
	 *  Converts a key from binary form.
	 * 
	 * 	@param enckey The encoded key.
	 * 	@return The key.
	 */
	public AsymmetricKeyParameter byteArrayToKey(byte[] enckey);
}
