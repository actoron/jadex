/**
 * 
 */
package haw.mmlab.production_line.simulation.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Describes the parameters of a simulation.
 * 
 * @author Peter
 * 
 */
@XmlRootElement(name = "simulation_configuration")
public class SimulationConfig {
	private Redundancy redundancy;
	private Integer workpieceCount;
	private ProcessTime processTime;
	private BufferSize bufferSize;
	private Integer runCount;
	private Integer timelordInterval;
	private List<TaskConf> tasks = new ArrayList<TaskConf>();

	@XmlElement(name = "redundancy")
	public Redundancy getRedundancy() {
		return redundancy;
	}

	public void setRedundancy(Redundancy redundancy) {
		this.redundancy = redundancy;
	}

	public Integer getRedundancyMin() {
		return redundancy == null ? null : redundancy.getMin();
	}

	public Integer getRedundancyMax() {
		return redundancy == null ? null : redundancy.getMax();
	}

	@XmlElement(name = "workpieces")
	public Integer getWorkpieceCount() {
		return workpieceCount;
	}

	public void setWorkpieceCount(Integer workpieceCount) {
		this.workpieceCount = workpieceCount;
	}

	@XmlElement(name = "processing_time")
	public ProcessTime getProcessTime() {
		return processTime;
	}

	public void setProcessTime(ProcessTime processTime) {
		this.processTime = processTime;
	}

	@XmlTransient
	public Integer getProcTimeMin() {
		return processTime == null ? null : processTime.getMin();
	}

	@XmlTransient
	public Integer getProcTimeMax() {
		return processTime == null ? null : processTime.getMax();
	}

	@XmlElement(name = "buffer_size")
	public BufferSize getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(BufferSize bufferSize) {
		this.bufferSize = bufferSize;
	}

	@XmlTransient
	public Integer getBufferSizeMin() {
		return bufferSize == null ? null : bufferSize.getMin();
	}

	@XmlTransient
	public Integer getBufferSizeMax() {
		return bufferSize == null ? null : bufferSize.getMax();
	}

	@XmlElement(name = "runs")
	public Integer getRunCount() {
		return runCount;
	}

	public void setRunCount(Integer runCount) {
		this.runCount = runCount;
	}

	@XmlElement(name = "timelord_interval")
	public Integer getTimelordInterval() {
		return timelordInterval;
	}

	public void setTimelordInterval(Integer timelordInterval) {
		this.timelordInterval = timelordInterval;
	}

	@XmlElementWrapper(name = "tasks")
	@XmlElement(name = "task")
	public List<TaskConf> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskConf> tasks) {
		this.tasks = tasks;
	}
}
