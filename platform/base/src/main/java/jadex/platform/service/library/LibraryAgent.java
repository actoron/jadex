package jadex.platform.service.library;



import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Agent that provides the library service.
 */
@Agent(autostart=Boolean3.TRUE)
@Arguments({
	@Argument(name="libpath", clazz=String.class),
	@Argument(name="baseclassloader", clazz=ClassLoader.class),
	@Argument(name="maven_dependencies", clazz=boolean.class)
})
@ProvidedServices({
	@ProvidedService(type=ILibraryService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(expression="jadex.commons.SReflect.isAndroid() ? jadex.platform.service.library.AndroidLibraryService.class.newInstance() : $args.libpath==null? new jadex.platform.service.library.LibraryService(): new jadex.platform.service.library.LibraryService(new java.net.URLClassLoader(jadex.commons.SUtil.toURLs($args.libpath), $args.baseclassloader==null ? jadex.platform.service.library.LibraryService.class.getClassLoader() : $args.baseclassloader))")),
	@ProvidedService(type=IDependencyService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(expression="$args.maven_dependencies? jadex.platform.service.dependency.maven.MavenDependencyResolverService.class.newInstance(): new jadex.platform.service.library.BasicDependencyService()"))
})
//@Properties(value=@NameValue(name="system", value="true"))
public class LibraryAgent
{
}
