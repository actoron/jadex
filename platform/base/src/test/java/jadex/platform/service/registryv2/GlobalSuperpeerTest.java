package jadex.platform.service.registryv2;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.commons.security.PemKeyPair;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;

/**
 *  Test registry infrastructure with one global and one local super peer
 *  and no awareness at all.
 */
public class GlobalSuperpeerTest	extends AbstractSearchQueryTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	/** Local super peer platform configuration. */
	public static final IPlatformConfiguration	SPCONF;

	/** Global super peer and relay platform configuration. */
	public static final IPlatformConfiguration	RELAYCONF;
	
	/** Fixed port for SSP platform. */
	public static final int sspport	= SSecurity.getSecureRandom().nextInt(Short.MAX_VALUE*2-1024)+1025;

	static
	{
		PemKeyPair ca = SSecurity.createTestCACert();
		PemKeyPair cert = SSecurity.createTestCert(ca);
		AbstractAuthenticationSecret clientsecret = AbstractAuthenticationSecret.fromKeyPair(ca, true);
		AbstractAuthenticationSecret serversecret = AbstractAuthenticationSecret.fromKeyPair(cert, false, ca);
		
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig(GlobalSuperpeerTest.class)
			.setValue("superpeerclient.awaonly", false)
			.setValue("superpeerclient.contimeout", WAITFACTOR*2)
			.setValue("intravmawareness", false)
			.setValue("catalogawareness", true)
			.setValue("rt", true)
			.setValue("platformurls", "intravm://GlobalSuperpeerTestSSP@localhost:"+sspport)
			.setNetworkNames(SecurityAgent.GLOBAL_NETWORK_NAME, STest.testnetwork_name)
			.setNetworkSecrets(clientsecret.toString(), STest.testnetwork_pass)
			// Remote only -> no simulation please
			.getExtendedPlatformConfiguration()
				.setSimul(false)
				.setSimulation(false)
				.setDebugFutures(true)
//			.setValue("debugservices", true)
			.setValue("security.debug", true);
			
		CLIENTCONF	= baseconf.clone()
			.setPlatformName("GlobalSuperpeerTestClient_*");
//			.setLogging(true);
//			.setValue("rt.debug", true);
		
		PROCONF	= baseconf.clone()
			.addComponent(GlobalProviderAgent.class)
			.addComponent(NetworkProviderAgent.class)
			.addComponent(LocalProviderAgent.class)
			.setPlatformName("GlobalSuperpeerTestProvider_*");
		
		SPCONF	= baseconf.clone()
			.setValue("superpeer", true)
			.setPlatformName("GlobalSuperpeerTestSP_*");
//		SPCONF.setValue("rt.debug", true);
//		SPCONF.setLogging(true);
		
		RELAYCONF	= baseconf.clone()
			.setValue("superpeer", true)
			.setValue("supersuperpeer", true)
			.setValue("rt.forwarding", true)
			.setValue("intravm.port", sspport)
			.setPlatformName("GlobalSuperpeerTestSSP")
			.setNetworkNames(SecurityAgent.GLOBAL_NETWORK_NAME)
			.setNetworkSecrets(serversecret.toString());
//		RELAYCONF.setLogging(true);
//		RELAYCONF.setValue("rt.debug", true);
//		RELAYCONF.setValue("status", true);
//		RELAYCONF.setValue("jettyrspublish", true);
	}
	
	//-------- constructors --------
	
	/**
	 *  Create the test.
	 */
	public GlobalSuperpeerTest()
	{
		super(false, CLIENTCONF, PROCONF, SPCONF, RELAYCONF);
	}
	
	// debug test
	
	/**
	 *  Test if client can find local SP by using SSP.
	 */
//	@Test	// TODO: fix abstract transport create connection retry?
	public void testClientFirstConnection()
	{
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		IExternalAccess	relay	= createPlatform(RELAYCONF);
		waitForSuperpeerConnections(relay, client);
	}

	/**
	 *  Test if client can find local SP by using SSP.
	 */
	@Test
	public void testRelayFirstConnection()
	{
		IExternalAccess	relay	= createPlatform(RELAYCONF);
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		waitForSuperpeerConnections(relay, client);
	}
}

