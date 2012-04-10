/**
 * 
 */
package sodekovs.graphanalysis;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

/**
 * @author thomas
 * 
 */
public class GraphView extends JFrame {

	private static final long serialVersionUID = -5879321363760163680L;

	private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
	private static final Dimension DEFAULT_SIZE = new Dimension(1000, 1000);

	private JGraphModelAdapter<Vertex, DefaultWeightedEdge> adapter;

	public static void main(String[] args) {
		GraphView view = new GraphView();
		view.init();
	}

	private void init() {
		GraphBuilder2 gb = new GraphBuilder2();

		ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph = gb.deserialize(new File("graphanalysis2/london_by_bike.ser"));

		adapter = new JGraphModelAdapter<Vertex, DefaultWeightedEdge>(graph);
		JGraph jGraph = new JGraph(adapter);

		adjustDisplaySettings(jGraph);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(jGraph);
		this.pack();
		this.setVisible(true);
	}

	private void adjustDisplaySettings(JGraph jg) {
		jg.setPreferredSize(DEFAULT_SIZE);
		Color c = DEFAULT_BG_COLOR;
		jg.setBackground(c);
	}
}