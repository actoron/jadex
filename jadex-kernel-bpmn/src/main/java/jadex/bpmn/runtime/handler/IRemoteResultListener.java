package jadex.bpmn.runtime.handler;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IResultListener;

@Reference
public interface IRemoteResultListener<E> extends IResultListener<E>
{

}
