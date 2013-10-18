package jadex.maven;

import jadex.bdiv3.BDIModelLoader;
import jadex.bdiv3.KernelBDIV3Agent;
import jadex.bdiv3.model.BDIModel;
import jadex.bridge.ResourceIdentifier;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
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
	 */
	protected File baseDirectory;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 * @readonly
	 */
	protected MavenProjectHelper projectHelper;

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

			Set<Artifact> relevantCompileArtifacts = getRelevantCompileArtifacts();
			for (Artifact artifact : relevantCompileArtifacts)
			{
				File jarFile = artifact.getFile();
				// enhance the jar

//				artifact.setFile(enhanceJar(jarFile));
			}

			File outputDirectory = new File(buildDirectory, "bdi-generated");
			// outputDirectory = inputDirectory;
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
	private File enhanceJar(File in)
	{
		String target = project.getBuild().getOutputDirectory();
		File tmp = new File(target, "bdi-generated");
		tmp.mkdirs();
		File out = new File(tmp, in.getName());

		if (out.exists())
		{
			return out;
		}
		else
		{
			try
			{
				out.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		// Create a new Jar file
		FileOutputStream fos = null;
		ZipOutputStream jos = null;
		try
		{
			fos = new FileOutputStream(out);
			jos = new ZipOutputStream(fos);
		}
		catch (FileNotFoundException e1)
		{
			getLog().error("Cannot enhance jar: the output file " + out.getAbsolutePath() + " has not been found");
			return null;
		}

		ZipFile inZip = null;
		try
		{
			inZip = new ZipFile(in);
			Enumeration<? extends ZipEntry> entries = inZip.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();
				if (bdiFileFilter.accept(new File("/"), entry.getName())) {
				} else {
					// If the entry is not a duplicate, copy.
					jos.putNextEntry(entry);
					InputStream currIn = inZip.getInputStream(entry);
					copyStreamWithoutClosing(currIn, jos);
					currIn.close();
					jos.closeEntry();
				}
			}
		}
		catch (IOException e)
		{
			getLog().error("Cannot removing duplicates : " + e.getMessage());
			return null;
		}

		try
		{
			if (inZip != null)
			{
				inZip.close();
			}
			jos.close();
			fos.close();
			jos = null;
			fos = null;
		}
		catch (IOException e)
		{
			// ignore it.
		}
		getLog().info(in.getName() + " rewritten enhanced: " + out.getAbsolutePath());
		return out;
	}

	private void generateBDI(File inputDirectory, File outputDirectory) throws Exception
	{
		BDIModelLoader modelLoader = new BDIModelLoader();

		ByteKeepingASMBDIClassGenerator gen = new ByteKeepingASMBDIClassGenerator();

		Collection<File> allBDIFiles = getAllBDIFiles(inputDirectory);
		String[] imports = getImportPath(allBDIFiles);
		ResourceIdentifier rid = new ResourceIdentifier();

		getLog().debug("Found BDI Files: " + allBDIFiles);
		URL inputUrl = inputDirectory.toURI().toURL();
		getLog().debug("Generating to: " + outputDirectory);

		ClassLoader originalCl = getClass().getClassLoader();
		URLClassLoader inputCl = new URLClassLoader(new URL[]
		{inputUrl}, originalCl);
		URLClassLoader outputCl = new URLClassLoader(new URL[]
		{inputUrl}, originalCl);

		getLog().info("Generating BDI V3 Agent classes...");

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
							// delete non-enhanced to allow repeatable execution
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

	private Collection<File> getAllBDIFiles(File dir)
	{
		final List<String> kernelTypes = getBDIKernelTypes();

		IOFileFilter dirFilter = new IOFileFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				return true;
			}

			@Override
			public boolean accept(File file)
			{
				return true;
			}
		};
		return FileUtils.listFiles(dir, bdiFileFilter, dirFilter);
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
	* Copies an input stream into an output stream but does not close the streams.
	*
	* @param in the input stream
	* @param out the output stream
	* @throws IOException if the stream cannot be copied
	*/
	    private static void copyStreamWithoutClosing( InputStream in, OutputStream out ) throws IOException
	    {
	        final int bufferSize = 4096;
	        byte[] b = new byte[ bufferSize ];
	        int n;
	        while ( ( n = in.read( b ) ) != - 1 )
	        {
	            out.write( b, 0, n );
	        }
	    }

}
