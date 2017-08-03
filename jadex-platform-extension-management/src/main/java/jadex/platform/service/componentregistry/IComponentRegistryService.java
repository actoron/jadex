package jadex.platform.service.componentregistry;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;

@Service(system=true)
public interface IComponentRegistryService
{
    /**
     *  Add a new component type and a strategy.
     *  @param componentmodel The component model.
     *  @param rid The resource identifier.
     */
    public IFuture<Void> addComponentType(CreationInfo info);

    /**
     *  Remove a new component type and a strategy.
     *  @param filename The component model.
     */
    public IFuture<Void> removeComponentType(String filename);

} 