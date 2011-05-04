/**
 * 
 */
package haw.mmlab.production_line.common;

import jadex.bridge.ComponentIdentifier;

import java.io.Serializable;

/**
 * The Container for the console messages.
 * 
 * @author Peter
 * 
 */
public class ConsoleMessage implements Serializable {
	private static final long serialVersionUID = 3920005705769684151L;
	public static final int TYPE_PRODLINE = 0;
	public static final int TYPE_ADAPTIVITY = 1;

	private String outmsg = null;
	private int type = 0;
	private long sendTime = 0;
	private long arrivalTime = 0;
	private ComponentIdentifier sender = null;

	public ConsoleMessage() {
	}

	public ConsoleMessage(int type) {
		this.type = type;
	}

	public ConsoleMessage(int type, String outMsg) {
		this.type = type;
		this.outmsg = outMsg;
	}

	public String getOutMsg() {
		return outmsg.toString();
	}

	public void setOutMsg(String outmsg) {
		this.outmsg = outmsg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public ComponentIdentifier getSender() {
		return sender;
	}

	public void setSender(ComponentIdentifier sender) {
		this.sender = sender;
	}

	public boolean isProdLineMessage() {
		return type == TYPE_PRODLINE;
	}

	public boolean isAdaptivityMessage() {
		return type != TYPE_PRODLINE;
	}

	@Override
	public String toString() {
		return getOutMsg();
	}
}
