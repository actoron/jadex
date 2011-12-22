package haw.mmlab.production_line.configuration.export;

import haw.mmlab.production_line.configuration.ProductionLineConfiguration;
import haw.mmlab.production_line.configuration.Robot;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.configuration.Task;
import haw.mmlab.production_line.configuration.Transport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import deco4mas.util.xml.XmlUtil;

/**
 * Export .dot files via the velocity template engine.<br>
 * Supports the generation of graphic files.
 * 
 * @author Jan Sudeikat
 */
public class DotWriter {

	// -------- constants --------

	private static final String TEMPLATE_NAME_CG = "haw/mmlab/production_line/configuration/export/template/dot_export_cg.vm";
	private static final String TEMPLATE_NAME_RFG = "haw/mmlab/production_line/configuration/export/template/dot_export_rfg.vm";
	private static final String EXPORT_FILE_EXTENSION = ".dot";

	private static final String VELOCITY_TRANSPORTS_IDENTIFIER = "transports";
	private static final String VELOCITY_ROBOTS_IDENTIFIER = "robots";
	private static final String VELOCITY_SIMPLE_TRANSPORTS_IDENTIFIER = "transports";
	private static final String VELOCITY_SIMPLE_ROBOTS_IDENTIFIER = "robots";

	// -------- methods --------

