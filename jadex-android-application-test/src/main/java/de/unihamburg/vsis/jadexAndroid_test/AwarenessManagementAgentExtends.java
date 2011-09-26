package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.service.awareness.management.DiscoveryInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import android.os.Bundle;
import android.os.Message;
import de.unihamburg.vsis.jadexAndroid_test.AwarenessActivity;
import de.unihamburg.vsis.jadexAndroid_test.AwarenessManagementAgentBase;
import de.unihamburg.vsis.jadexAndroid_test.RemoteComponentIdentifier;

public class AwarenessManagementAgentExtends extends AwarenessManagementAgentBase {

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
	public IFuture<IComponentIdentifier> createProxy(IComponentIdentifier cid) {
		Bundle data = new Bundle();
		data.putString("text", cid.getName() + " auf " + cid.getPlatformName());
		data.putSerializable("identifier", new RemoteComponentIdentifier(cid));
		data.putString("method", "add");
		Message message = AwarenessActivity.handler.obtainMessage();
		message.setData(data);
		message.sendToTarget();
		return super.createProxy(cid);
	}

	@Override
	public IFuture<Void> deleteProxy(DiscoveryInfo dif) {
		Bundle data = new Bundle();
		data.putString("text",
				dif.cid.getName() + " auf " + dif.cid.getPlatformName()
						+ " nicht mehr da");
		data.putSerializable("identifier", new RemoteComponentIdentifier(
				dif.cid));
		data.putString("method", "remove");
		Message message = AwarenessActivity.handler.obtainMessage();
		message.setData(data);
		message.sendToTarget();
		return super.deleteProxy(dif);
	}

}
