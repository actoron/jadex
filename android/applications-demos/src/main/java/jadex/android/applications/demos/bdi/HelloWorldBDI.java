package jadex.android.applications.demos.bdi;

import android.widget.Toast;

import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;


@Agent(keepalive= Boolean3.FALSE)
public abstract class HelloWorldBDI implements IBDIAgent, IDisplayTextService
{
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		adoptPlan("printHello").get();
	}

	/**
	 *  Plan that prints out goal text and passes.
	 */
	@Plan
	protected void printMessage(ChangeEvent ev)
	{
		final String message = (String)((Object[])ev.getValue())[0];
		BDIDemoActivity.INSTANCE.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(BDIDemoActivity.INSTANCE, message, Toast.LENGTH_LONG).show();
			}
		});

	}

	/**
	 *  Say hello method.
	 */
	@Override
	public IFuture<Void> showUiMessage(String message)
	{
		adoptPlan("printMessage");

		return IFuture.DONE;
	}
}

