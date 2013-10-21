package jadex.maven;

import jadex.bdiv3.BDIModelLoader;
import jadex.bdiv3.KernelBDIV3Agent;
import jadex.bdiv3.model.BDIModel;
import jadex.bridge.ResourceIdentifier;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;

/**
 * @goal generateBDI
 * @phase compile
 * @requiresProject true
 * @requiresOnline false
 * @requiresDependencyResolution runtime
 * @author Julian Kalinowski
 * 
 */
public class GenerateBDIMojo extends AbstractJadexMojo
{

	/**
	 * The maven session.
	 * 
	 * @parameter property=session
	 * @required
	 * @readonly
	 */
	protected MavenSession session;

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
	 * 
	 * @parameter default-value="false"
	 */
	protected Boolean enhanceDependencies;

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

	@SuppressWarnings("resource")
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			File outputDirectory = new File(buildDirectory, "bdi-generated");
			File tmpDirectory = new File(buildDirectory, "bdi-generated-deps");

			if (enhanceDependencies)
			{
				Set<Artifact> relevantCompileArtifacts = getRelevantCompileArtifacts();
				getLog().info("Found " + relevantCompileArtifacts.size() + " dependencies: " + relevantCompileArtifacts);

				outputDirectory.mkdirs();
				tmpDirectory.mkdirs();

				for (Artifact artifact : relevantCompileArtifacts)
				{
					File jarFile = artifact.getFile();
					// enhance the jar

					File enhanceJar;
					try
					{
						enhanceJar = enhanceJar(jarFile, new File(tmpDirectory, jarFile.getName()));
						artifact.setFile(enhanceJar);
					}
					catch (IOException e)
					{
						getLog().error("Could not enhance jar: " + jarFile);
						e.printStackTrace();
					}
				}

				// outputDirectory = inputDirectory;
				getLog().info("Now enhancing project-owned code...");
			}
			generateBDI(inputDirectory, outputDirectory);

			getLog().info("Generated BDI V3 Agents successfully!");

			project.getBuild().setOutputDirectory(outputDirectory.getPath());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MojoExecutionException(e.getMessage());
		}

	}

	private File enhanceJar(File in, File outputDir) throws IOException
	{
		unzipJar(in, outputDir);

		getLog().info("enhancing " + in);
		// now the whole jar has been extracted to generated-bdi/jar-name/
		File outputFile = new File(outputDir.getParent(), in.getName().replace(".jar", ".generated.jar"));
		getLog().debug("Zipping to: " + outputFile);
		JarOutputStream jos = null;
		try
		{
			// generate
			generateBDI(outputDir, outputDir);
			// and now re-zip the enhanced jar...

			jos = new JarOutputStream(new FileOutputStream(outputFile));
			// System.out.println(outputDir.list());
			Collection<File> allFiles = FileUtils.listFiles(outputDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			for (File file : allFiles)
			{
				String relativePath = ResourceUtils.getRelativePath(file.getPath(), outputDir.getPath(), File.separator);
				FileInputStream is = new FileInputStream(file);
				ZipEntry zipEntry = new ZipEntry(relativePath);
				jos.putNextEntry(zipEntry);
				copyStreamWithoutClosing(is, jos);
				jos.closeEntry();
				is.close();
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
			}
		}

		getLog().debug(in.getName() + " rewritten enhanced: " + outputFile.getName());
		return outputFile;
	}

	private void unzipJar(File in, File outputDir) throws ZipException, IOException
	{
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
			if (bdiFileFilter.accept(new File("/"), entry.getName()))
			{
			}
			else
			{
				// If the entry is not a duplicate, copy.
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
		}

		try
		{
			if (inZip != null)
			{
				inZip.close();
			}
			fos.close();
			fos = null;
		}
		catch (IOException e)
		{
			// ignore it.
		}

	}

	private void generateBDI(File inputDirectory, File outputDirectory) throws Exception
	{
		BDIModelLoader modelLoader = new BDIModelLoader();

		ByteKeepingASMBDIClassGenerator gen = new ByteKeepingASMBDIClassGenerator();

		Collection<File> allBDIFiles = FileUtils.listFiles(inputDirectory, bdiFileFilter, TrueFileFilter.INSTANCE);
		String[] imports = getImportPath(allBDIFiles);
		ResourceIdentifier rid = new ResourceIdentifier();

		if (allBDIFiles.size() > 0)
		{
			getLog().info("Found " + allBDIFiles.size() + " BDI V3 Agent classes in " + inputDirectory);
		}
		URL inputUrl = inputDirectory.toURI().toURL();
		getLog().debug("Generating to: " + outputDirectory);

		ClassLoader originalCl = getClass().getClassLoader();
		URLClassLoader inputCl = new URLClassLoader(new URL[]
		{inputUrl}, originalCl);
		URLClassLoader outputCl = new URLClassLoader(new URL[]
		{inputUrl}, originalCl);
		Collection<File> allClasses = FileUtils.listFiles(inputDirectory, new String[]
		{"class"}, true);

		for (File bdiFile : allClasses)
		{
			List<Class<?>> classes = null;
			BDIModel model = null;

			String relativePath = ResourceUtils
					.getRelativePath(bdiFile.getAbsolutePath(), inputDirectory.getAbsolutePath(), File.separator);
			if (bdiFileFilter.accept(bdiFile))
			{
				String className = relativePath.replaceAll(File.separator, ".").replace(".class", "");

				getLog().debug("Loading Model: " + relativePath);

				model = (BDIModel) modelLoader.loadModel(relativePath, imports, inputCl, new Object[]
				{rid, null});
				// className = model.getModelInfo().getPackage() +
				// model.getModelInfo().getName();

				getLog().info("Generating classes for: " + relativePath);
				classes = gen.generateBDIClass(className, model, outputCl);
				for (Class<?> clazz : classes)
				{
					String path = clazz.getName().replace('.', File.separatorChar) + ".class";
					// String path = relativePath;
					getLog().debug("path: " + path);
					byte[] classBytes = gen.getClassBytes(clazz.getName());
					// ByteArrayInputStream inputStream = new
					// ByteArrayInputStream(classBytes);
					// FileWriter writer = null;
					try
					{
						// write enhanced class
						getLog().info("    ... " + clazz.getName());
						File enhancedFile = new File(outputDirectory, path);
						enhancedFile.getParentFile().mkdirs();
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(enhancedFile));
						dos.write(classBytes);
						dos.close();

						if (!inputDirectory.equals(outputDirectory))
						{
							// delete non-enhanced to allow repeatable
							// execution
							// of
							// this plugin
							File oldFile = new File(inputDirectory, path);
							oldFile.delete();
						}
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
						throw new MojoExecutionException(e.getMessage());
					}
					catch (IOException e)
					{
						e.printStackTrace();
						throw new MojoExecutionException(e.getMessage());
					}
				}
			}
			else
			{
				// jsut copy file
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
	}
	
	private String[] getImportPath(Collection<File> allBDIFiles)
	{
		getLog().debug("Building imports Path...");
		List<String> result = new ArrayList<String>();
		String absoluteOutput = inputDirectory.getAbsolutePath();

		for (File bdiFile : allBDIFiles)
		{
			String relativePath = ResourceUtils.getRelativePath(bdiFile.getAbsolutePath(), absoluteOutput, File.separator);
			String importPath = relativePath.replaceAll(File.separator, ".").replace(".class", "");
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
			getLog().info("possible annotation: " + value[i]);
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

		getLog().info("KernelBDIV3 Types: " + kernelTypes);
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
