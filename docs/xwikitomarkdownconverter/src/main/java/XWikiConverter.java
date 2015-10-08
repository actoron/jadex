
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.xwiki.component.annotation.ComponentAnnotationLoader;
import org.xwiki.component.descriptor.ComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.rendering.converter.ConversionException;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;


public class XWikiConverter {
    private Converter	converter;

//	public static void main(String[] args) {
//        try
//		{
//			XWikiConverter myConverter = new XWikiConverter();
//			myConverter.convert(Paths.get("src/main/resources/Overview.txt"));
//		}
//		catch(ConversionException e)
//		{
//			e.printStackTrace();
//		}
//		catch(ComponentLookupException e)
//		{
//			e.printStackTrace();
//		}
//
//    }
//
//    private void convert(Path path)
//	{
//    	convert(path.toFile());
//	}

	public XWikiConverter() throws ConversionException, ComponentLookupException
	{
    	// Initialize Rendering components and allow getting instances
        EmbeddableComponentManager ecm = new EmbeddableComponentManager();
		ecm.initialize(this.getClass().getClassLoader());
        converter = ecm.getInstance(org.xwiki.rendering.converter.Converter.class);


		//Register my special custom macros
//		ComponentAnnotationLoader loader = new ComponentAnnotationLoader();
//		List<ComponentDescriptor> compDescriptors =
//				loader.getComponentsDescriptors(MySpecialMacro.class);
////		ecm.registerComponent(compDescriptors.get(0));

//		Converter converter = ecm.lookup(Converter.class);
	}
    
//    public File convert(File f) {
//    	List<String> lines = null;
//		try
//		{
//			lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
//		}
//		catch(IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//    	String xhtml = convertXwikiToXhtml(lines);
//    	System.out.println(xhtml);
//		return f;
//    }

	public String convert(String s) {
		DefaultWikiPrinter printer = new DefaultWikiPrinter(new StringBuffer());

		try {
			converter.convert(new StringReader(s), Syntax.XWIKI_2_1, Syntax.HTML_5_0, printer);
		} catch (ConversionException e) {
			e.printStackTrace();
		}
		return printer.toString();
	}

//	private String convertXwikiToXhtml(List<String> lines)
//	{
//		WikiPrinter printer = new DefaultWikiPrinter();
//
//		StringReader stringReader = new StringReader(lines.stream().reduce("", (s1, s2) -> s1+s2));
//		try
//		{
//			converter.convert(stringReader, Syntax.XWIKI_2_1, Syntax.HTML_5_0, printer);
//		}
//		catch(ConversionException e)
//		{
//			e.printStackTrace();
//		}
//		return printer.toString();
//	}
}