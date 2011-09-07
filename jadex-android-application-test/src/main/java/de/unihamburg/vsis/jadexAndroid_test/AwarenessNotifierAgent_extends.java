package de.unihamburg.vsis.jadexAndroid_test;

import android.os.Bundle;
import android.os.Message;
import jadex.base.service.awareness.management.AwarenessManagementAgent;
import jadex.base.service.awareness.management.DiscoveryInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class AwarenessNotifierAgent_extends extends AwarenessManagementAgent {

	
	
	@Override
	public IFuture agentCreated() {
		Bundle data = new Bundle();
		data.putString("text", "Awareness Notifier Agent started.");
		Message message = AwarenessActivity.handler.obtainMessage();
		message.setData(data);
		message.sendToTarget();
		return super.agentCreated();
	}

	@Override
	public void executeBody() {
		super.executeBody();
	}

	@Override
	public IFuture createProxy(IComponentIdentifier cid) {
		Bundle data = new Bundle();
		data.putString("text", cid.getName() + " auf " + cid.getPlatformName());
		Message message = AwarenessActivity.handler.obtainMessage();
		message.setData(data);
		message.sendToTarget();
		return super.createProxy(cid);
	}

	@Override
	public IFuture deleteProxy(DiscoveryInfo dif) {
		return super.deleteProxy(dif);
	}
	
	
}
