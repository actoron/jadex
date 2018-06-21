package jadex.micro.testcases.authenticate;

import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IFuture;

/**
 *  Test service interface for authentication.
 */
@Security(roles=Security.UNRESTRICTED)	// always allow searching 
public interface ITestService
{
	/**
	 *  Test unrestricted access.
	 */
	@Security(roles=Security.UNRESTRICTED)
	public IFuture<Void> unrestrictedMethod();
	
	/**
	 *  Test default access.
	 */
	@Security
	public IFuture<Void> defaultMethod();
	
	/**
	 *  Test custom access.
	 */
	@Security(roles="custom")
	public IFuture<Void> customMethod();

	/**
	 *  Test custom access with multiple roles.
	 */
	@Security(roles={"custom1", "custom"})
	public IFuture<Void> custom1Method();
}
