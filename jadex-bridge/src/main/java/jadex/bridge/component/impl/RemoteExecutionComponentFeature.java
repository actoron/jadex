package jadex.bridge.component.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IRemoteExecutionFeature;
import jadex.bridge.service.types.security.IMsgSecurityInfos;

/**
 *  Feature for securely sending and handling remote execution commands.
 */
public class RemoteExecutionComponentFeature extends AbstractComponentFeature implements IRemoteExecutionFeature
{
	/** Commands safe to use with untrusted clients. */
	protected static final Set<Class<?>> safecommands	= Collections.unmodifiableSet(new HashSet<Class<?>>()
	{{
		
	}});
	
	/**
	 *  Create the feature.
	 */
	public RemoteExecutionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		component.getComponentFeature(IMessageFeature.class).addMessageHandler(new RxHandler());
	}
	
	
	protected class RxHandler implements IMessageHandler
	{
		/**
		 *  Test if handler should handle a message.
		 *  @return True if it should handle the message. 
		 */
		public boolean isHandling(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
//			((MsgHeader) header)
			return false;
		}
		
		/**
		 *  Test if handler should be removed.
		 *  @return True if it should be removed. 
		 */
		public boolean isRemove()
		{
			return false;
		}
		
		/**
		 *  Handle the message.
		 *  @param header The header.
		 *  @param msg The message.
		 */
		public void handleMessage(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
		}
	}
}
