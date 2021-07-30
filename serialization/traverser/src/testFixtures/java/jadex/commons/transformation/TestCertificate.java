package jadex.commons.transformation;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import jadex.commons.Base64;
import jadex.commons.SUtil;

/**
 *  Class containing the test certificate, avoids generating / dependencies on bouncy.
 */
public class TestCertificate
{
	/** Cert data */
	private static final byte[] CERT_DATA = Base64.decodeNoPadding("MIIBtTCCAR4CCQDN7HEHq5CYtzANBgkqhkiG9w0BAQQFADAfMR0wGwYDVQQDExRDS1MgU2VsZiBTaWduZWQgQ2VydDAeFw0xODA1MTUxNzIwMDJaFw0yMTAyMDgxNzIwMDJaMB8xHTAbBgNVBAMTFENLUyBTZWxmIFNpZ25lZCBDZXJ0MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzR4Acfl+pWUQC5vko+TiqCgh/y0RMfikai3AbpkqGqEc17bQwx65awKY2g7OQbVOyGRZnJsYmS2+CaM9ecwPD6qN6Ocxf8VE3PxxAqnlZkl42cnSZPTmZaHRGfn9JzU2gEBC7SiDcDnq6aRpPSKXAUFEvwhPt4HFPV86akD92xQIDAQABMA0GCSqGSIb3DQEBBAUAA4GBAG/Al696v0v7KeQU9dwKsU8ZO+3mZSq5m+1f92UKYwncfxkZlVDIHfgTB/0gltVCwNrnMBiq+KrSsz9O9U5+c/lj9X6dVoqNrEStHAbbqtUTZjMLnIAcSDrKuHxZRUBiWVDcuVHdRe1Zjtcw2HMZ+20o7PMMEtz0c5IWmIZ7xoip".getBytes(SUtil.ASCII));
	
	public static final Certificate getCert()
	{
		try { return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(CERT_DATA)); }
		catch (CertificateException e) { throw SUtil.throwUnchecked(e); }
	}
}
