package jadex.micro.testcases.securityinheritance;

import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;

/**
 *  Base service interface with unrestricted security.
 */
@Service
@Security( roles = Security.UNRESTRICTED )
public interface IUnrestrictedBaseService
{

}
