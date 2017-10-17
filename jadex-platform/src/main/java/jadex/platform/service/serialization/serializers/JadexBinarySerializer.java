package jadex.platform.service.serialization.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.SUtil;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.binaryserializer.SBinarySerializer;
import jadex.commons.transformation.binaryserializer.SerializationConfig;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  The Jadex Binary serializer. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexBinarySerializer implements ISerializer
{
	//-------- constants --------
	
	/** The JadexBinary serializer id. */
	public static final int SERIALIZER_ID = 0;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	protected static SerializationConfig CONFIG = new SerializationConfig(new String[] {
		"doSendMessage",
		"MapCodec.java",
		"logging.level",
		"decoupled",
		"getServiceProvider",
		"arguments",
		"notifyListener",
		"getServiceContainer",
		"linkToLinkMap",
		"endToEndMap",
		"realReceiver",
		"ThreadPool.java",
		"providerId",
		"java.util.logging.Level.SEVERE",
		"runPreProcessors",
		"callId",
		"excludedMethods",
		"getProxyReference",
		"addResultListener",
		"nonFunctionalProperties",
		"encode",
		"invoke",
		"targetInterfaces",
		"sender",
		"writeObjectToStream",
		"writeBeanProperties",
		"content",
		"addCachedMethodValue",
		"methodReplacements",
		"execute",
		"Future.java",
		"SBinarySerializer.java",
		"x_nonfunctional",
		"remoteReference",
		"java.lang.reflect.Method",
		"providedServices",
		"targetIdentifier",
		"global",
		"requiredServices",
		"timestamp",
		"scope",
		"AsyncExecutionService.java",
		"Executor.java",
		"JadexBinarySerializer.java",
		"jadex.bridge.service.types.execution.IExecutionService",
		"componentviewer.viewerclass",
		"BeanCodec.java",
		"jadex.bridge.service.types.threadpool.IThreadPoolService",
		"process",
		"jadex.platform.service.df.DirectoryFacilitatorAgent.class",
		"jadex.bridge.service.types.persistence.IPersistenceService",
		"jadex.platform.service.address.TransportAddressAgent.class",
		"jadex.platform.service.filetransfer.FileTransferAgent.class",
		"doTraverse",
		"contentData",
		"x_timestamp",
		"className",
		"jadex.bridge.service.types.threadpool.IDaemonThreadPoolService",
		"proxytype",
		"MapSendTask.java",
		"jadex.platform.service.remote.RemoteServiceManagementAgent.class",
		"jadex.platform.service.dht.DistributedServiceRegistryAgent.class",
		"FALSE",
		"__securitymessage__",
		"x_message_id",
		"java.lang.Boolean",
		"RemoteServiceManagementService.java",
		"digestContent",
		"jadex.platform.service.awareness.management.AwarenessManagementAgent.class",
		"receiver",
		"conversation_id",
		"implementation",
		"validityDuration",
		"type",
		"jadex.commons.concurrent.ThreadPool$ServiceThread",
		"remoteManagementServiceIdentifier",
		"authenticationData",
		"TRUE",
		"RemoteReferenceModule.java",
		"jadex.commons.transformation.binaryserializer.MapCodec",
		"jadex.commons.future.Future",
		"jadex.platform.service.execution.AsyncExecutionService$1",
		"Traverser.java",
		"MessageService.java",
		"jadex.platform.service.message.MessageService$SendManager",
		"NativeMethodAccessorImpl.java",
		"resultAvailable",
		"parameterTypeInfos",
		"jadex.commons.transformation.binaryserializer.SBinarySerializer",
		"AbstractSendTask.java",
		"jadex.platform.service.remote.RemoteServiceManagementService$15",
		"addresses",
		"successors",
		"jadex.platform.service.message.transport.niotcpmtp.NIOTCPTransport",
		"jadex.commons.concurrent.Executor",
		"lookupTypes",
		"AbstractCodec.java",
		"sun.reflect.NativeMethodAccessorImpl",
		"jadex.bridge.service.types.cms.IComponentManagementService",
		"jadex.platform.service.message.transport.serializers.JadexBinarySerializer",
		"predecessors",
		"jadex.platform.service.message.MapSendTask",
		"NIOTCPTransport.java",
		"jadex.platform.service.remote.RemoteReferenceModule",
		"jadex.commons.transformation.binaryserializer.BeanCodec",
		"java.lang.String",
		"jadex.platform.service.message.MessageService$SendManager$1",
		"java.lang.Object",
		"implementationClass",
		"filename",
		"jadex.platform.service.message.transport.niotcpmtp.NIOTCPTransport$2",
		"jadex.platform.service.message.transport.niotcpmtp.NIOTCPTransport$2$1",
		"boolean",
		"jadex.platform.service.message.AbstractSendTask",
		"value",
		"clazz",
		"jadex.commons.transformation.traverser.Traverser",
		"jadex.commons.transformation.binaryserializer.AbstractCodec",
		"name",
		"typeName",
		"description"
	},
	new String[] {
		"0",
		"jadex.bridge.service.IService",
		"jadex.bridge.component.IMessageFeature",
		"java.util.HashSet",
		"jadex.commons.Tuple2",
		"jadex.bridge.component.impl.MsgHeader",
		"jadex.bridge.component.IExecutionFeature",
		"jadex.bridge.component.IPropertiesFeature",
		"jadex.bridge.modelinfo.UnparsedExpression",
		"jadex.bridge.modelinfo.ConfigurationInfo[]",
		"jadex.bridge.service.ProvidedServiceInfo[]",
		"jadex.bridge.service.RequiredServiceInfo[]",
		"jadex.micro.features.IMicroInjectionFeature",
		"jadex.bridge.modelinfo.UnparsedExpression[]",
		"jadex.bridge.service.RequiredServiceBinding",
		"jadex.bridge.component.ISubcomponentsFeature",
		"jadex.bridge.component.IPojoComponentFeature",
		"jadex.bridge.modelinfo.SubcomponentTypeInfo[]",
		"jadex.bridge.component.IArgumentsResultsFeature",
		"jadex.base.service.remote.commands.RemoteMethodInvocationCommand",
		"jadex.bridge.component.IComponentFeatureFactory[]",
		"jadex.bridge.component.ILifecycleComponentFeature",
		"jadex.bridge.service.ProvidedServiceImplementation",
		"jadex.bridge.component.IMonitoringComponentFeature",
		"jadex.micro.features.IMicroServiceInjectionFeature",
		"jadex.bridge.component.INFPropertyComponentFeature",
		"java.lang.Class[]",
		"jadex.micro.features.impl.MicroPojoComponentFeature",
		"jadex.bridge.component.impl.ComponentFeatureFactory",
		"1",
		"jadex.bridge.component.impl.ExecutionComponentFeature",
		"jadex.base.service.remote.commands.RemoteSearchCommand",
		"jadex.bridge.component.impl.PropertiesComponentFeature",
		"jadex.bridge.component.impl.NFPropertyComponentFeature",
		"jadex.bridge.component.impl.MonitoringComponentFeature",
		"jadex.bridge.ClassInfo[]",
		"jadex.micro.features.impl.MicroMessageComponentFeature",
		"jadex.commons.MethodInfo",
		"jadex.bridge.service.component.IRequiredServicesFeature",
		"jadex.bridge.service.component.IProvidedServicesFeature",
		"jadex.micro.features.impl.MicroInjectionComponentFeature",
		"jadex.micro.features.impl.MicroLifecycleComponentFeature",
		"jadex.bridge.component.impl.SubcomponentsComponentFeature",
		"jadex.bridge.component.impl.ArgumentsResultsComponentFeature",
		"jadex.micro.features.impl.MicroServiceInjectionComponentFeature",
		"jadex.bridge.service.component.ProvidedServicesComponentFeature",
		"jadex.bridge.service.component.RequiredServicesComponentFeature",
		"byte[]",
		"jadex.base.service.remote.commands.RemoteResultCommand",
		"jadex.bridge.service.ServiceIdentifier",
		"java.lang.Long",
//		"jadex.base.service.remote.ProxyInfo",
		"jadex.bridge.component.impl.remotecommands.ProxyInfo",
		"java.lang.Boolean",
		"jadex.bridge.ClassInfo",
		"jadex.base.service.remote.ProxyReference",
		"java.util.HashMap",
		"java.lang.String[]",
		"java.util.ArrayList",
		"jadex.base.service.remote.RemoteReference",
		"jadex.base.service.remote.commands.RemoteIntermediateResultCommand",
		"jadex.base.service.remote.replacements.DefaultEqualsMethodReplacement",
		"jadex.base.service.remote.replacements.DefaultHashcodeMethodReplacement",
		"jadex.bridge.ComponentIdentifier",
		"java.lang.String",
		"jadex.bridge.IComponentIdentifier[]",
		"jadex.platform.service.message.transport.MessageEnvelope",
		"jadex.bridge.BasicComponentIdentifier"
	});
	
	
	//-------- methods --------
	
	/**
	 *  Get the serializer id.
	 *  @return The serializer id.
	 */
	public int getSerializerId()
	{
		return SERIALIZER_ID;
	}
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs, Object usercontext)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SBinarySerializer.writeObjectToStream(baos, val, preprocs!=null?Arrays.asList(preprocs):null, null, usercontext, classloader, CONFIG);
		
		byte[] ret = baos.toByteArray();
		
		if(DEBUG)
		{
			System.out.println("encode message: "+(new String(ret, SUtil.UTF8)));
		}
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		if(DEBUG)
		{
			System.out.println("decode message: "+(new String((byte[])bytes, SUtil.UTF8)));
		}
		
		InputStream is = new ByteArrayInputStream((byte[]) bytes);
		
		return decode(is, classloader, postprocs, rep, usercontext);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep)
	{
		return decode(is, classloader, postprocs, rep, null);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		
		Object ret = SBinarySerializer.readObjectFromStream(is, postprocs!=null?Arrays.asList(postprocs):null, usercontext, classloader, null, CONFIG);
		
		try
		{
			is.close();
		}
		catch (IOException e)
		{
		}
		
		
		return ret;
	}
}