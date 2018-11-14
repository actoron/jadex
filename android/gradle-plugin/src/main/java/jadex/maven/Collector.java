package jadex.maven;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class Collector extends Remapper{

    public static class EmptyVisitor extends ClassVisitor
	{

		public EmptyVisitor()
		{
			super(Opcodes.ASM4);
		}
	}

	private final Set<Class<?>> classNames;
    private final String prefix;

    public Collector(final Set<Class<?>> classNames, final String prefix){
        this.classNames = classNames;
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String mapDesc(final String desc){
        if(desc.startsWith("L")){
            this.addType(desc.substring(1, desc.length() - 1));
        }
        return super.mapDesc(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] mapTypes(final String[] types){
        for(final String type : types){
            this.addType(type);
        }
        return super.mapTypes(types);
    }

    private void addType(final String type){
        final String className = type.replace('/', '.');
        if(className.startsWith(this.prefix)){
            try{
                this.classNames.add(Class.forName(className));
            } catch(final ClassNotFoundException e){
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public String mapType(final String type){
        this.addType(type);
        return type;
    }
    
    public static Set<Class<?>> getClassesUsedBy(
    		final InputStream input,   // class name
    		final String prefix  // common prefix for all classes
    		// that will be retrieved
    		) throws IOException{
    	final ClassReader reader = new ClassReader(input);
    	final Set<Class<?>> classes =
    			new TreeSet<Class<?>>(new Comparator<Class<?>>(){
    				
    				@Override
    				public int compare(final Class<?> o1, final Class<?> o2){
    					return o1.getName().compareTo(o2.getName());
    				}
    			});
    	final Remapper remapper = new Collector(classes, prefix);
    	final ClassVisitor inner = new EmptyVisitor();
    	final RemappingClassAdapter visitor =
    			new RemappingClassAdapter(inner, remapper);
    	reader.accept(visitor, 0);
    	return classes;
    }

    public static Set<Class<?>> getClassesUsedBy(
    		final String name,   // class name
    		final String prefix  // common prefix for all classes
    		// that will be retrieved
    		) throws IOException{
    	final ClassReader reader = new ClassReader(name);
    	final Set<Class<?>> classes =
    			new TreeSet<Class<?>>(new Comparator<Class<?>>(){
    				
    				@Override
    				public int compare(final Class<?> o1, final Class<?> o2){
    					return o1.getName().compareTo(o2.getName());
    				}
    			});
    	final Remapper remapper = new Collector(classes, prefix);
    	final ClassVisitor inner = new EmptyVisitor();
    	final RemappingClassAdapter visitor =
    			new RemappingClassAdapter(inner, remapper);
    	reader.accept(visitor, 0);
    	return classes;
    }
}
