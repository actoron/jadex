package com.daimler.client.connector;

import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

import java.util.List;
import java.util.Set;

public interface IClient
{
	//public void fetchData(String role, ITaskContext context, IResultListener listener);
	
	public Set getRoles();
}
