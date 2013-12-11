
package jadex.xml.tutorial.jibx.example22;


//import org.jibx.extras.DocumentComparator;
//import org.jibx.runtime.*;


/**
 * Test program for the JiBX framework. Works with two or three command line
 * arguments: mapped-class, in-file, and out-file (optional, only needed if
 * different from in-file). You can also supply a multiple of three input
 * arguments, in which case each set of three is processed in turn (in this case
 * the out-file is required). Unmarshals documents from files using the binding
 * defined for the mapped class, then marshals them back out using the same
 * bindings and compares the results. In case of a comparison error the output
 * file is left as <i>temp.xml</i>.
 * 
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class Test {
    
//    // definitions for version attribute on document root element
//	@SuppressWarnings("unused")
//	private static final String VERSION_URI = null;
//	@SuppressWarnings("unused")
//	private static final String VERSION_NAME = "version";
//    
//    // attribute text strings used for different document versions
//	@SuppressWarnings("unused")
//	private static String[] VERSION_TEXTS = {
//        "1.0", "1.1", "1.2"
//    };
//    
//    // binding names corresponding to text strings
//	@SuppressWarnings("unused")
//	private static String[] VERSION_BINDINGS = {
//        "binding0", "binding1", "binding2"
//    };
//
//    public static void main(String[] args) {
//        if (args.length == 1) {
//            
//            // delete generated output file if present
//            File temp = new File("temp.xml");
//            if (temp.exists()) {
//                temp.delete();
//            }
//            try {
//                
//                // process input file according to declared version
//                BindingSelector select = new BindingSelector(VERSION_URI,
//                    VERSION_NAME, VERSION_TEXTS, VERSION_BINDINGS);
//                IUnmarshallingContext context = select.getContext();
//                context.setDocument(new FileInputStream(args[0]), null);
//                Customer customer = (Customer)select.
//                    unmarshalVersioned(Customer.class);
//                
//                // now marshal to in-memory array with same document version
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                select.setOutput(bos, "UTF-8");
//                select.marshalVersioned(customer, customer.version);
//                
//                // run comparison of output with original document
//                InputStreamReader brdr = new InputStreamReader
//                    (new ByteArrayInputStream(bos.toByteArray()), "UTF-8");
//                FileReader frdr = new FileReader(args[0]);
//                FileOutputStream fos = new FileOutputStream("temp.xml");
//                fos.write(bos.toByteArray());
//                fos.close();
//                DocumentComparator comp = new DocumentComparator(System.err);
//                if (!comp.compare(frdr, brdr)) {
//                    
//                    // report mismatch with output saved to file
////                    FileOutputStream fos = new FileOutputStream("temp.xml");
////                    fos.write(bos.toByteArray());
////                    fos.close();
//                    System.err.println("Error testing on input file " + args[0]);
//                    System.err.println("Saved output document file path " +
//                        temp.getAbsolutePath());
//                    System.exit(1);
//                }
//                
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.exit(1);
//            }
//            
//        } else {
//            System.err.println("Usage: java exampl22.Test in-file\n" +
//                "Leaves output as temp.xml in case of error");
//            System.exit(1);
//        }
//    }
}

