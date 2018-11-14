package org.activecomponents.udp.symciphers;

/** Implements NOP cipher, insecure since it is emitting plaintext. */
public class NullCipher implements ISymCipher
{
	/**
	 *  Creates the null cipher.
	 *  @param key Key, ignored.
	 *  @param nonce Nonce, ignored.
	 */
	public NullCipher(byte[] key, Nonce nonce)
	{
	}
	
	/**
	 *  Encrypts a message.
	 *  
	 *  @param plain Plain text.
	 *  @return Cipher text.
	 */
	public byte[] encrypt(byte[] plain)
	{
		return plain;
	}
	
	/**
	 *  Encrypts a message.
	 *  
	 *  @param plain Plain text.
	 *  @param offset Offset where the plain text starts.
	 *  @param len Length of the plain text, -1 for rest of the array.
	 *  @return Cipher text.
	 */
	public byte[] encrypt(byte[] plain, int offset, int len)
	{
		if (len < 0)
		len = plain.length - offset;
		byte[] ret = new byte[len];
		System.arraycopy(plain, offset, ret, 0, len);
		return ret;
	}
	
	/**
	 *  Decrypts a message.
	 *  
	 * 	@param cipher Cipher text.
	 * 	@return Plain text.
	 */
	public byte[] decrypt(byte[] cipher)
	{
		return cipher;
	}
	
	/**
	 *  Decrypts a message.
	 *  
	 * 	@param cipher Cipher text.
	 *  @param offset Offset where the cipher text starts.
	 *  @param len Length of the cipher text, -1 for rest of the array.
	 * 	@return Plain text.
	 */
	public byte[] decrypt(byte[] cipher, int offset, int len)
	{
		if (len < 0)
		len = cipher.length - offset;
		byte[] ret = new byte[len];
		System.arraycopy(cipher, offset, ret, 0, len);
		return ret;
	}
}
