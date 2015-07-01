package jadex.maven;

import jadex.bdiv3.AbstractAsmBdiClassGenerator;
import jadex.bdiv3.ByteKeepingASMBDIClassGenerator;
import jadex.bdiv3.KernelBDIV3Agent;
import jadex.bdiv3.MavenBDIModelLoader;
import jadex.bdiv3.model.BDIModel;
import jadex.bridge.ResourceIdentifier;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * @goal generateBDI
 * @phase compile
 * @requiresProject true
 * @requiresOnline false
 * @requiresDependencyResolution compile+runtime
 * @author Julian Kalinowski
 * 
 */
public class GenerateBDIMojo extends AbstractJadexMojo
{

	/**
	 * The java sources directory.
	 * 
	 * @parameter default-value="${project.build.sourceDirectory}"
	 * @readonly
	 */
	protected File sourceDirectory;

	/**
	 * The java target directory.
	 * 
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @readonly
	 */
	protected File inputDirectory;

	/**
	 * @parameter default-value="${project.build.directory}"
	 * @readonly
	 */
	protected File buildDirectory;

	/**
	 * The android resources directory.
	 * 
	 * @parameter default-value="${project.basedir}"
	 * @readonly
	 */
	protected File baseDirectory;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 * @readonly
	 */
	protected MavenProjectHelper projectHelper;

	/**
	 * Decides whether or not to enhance project runtime dependencies, too.
	 * This is an experimental Goal.
	 * The recommended way to use dependencies including BDIV3 classes is to include
	 * this plugin in every of those dependency builds.
	 * @parameter default-value="false"
	 */
	protected Boolean enhanceDependencies;
	
	/**
	 * If set to true, this will remove all android-incompatible classes from Build,
	 * e.g. Classes that use java.swing components.
	 * @parameter default-value="false"
	 */
	protected Boolean removeAndroidIncompatible;
	
	
	/**
	 * Enable in-place mode.
	 * Tries to enhance the classes directly inside the output dir. 
	 * (necessary to work with eclipse build system)
	 * This does not affect dependency handling.
	 * @parameter default-value="true"
	 */
	protected Boolean inPlace;
	
	/**
	 * The PluginDescriptor.
	 * @component
	 */
	private PluginDescriptor descriptor;
	
	/**
	 * The BuildContext.
	 * @component
	 */
	private BuildContext buildContext;
	
	private IOFileFilter bdiFileFilter = new IOFileFilter()
	{
		private List<String> kernelTypes = getBDIKernelTypes();
		@Override
		public boolean accept(File dir, String name)
		{
			boolean result = false;
			for (String string : kernelTypes)
			{
				if (name.endsWith(string))
				{
					result = true;
					break;
				}
			}
			return result;
		}

		@Override
		public boolean accept(File file)
		{
			return accept(file.getParentFile(), file.getName());
		}
	};

	private MavenBDIModelLoader modelLoader;
	private ByteKeepingASMBDIClassGenerator gen;

