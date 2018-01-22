package jadex.micro.examples.helplinemega;

import java.util.Set;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Basic interface for helpline.
 *  Allows to get local information about a person and
 *  add information about a person.
 *  The person's name for the service instance is annotated as a tag,
 *  derived from the component argument named 'person'.
 */
@Security(Security.UNRESTRICTED)
@NFProperties(@NFProperty(value=TagProperty.class, parameters=@NameValue(name=TagProperty.ARGUMENT, value="\"person\"")))
@Service
public interface IHelpline
{
	/**
	 *  Add new information about a person, e.g. from GUI.
	 *  @param info The information text.
	 */
	public IFuture<Void>	addInformation(String info);
	
	/**
	 *  Receive all locally stored information about a person.
	 *  @return Future that contains all currently known information in a set of records.
	 */
	public IFuture<Set<InformationEntry>>	getInformation();
	
	/**
	 *  Forward existing information to this service, e.g. from other helpline nodes (i.e. push).
	 *  @param entry The information record.
	 */
	public IFuture<Void>	forwardInformation(InformationEntry entry);

	/**
	 *  Search for information about a person in the network (i.e. pull).
	 *  The information is stored locally.
	 *  @return All information that can currently be found.
	 */
	public IIntermediateFuture<InformationEntry>	searchInformation();
}
