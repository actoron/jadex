package de.unihamburg.vsis.jadexAndroid_test;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

public class AndroidAgent extends MicroAgent {

	@Override
	public IFuture agentCreated() {
		// TODO Auto-generated method stub
		return super.agentCreated();
	}

	@Override
	public IFuture<Void> executeBody() {
		System.out.println(this.getAgentName() + ": execute Body");
		Context context = (Context) getArgument("context");
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("text", "Agent " + getAgentName() + ": execute Body");
		message.setData(bundle);
		AgentActivity.getHandler().sendMessage(message);
		return super.executeBody();
	}
}