	public GenerateBDIMojo()
	{
		modelLoader = new MavenBDIModelLoader();
		gen = new ByteKeepingASMBDIClassGenerator();
		modelLoader.setGenerator(gen);
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		if (!inputDirectory.getAbsolutePath().endsWith("/bin/classes")) {
			buildContext.addMessage(inputDirectory, 0, 0, "For Android projects, <build><directory> must be set to 'bin'!",  BuildContext.SEVERITY_ERROR, null);
		}
		
		File outputDirectory; 
		if (inPlace) {
			getLog().info("Trying to enhance classes in-place...");
			outputDirectory = inputDirectory;
		} else {
			outputDirectory = new File(buildDirectory, "bdi-generated");
		}
		
		getLog().info("Generating from: " + inputDirectory.toString());
		getLog().info("Generating to: " + outputDirectory.toString());
		
		try
		{

			if (enhanceDependencies)
			{
				File depDirectory = new File(buildDirectory, "bdi-generated-deps");
				outputDirectory.mkdirs();
				depDirectory.mkdirs();
				File dummy = new File(depDirectory, "dummy.jar");
				makeJar(new File[0], dummy);
		
				Set<Artifact> relevantCompileArtifacts = getRelevantCompileArtifacts();
				getLog().info("Found " + relevantCompileArtifacts.size() + " dependencies: " + relevantCompileArtifacts);

				File depOutputDir = new File(depDirectory, "alldeps");
				File allDepsFile = new File(depDirectory, "enhanced-dependencies.jar");
				
				for (Artifact artifact : relevantCompileArtifacts)
				{
					File jarFile = artifact.getFile();
					
					if (jarFile.isDirectory()) {
						// we are probably running inside eclipse M2E and got a project dependency
						// so jarFile is the path to target/classes of the dependency
						
						// copy classes
						FileUtils.copyDirectory(jarFile, depOutputDir);
					} else {
						// unzip the jar
						unzipJar(jarFile, depOutputDir);
					}
					
					// ignore original jar
					artifact.setFile(dummy);

				}
				// add real deps to random artifact
				relevantCompileArtifacts.iterator().next().setFile(allDepsFile);
				
				File metaInf= new File(depOutputDir, "META-INF"); 
				FileUtils.deleteDirectory(metaInf);
				
				// strip incompatible
				if (removeAndroidIncompatible) {
					getLog().info("Removing incompatible code...");
					removeAndroidIncompatible(depOutputDir);
				}
				
				generateBDI(depOutputDir, depOutputDir);
				
				File[] depDirs = depDirectory.listFiles();
				makeJar(depDirs, allDepsFile);
				
				// outputDirectory = inputDirectory;
			}
			if (buildContext.hasDelta(inputDirectory)) {
				getLog().info("Enhancing project-own classes in directory: " + inputDirectory);
				generateBDI(inputDirectory, outputDirectory);
				getLog().info("Generated BDI V3 Agents successfully!");
			} else {
				getLog().info("Everything up to date.");
			}
			
			project.getBuild().setOutputDirectory(outputDirectory.getPath());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MojoExecutionException(e.toString());
		}

	}

