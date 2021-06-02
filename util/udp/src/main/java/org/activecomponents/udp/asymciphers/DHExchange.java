package org.activecomponents.udp.asymciphers;

import org.activecomponents.udp.SUdpUtil;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.generators.DHParametersGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;

public class DHExchange extends AbstractKeyExchange
{
	/** Key size */
	protected static final int DEFAULT_KEY_SIZE = 4096;
	
	/** Generated key pair. */
	protected AsymmetricCipherKeyPair keypair;
	
	/** Key Agreement. */
	protected DHBasicAgreement agreement;
	
	public DHExchange()
	{
		DHKeyPairGenerator generator = new DHKeyPairGenerator();
		DHParametersGenerator dhparamgen = new DHParametersGenerator();
		dhparamgen.init(DEFAULT_KEY_SIZE, 128, SUdpUtil.getSecRandom());
		DHKeyGenerationParameters keyparams = new DHKeyGenerationParameters(SUdpUtil.getSecRandom(), dhparamgen.generateParameters());
		generator.init(keyparams);
		keypair = generator.generateKeyPair();
		agreement = new DHBasicAgreement();
		agreement.init(keypair.getPrivate());
	}
}
