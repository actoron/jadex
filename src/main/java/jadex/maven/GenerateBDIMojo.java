package jadex.maven;

import jadex.bdiv3.BDIClassReader;
import jadex.bdiv3.BDIModelLoader;
import jadex.bdiv3.KernelBDIV3Agent;
import jadex.bdiv3.model.BDIModel;
import jadex.bridge.ResourceIdentifier;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * @goal generateBDI
 * @phase compile
 * @requiresProject true
 * @requiresOnline false
 * @author Julian Kalinowski
 * 
 */
public class GenerateBDIMojo extends AbstractMojo
{

	/**
	 * The maven project.
	 * 
	 * @parameter property=project
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

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
	protected File outputDirectory;

	/**
	 * The android resources directory.
	 * 
	 * @parameter default-value="${project.basedir}/res"
	 */
	protected File resourceDirectory;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 * @readonly
	 */
	protected MavenProjectHelper projectHelper;

	@SuppressWarnings("resource")
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			generateBDI();
			getLog().info("Generated BDI V3 Agents successfully!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MojoExecutionException(e.getMessage());
		}
			
	}
	private void generateBDI() throws Exception
	{
		BDIModelLoader modelLoader = new BDIModelLoader();
		BDIClassReader reader = new BDIClassReader(modelLoader);
		
		ByteKeepingASMBDIClassGenerator gen = new ByteKeepingASMBDIClassGenerator();
				
		Collection<File> allBDIFiles = getAllBDIFiles(outputDirectory);
		String[] imports = getImportPath(allBDIFiles);
		ResourceIdentifier rid = new ResourceIdentifier();

		getLog().debug("Found BDI Files: " + allBDIFiles);
		URLClassLoader classLoader;
		URL outputUrl;
		outputUrl = outputDirectory.toURI().toURL();
		ClassLoader originalCl = getClass().getClassLoader();
//		URL[] urls = SUtil.collectClasspathURLs(originalCl).toArray(new URL[1]);
//		
//		ClassLoader baseCl = new ClassLoader() {};
//		DelegationURLClassLoader otherCl = new DelegationURLClassLoader(baseCl, new DelegationURLClassLoader[]{
//			new DelegationURLClassLoader(originalCl, null)
//		});
		
		URLClassLoader inputCl = new URLClassLoader(new URL[]{outputUrl}, originalCl) {
			
		};
		URLClassLoader outputCl = new URLClassLoader(new URL[]{outputUrl}, originalCl);
		
		getLog().info("Generating BDI V3 Agent classes...");

		for (File bdiFile : allBDIFiles)
		{
			List<Class<?>> classes = null;
			BDIModel model = null;
			String relativePath = ResourceUtils.getRelativePath(bdiFile.getAbsolutePath(), outputDirectory.getAbsolutePath(),
					File.separator);
			String className = relativePath.replaceAll(File.separator, ".").replace(".class", "");

			getLog().debug("Loading Model: " + relativePath);

			model = (BDIModel) modelLoader.loadModel(relativePath, imports, inputCl , new Object[]
			{rid, null});
			// className = model.getModelInfo().getPackage() +
			// model.getModelInfo().getName();

			getLog().info("Generating classes for: " + relativePath);
			classes = gen.generateBDIClass(className, model,
					 outputCl);
			for (Class<?> clazz : classes)
			{
				String path = clazz.getName().replace('.', File.separatorChar) + ".class";
				getLog().debug("path: " + path);
				byte[] classBytes = gen.getClassBytes(clazz.getName());
//				ByteArrayInputStream inputStream = new ByteArrayInputStream(classBytes);
//				FileWriter writer = null;
				try
				{
					getLog().info("    ... " + clazz.getName());
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(outputDirectory, path)));
					dos.write(classBytes);
					dos.close();
//					IOUtils.copy(inputStream , writer);
//					writer.close();
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
	}
	private String[] getImportPath(Collection<File> allBDIFiles)
	{
		getLog().debug("Building imports Path...");
		List<String> result = new ArrayList<String>();
		String absoluteOutput = outputDirectory.getAbsolutePath();

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

		IOFileFilter fileFilter = new IOFileFilter()
		{

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
		return FileUtils.listFiles(dir, fileFilter, dirFilter);
	}

}
