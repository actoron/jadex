package jadex.android.application.demo;

import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import android.os.Bundle;
import android.os.Message;

public class AndroidAgent extends MicroAgent {

	@Override
	public IFuture agentCreated() {
		// TODO Auto-generated method stub
		return super.agentCreated();
	}

	@Override
	public void executeBody() {
		super.executeBody();
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("text","This is Agent <<" + this.getAgentName() + ">> saying hello!");
		message.setData(bundle);
		JadexAndroidHelloWorldActivity.getHandler().sendMessage(message);
	}
}
