package jadex.commons.security.random;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class OpenSslAesCtr
{
	/** OpenSSL Crypto lib name. */
	public static final String JNA_LIBRARY_NAME = "crypto";
	
	/** OpenSSL Crypto lib. */
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(OpenSslAesCtr.JNA_LIBRARY_NAME);
	
	public static native void ERR_load_crypto_strings();
	public static native void OpenSSL_add_all_ciphers();
	public static native void OPENSSL_config(String config_name);
	public static native Pointer EVP_CIPHER_CTX_new();
	public static native void EVP_CIPHER_CTX_free(Pointer ctx);
	public static native int EVP_CIPHER_CTX_set_padding(Pointer context, int padding);
	public static native Pointer EVP_aes_256_ctr();
	public static native Pointer EVP_aes_128_ctr();
	public static native Pointer EVP_aes_256_ecb();
	public static native int EVP_EncryptInit_ex(Pointer context, Pointer ciphertype, Pointer impl, byte[] key, byte[] iv);
	public static native int EVP_EncryptUpdate(Pointer context, byte[] out, IntByReference outlen, byte[] in, int inlen);
	public static native int EVP_EncryptFinal_ex(Pointer ctx, byte[] out, IntByReference outlen);
	public static native void ERR_print_errors_fp(Pointer fp);
	
	/** Flag if the library is found and usable. */
	private static volatile boolean ENABLED = false;
	
	static
	{
		try
		{
			Native.register(OpenSslAesCtr.JNA_LIBRARY_NAME);
			ERR_load_crypto_strings();
			OpenSSL_add_all_ciphers();
			OPENSSL_config(null);
			
			ENABLED=true;
		}
		catch (Throwable e)
		{
		}
	}
	
	/** Empty static input buffer as source */
	protected static final byte[] BUFFER_INPUT = new byte[1024];
	
	/** Buffer size as C reference. */
	protected static final IntByReference BUFFER_SIZE_P = new IntByReference(BUFFER_INPUT.length);
	
	/** Checks if the library is in a usable state. */
	public static final boolean isEnabled()
	{
		return ENABLED;
	}
	
	/** Current cipher context. */
	protected Pointer evpCipherContext;
	
	/** Create an instance. */
	public OpenSslAesCtr()
	{
		if (!isEnabled())
			throw new IllegalStateException("OpenSSL not found.");
		
	}
	
	/**
	 *  Initialize the cipher.
	 *  
	 *  @param key The key.
	 *  @param iv The IV to use.
	 */
	public void init(byte[] key, byte[] iv)
	{
		if (evpCipherContext != null)
		{
			EVP_CIPHER_CTX_free(evpCipherContext);
			evpCipherContext = null;
		}
		evpCipherContext = EVP_CIPHER_CTX_new();
		EVP_EncryptInit_ex(evpCipherContext, key.length == 32 ? EVP_aes_256_ctr() : EVP_aes_128_ctr(), Pointer.NULL, key, iv);
		EVP_CIPHER_CTX_set_padding(evpCipherContext, 0);
	}
	
	/**
	 *  Retrieves the next buffer of random values.
	 *  
	 *  @return The next buffer of random values.
	 */
	public byte[] nextBytes()
	{
		byte[] ret = new byte[BUFFER_INPUT.length];
		EVP_EncryptUpdate(evpCipherContext, ret, BUFFER_SIZE_P, BUFFER_INPUT, BUFFER_INPUT.length);
		EVP_EncryptFinal_ex(evpCipherContext, ret, BUFFER_SIZE_P);
		return ret;
	}
}
