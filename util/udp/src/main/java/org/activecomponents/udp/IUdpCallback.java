package org.activecomponents.udp;

/** Callback interface. */
public interface IUdpCallback<T>
{
	/** Called when the callback results are available. */
	public void resultAvailable(T result);
}
