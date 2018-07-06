/**
 * 
 */
package org.activecomponents.udp.symciphers;

/**
 * @author jander
 *
 */
public interface ISymCipher
{
	/**
	 *  Encrypts a message.
	 *  
	 *  @param plain Plain text.
	 *  @return Cipher text.
	 */
	public byte[] encrypt(byte[] plain);
	
	/**
	 *  Encrypts a message.
	 *  
	 *  @param plain Plain text.
	 *  @param offset Offset where the plain text starts.
	 *  @param len Length of the plain text, -1 for rest of the array.
	 *  @return Cipher text.
	 */
	public byte[] encrypt(byte[] plain, int offset, int len);
	
	/**
	 *  Decrypts a message.
	 *  
	 * 	@param cipher Cipher text.
	 * 	@return Plain text.
	 */
	public byte[] decrypt(byte[] cipher);
	
	/**
	 *  Decrypts a message.
	 *  
	 * 	@param cipher Cipher text.
	 *  @param offset Offset where the cipher text starts.
	 *  @param len Length of the cipher text, -1 for rest of the array.
	 * 	@return Plain text.
	 */
	public byte[] decrypt(byte[] cipher, int offset, int len);
}
