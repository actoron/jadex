package jadex.micro.testcases.visibility;

/**
 *  Minimal service implementation.
 */
public class MessageService implements IMessageService
{
	public void receiveMessage(String message)
	{
		System.out.println(message + " received");
	}
}
