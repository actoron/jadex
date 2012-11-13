package jadex.wfms.simulation.stateset;

import jadex.wfms.parametertypes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DocumentStateSet extends AbstractParameterStateSet
{
	private static final int DEFAULT_RANDOM_DOCUMENT_SIZE = 65535;
	
	private Random random;
	
	private List documents;
	
	public DocumentStateSet(String parameterName)
	{
		this.name = parameterName;
		this.random = new Random();
		documents = new ArrayList();
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return Document.class;
	}
	
	/**
	 * Adds a randomized Document to the set.
	 */
	public void addRandom()
	{
		Document doc = new Document();
		byte[] content = new byte[DEFAULT_RANDOM_DOCUMENT_SIZE];
		random.nextBytes(content);
		doc.encodeContent(content);
		String baseName = "RandomDocument";
		doc.setFileName(baseName);
		long counter = 0;
		while (documents.contains(doc))
			doc.setFileName(baseName + String.valueOf(++counter));
		addDocument(doc);
	}
	
	/**
	 * Adds a new Document to the set.
	 * @param document the new Document
	 */
	public void addDocument(Document document)
	{
		if (!documents.contains(document))
		{
			documents.add(document);
			Collections.sort(documents);
			fireStateChange(document);
		}
	}
	
	/**
	 * Removes a Document from the set.
	 * @param document the Document
	 */
	public void removeDocument(Document document)
	{
		documents.remove(document);
		fireStateChange(document);
	}
	
	public List getDocuments()
	{
		return documents;
	}
	
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount()
	{
		return documents.size();
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return documents.get((int) index);
	}
}
