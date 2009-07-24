package com.daimler.client.connector;

public interface IRequestListener
{
	public void startedRequest(ClientRequest request);
	
	public void finishedRequest(ClientRequest request);
}