	private void makeJar(File[] depDirs, File outputFile)
	{
		getLog().info("Zipping to: " + outputFile);
		JarOutputStream jos = null;
		try
		{
			jos = new JarOutputStream(new FileOutputStream(outputFile), new Manifest());	// Empty manifest to avoid empty zip file.
			
			// System.out.println(outputDir.list());
			for (File depDir : depDirs)
			{
				if (depDir.isDirectory()) {
					Collection<File> allFiles = FileUtils.listFiles(depDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
					for (File file : allFiles)
					{
						String relativePath = ResourceUtils.getRelativePath(file.getPath(), depDir.getPath(), File.separator);
						if(!File.separator.equals("/"))
						{
							// Zip entries must use '/' as file separator.
							relativePath	= relativePath.replace(File.separator, "/");
						}
						FileInputStream is = new FileInputStream(file);
						ZipEntry zipEntry = new ZipEntry(relativePath);
						jos.putNextEntry(zipEntry);
						copyStreamWithoutClosing(is, jos);
						jos.closeEntry();
						is.close();
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				jos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		getLog().info("written enhanced: " + outputFile.getName());
	}

	private void unzipJar(File in, File outputDir) throws ZipException, IOException
	{
		getLog().info("unzipping " + in.getName());
		if (!outputDir.exists())
		{
			outputDir.mkdir();
		}

		// Create a new Jar file
		FileOutputStream fos = null;

		ZipFile inZip = null;
		inZip = new ZipFile(in);
		Enumeration<? extends ZipEntry> entries = inZip.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();
			InputStream currIn = inZip.getInputStream(entry);
			if (entry.isDirectory())
			{
				File dir = new File(outputDir, entry.getName());
				dir.mkdir();
			}
			else
			{
				File outputFile = new File(outputDir, entry.getName());
				File dir = outputFile.getParentFile();
				if (!dir.exists())
				{
					dir.mkdirs();
				}
				fos = new FileOutputStream(outputFile);
				copyStreamWithoutClosing(currIn, fos);
				currIn.close();
				fos.close();
			}
		}

		try
		{
			if (inZip != null)
			{
				inZip.close();
			}
			if(fos!=null)
			{
				fos.close();
			}
			fos = null;
		}
		catch (IOException e)
		{
			// ignore it.
		}

	}

	private void generateBDI(File inputDirectory, File outputDirectory) throws Exception
	{
		Collection<File> allBDIFiles = FileUtils.listFiles(inputDirectory, bdiFileFilter, TrueFileFilter.INSTANCE);
		String[] imports = getImportPath(allBDIFiles);
		ResourceIdentifier rid = new ResourceIdentifier();

		if (allBDIFiles.size() > 0)
		{
			getLog().info("Found " + allBDIFiles.size() + " BDI V3 Agent classes in " + inputDirectory);
		}
		URL inputUrl = inputDirectory.toURI().toURL();

		setClassRealm();
		ClassLoader originalCl = descriptor.getClassRealm();
		URLClassLoader inputCl = new URLClassLoader(new URL[]
		{inputUrl}, originalCl);
		
		Collection<File> allClasses = FileUtils.listFiles(inputDirectory, null, true);
		
		for (File bdiFile : allClasses)
		{
			gen.clearRecentClassBytes();
			BDIModel model = null;

			String relativePath = ResourceUtils
					.getRelativePath(bdiFile.getAbsolutePath(), inputDirectory.getAbsolutePath(), File.separator);
			
			if (bdiFileFilter.accept(bdiFile) && buildContext.hasDelta(bdiFile))
			{
				String agentClassName = relativePath.replace(File.separator, ".").replace(".class", "");
				
				if (inPlace) {
					Class<?> loadClass = inputCl.loadClass(agentClassName);
					if (AbstractAsmBdiClassGenerator.isEnhanced(loadClass)) {
						getLog().info("Already enhanced: " + relativePath);
						continue;
					}
//					tempLoader.close();
				}
				
				getLog().debug("Loading Model: " + relativePath);
				
				try
				{
					model = (BDIModel) modelLoader.loadModel(relativePath, imports, inputCl, inputCl, new Object[]
					{rid, null, null});
				}
				catch (Throwable t)
				{
					// if error during model building, just dont enhance this file.
					String message = t.getMessage();
					if (message == null)  {
						message = t.toString();
					}
					getLog().error("Error loading model: " + agentClassName + ", exception was: " + t.toString());
					buildContext.addMessage(bdiFile, 0, 0, "Error loading model: " + agentClassName, BuildContext.SEVERITY_ERROR, t);
					// just copy file
					if (!inputDirectory.equals(outputDirectory))
					{
						File newFile = new File(outputDirectory, relativePath);
						if (!newFile.exists())
						{
							newFile.getParentFile().mkdirs();
							FileUtils.copyFile(bdiFile, newFile);
						}
					}
					continue;
				}
				
				getLog().info("Generating classes for: " + relativePath);
//				classes = gen.generateBDIClass(agentClassName, model, outputCl);
				
				Set<Entry<String,byte[]>> classEntrySet = gen.getRecentClassBytes().entrySet();
				
//				for (Class<?> clazz : classes)
				for (Entry<String, byte[]> entry : classEntrySet)
				{
					String className = entry.getKey();
					byte[] classBytes = entry.getValue();
					getLog().debug("    ... " + className);
					String path = className.replace('.', File.separatorChar) + ".class";
//					byte[] classBytes = gen.getClassBytes(clazz.getName());
					try
					{
						// write enhanced class
						File enhancedFile = new File(outputDirectory, path);
						enhancedFile.getParentFile().mkdirs();
						DataOutputStream dos = new DataOutputStream(buildContext.newFileOutputStream(enhancedFile));
						dos.write(classBytes);
						dos.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
						// URLClassLoader.close() not in JDK 1.6
//						if (inputCl != null) {
//							inputCl.close();
//						}
//						if (outputCl != null) {
//							outputCl.close();
//						}
						throw new MojoExecutionException(e.getMessage());
					}
				}
			}
			else
			{
				// just copy file
				if (!inputDirectory.equals(outputDirectory))
				{
					File newFile = new File(outputDirectory, relativePath);
					if (!newFile.exists())
					{
						newFile.getParentFile().mkdirs();
						FileUtils.copyFile(bdiFile, newFile);
					}
				}
			}
		}
//		inputCl.close();
//		outputCl.close();
	}

	private void setClassRealm()
	{
		// collect runtime classpath elements of the user project
		List<String> classPathElements;
		try
		{
			final ClassRealm realm = descriptor.getClassRealm();
			classPathElements = project.getRuntimeClasspathElements();
			classPathElements.addAll(project.getCompileClasspathElements());
			
			for (String element : classPathElements)
			{
			    File elementFile = new File(element);
			    realm.addURL(elementFile.toURI().toURL());
			}
			
		}
		catch(DependencyResolutionRequiredException e1)
		{
			e1.printStackTrace();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	private void removeAndroidIncompatible(File path) throws IOException {
		@SuppressWarnings("unchecked")
		Collection<File> allFiles = FileUtils.listFiles(path, new FileFileFilter() {

			@Override
			public boolean accept(File file)
			{
				return file.getName().endsWith("class");
			}
			
		}, TrueFileFilter.INSTANCE);
		
		for (File file : allFiles)
		{
			boolean compatible = isAndroidCompatible(file);
			if (!compatible) {
				getLog().debug("not compatible: " + file);
				file.delete();
			}
		}
		
	}
	
	private boolean isAndroidCompatible(File bdiFile) throws IOException
	{
		boolean result = true;
		FileInputStream fin = new FileInputStream(bdiFile);
//		ClassReader cr = new ClassReader(fin);
//		ClassNode cn = new ClassNode();
//		cr.accept(cn, ClassReader.EXPAND_FRAMES);
//		
//		List<AnnotationNode> invisibleAnnotations = cn.invisibleAnnotations;
//		
//		if (invisibleAnnotations != null) {
//			for (AnnotationNode ann : invisibleAnnotations)
//			{
//				if (REFLECTNAME.equals(ann.desc)) {
//					List<Object> values = ann.values;
//					boolean compatible = false;
//					int minApi = 0;
//					for (int i = 0; i < values.size(); i=i+2) {
//						String name = (String) values.get(i);
//						if (PARAMNAME_VALUE.equals(name)) {
//							compatible = (Boolean) values.get(i+1);
//						} else if (PARAMNAME_MINAPI.equals(name)) {
//							minApi = (Integer) values.get(i+1);
//						}
//					}
//					result = compatible;
//				}
//			}
//		}
		Set<Class<?>> classesUsedBy = Collector.getClassesUsedBy(fin, "javax.swing");
		result = classesUsedBy.isEmpty();
		
		fin.close();
		return result;
	}

	private String[] getImportPath(Collection<File> allBDIFiles)
	{
		getLog().debug("Building imports Path...");
		List<String> result = new ArrayList<String>();
		String absoluteOutput = inputDirectory.getAbsolutePath();

		for (File bdiFile : allBDIFiles)
		{
			String relativePath = ResourceUtils.getRelativePath(bdiFile.getAbsolutePath(), absoluteOutput, File.separator);
			String importPath = relativePath.replace(File.separator, ".").replace(".class", "");
			result.add(importPath);
		}

		return result.toArray(new String[result.size()]);
	}

	private List<String> getBDIKernelTypes()
	{
		Properties annotation = KernelBDIV3Agent.class.getAnnotation(Properties.class);
		NameValue[] value = annotation.value();

		String types = null;
		for (int i = 0; i < value.length; i++)
		{
			getLog().debug("possible annotation: " + value[i]);
			if (value[i].name().equals("kernel.types"))
			{
				types = value[i].value();
			}
		}

		final List<String> kernelTypes = new ArrayList<String>();
		int begin = types.indexOf("\"");;
		while (begin != -1)
		{
			int end = types.indexOf("\"", begin + 1);
			String kernelType = types.substring(begin + 1, end);
			if (kernelType.length() > 0)
			{
				kernelTypes.add(kernelType);
			}
			begin = types.indexOf("\"", end + 1);
		}

		getLog().debug("KernelBDIV3 Types: " + kernelTypes);
		return kernelTypes;
	}

	/**
	 * Copies an input stream into an output stream but does not close the
	 * streams.
	 * 
	 * @param in
	 *            the input stream
	 * @param out
	 *            the output stream
	 * @throws IOException
	 *             if the stream cannot be copied
	 */
	private static void copyStreamWithoutClosing(InputStream in, OutputStream out) throws IOException
	{
		final int bufferSize = 4096;
		byte[] b = new byte[bufferSize];
		int n;
		while ((n = in.read(b)) != -1)
		{
			out.write(b, 0, n);
		}
	}

}
