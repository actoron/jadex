package deco4mas.distributed.util.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * A utility class that is required for (JAXB) schema serialization.
 * 
 * @author Jan Sudeikat
 *
 */
class MySchemaOutputResolver extends SchemaOutputResolver {
    
	public Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException {
		System.out.println("Schema for: " + namespaceUri + "is exported to: " + suggestedFileName);
		return new StreamResult(new File(suggestedFileName));
    }
	
}