	/**
	 * Exports the communication graph as a dot file from the given {@link ProductionLineConfiguration}.
	 * 
	 * @param file_name
	 *            the name of the exported file (without suffix)
	 * @param plc
	 *            the given {@link ProductionLineConfiguration}
	 */
	public boolean exportDotFileCG(String file_name, ProductionLineConfiguration plc) {
		// first, we initialize the runtime engine...
		try {
			Properties p = new Properties();
			p.setProperty("resource.loader", "file, class");
			p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
			p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			p.setProperty("directive.foreach.counter.name", "counter");
			p.setProperty("directive.foreach.counter.initial.value", "0");
			p.setProperty("file.resource.loader.path", ".haw/mmlab/production_line/configuration/export/template");
			Velocity.init(p);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// create file to write in...
		File outputFile = new File(file_name + EXPORT_FILE_EXTENSION);
		// System.out.println("Export to: " + outputFile.getAbsolutePath());
		FileWriter w;
		// create context to write...
		VelocityContext context = new VelocityContext();
		// insert the data for the template:
		context.put(VELOCITY_ROBOTS_IDENTIFIER, plc.getRobots());
		context.put(VELOCITY_TRANSPORTS_IDENTIFIER, plc.getTransports());

		try {
			w = new FileWriter(outputFile);
			if (Velocity.mergeTemplate(TEMPLATE_NAME_CG, "UTF-8", context, w)) {
				// File has been successfully written
				w.close();
				return true;
			} else {
				// Error:
				w.close();
				return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (ParseErrorException e) {
			e.printStackTrace();
			return false;
		} catch (MethodInvocationException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Generates png file.
	 * 
	 * @param fileName
	 *            the name of the file (for both .dot and .png extension)
	 */
	public void generatePNG(String fileName) {
		// String command = "C:/development/Graphviz2.26.3/bin/dot.exe -Tpng " + fileName + ".dot -o " + fileName + ".png";
		//
		// try {
		// Runtime.getRuntime().exec(command);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Exports the resource flow graph as a dot file from the given {@link ProductionLineConfiguration}.
	 * 
	 * @param fileName
	 *            the name of the exported file (without suffix)
	 * @param plc
	 *            the given {@link ProductionLineConfiguration}
	 */
	public boolean exportDotFileRFG(String fileName, ProductionLineConfiguration plc) {
		List<SimpleAgent> robots = getSimpleRobots(plc);
		List<SimpleAgent> transports = getSimpleTransports(plc);

		// first, we initialize the runtime engine...
		try {
			Properties p = new Properties();
			p.setProperty("resource.loader", "file, class");
			p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
			p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			p.setProperty("directive.foreach.counter.name", "counter");
			p.setProperty("directive.foreach.counter.initial.value", "0");
			p.setProperty("file.resource.loader.path", ".haw/mmlab/production_line/configuration/export/template");
			Velocity.init(p);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// create file to write in...
		File outputFile = new File(fileName + EXPORT_FILE_EXTENSION);
		// System.out.println("Export to: " + outputFile.getAbsolutePath());
		FileWriter w;
		// create context to write...
		VelocityContext context = new VelocityContext();
		// insert the data for the template:
		// insert the data for the template:
		context.put(VELOCITY_SIMPLE_ROBOTS_IDENTIFIER, robots);
		context.put(VELOCITY_SIMPLE_TRANSPORTS_IDENTIFIER, transports);

		try {
			w = new FileWriter(outputFile);
			if (Velocity.mergeTemplate(TEMPLATE_NAME_RFG, "UTF-8", context, w)) {
				// File has been successfully written
				w.close();
				return true;
			} else {
				// Error:
				w.close();
				return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (ParseErrorException e) {
			e.printStackTrace();
			return false;
		} catch (MethodInvocationException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates a {@link List} of {@link SimpleAgent}s from {@link ProductionLineConfiguration#getRobots()}.
	 * 
	 * @param plc
	 *            the given {@link ProductionLineConfiguration}
	 * @return a {@link List} of {@link SimpleAgent}s
	 */
	private List<SimpleAgent> getSimpleRobots(ProductionLineConfiguration plc) {
		List<SimpleAgent> robots = new ArrayList<SimpleAgent>();

		for (Robot robot : plc.getRobots()) {
			for (Role role : robot.getRoles()) {
				SimpleAgent agent = new SimpleAgent();
				agent.setAgentId(robot.getAgentId());
				agent.setPredecessor(role.getPrecondition().getTargetAgent());
				agent.setSuccessor(role.getPostcondition().getTargetAgent());
				agent.setTaskId(role.getPrecondition().getTaskId());
				agent.setType(SimpleAgent.TYPE_ROBOT);

				robots.add(agent);
			}
		}

		return robots;
	}

	/**
	 * Creates a {@link List} of {@link SimpleAgent}s from {@link ProductionLineConfiguration#getTransports()}.
	 * 
	 * @param plc
	 *            the given {@link ProductionLineConfiguration}
	 * @return a {@link List} of {@link SimpleAgent}s
	 */
	private List<SimpleAgent> getSimpleTransports(ProductionLineConfiguration plc) {
		List<SimpleAgent> transports = new ArrayList<SimpleAgent>();
		List<Task> tasks = plc.getTasks();

		for (Transport transport : plc.getTransports()) {
			for (Role role : transport.getRoles()) {
				SimpleAgent agent = new SimpleAgent();
				agent.setAgentId(transport.getAgentId());
				if (role.getPrecondition().getTargetAgent() != null)
					agent.setPredecessor(role.getPrecondition().getTargetAgent());
				if (role.getPostcondition().getTargetAgent() != null)
					agent.setSuccessor(role.getPostcondition().getTargetAgent());

				Task task = getTask(tasks, role.getPrecondition().getTaskId());
				if (task != null) {
					agent.setTaskId(role.getPrecondition(), task);
				}
				agent.setType(SimpleAgent.TYPE_TRANSPORT);

				transports.add(agent);
			}
		}

		return transports;
	}

	/**
	 * Gets the {@link Task} with the given taskId from the {@link List} of given {@link Task}s or <code>null</code> if there is no match.
	 * 
	 * @param tasks
	 *            the {@link List} of given {@link Task}s
	 * @param taskId
	 *            the given taskId
	 * @return the {@link Task} with the given taskId or <code>null</code> if there is no match
	 */
	private Task getTask(List<Task> tasks, String taskId) {
		for (Task task : tasks) {
			if (task.getId().equals(taskId)) {
				return task;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		ProductionLineConfiguration plc = null;
		DotWriter dw = new DotWriter();
		try {
			plc = (ProductionLineConfiguration) XmlUtil.retrieveFromXML(ProductionLineConfiguration.class, "conf/generated.conf.xml");

			dw.exportDotFileRFG("generated_rfg", plc);
			dw.exportDotFileCG("generated_cg", plc);
			dw.generatePNG("generated_rfg");
			dw.generatePNG("generated_cg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}