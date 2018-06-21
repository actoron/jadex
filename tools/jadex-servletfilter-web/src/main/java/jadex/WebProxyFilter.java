package jadex;


import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class WebProxyFilter extends ForwardFilter
{
	/**
	 * 
	 */
	protected void sendDisplayMappings(HttpServletRequest request, HttpServletResponse response) 
	{
		request.setAttribute("forwardinfos", getForwardInfos());
		String lt = getLeasetime()==0? "0": ""+(getLeasetime()/1000/60);
		request.setAttribute("leasetime", lt);
		sendX(request, response, "mappings.jsp");
	}
	
	/**
	 * 
	 */
	protected void sendLoginPage(HttpServletRequest request, HttpServletResponse response, String next)
	{
		sendX(request, response, "login.jsp");
	}
	
	
	/**
	 * 
	 */
	protected void sendDisplayUsers(HttpServletRequest request, HttpServletResponse response)
	{
		request.setAttribute("users", getUsers());
		sendX(request, response, "users.jsp");
	}
	
	/**
	 * 
	 */
	protected void sendDisplayInfo(HttpServletRequest request, HttpServletResponse response)
	{
		sendX(request, response, "main.jsp");
	}
	
	/**
	 * 
	 */
	protected void sendX(HttpServletRequest request, HttpServletResponse response, String view)
	{
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher("/jsp/"+view);
			rd.forward(request, response);
		}
		catch(Exception e)
		{
			try
		    {
		    	response.sendError(500, "Exception occurred: "+e.getMessage());
		    }
		    catch(Exception ex)
		    {
		    	// ignore
		    }
		}
	}
}
