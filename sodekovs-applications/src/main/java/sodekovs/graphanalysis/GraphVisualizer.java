package sodekovs.graphanalysis;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * 
 * @author thomas
 * 
 */
public class GraphVisualizer {

	public static void main(String[] args) {
		GraphBuilder gb = new GraphBuilder();

		DirectedSparseMultigraph<Vertex, Edge> londonBikeGraph = gb.deserialize(new File("graphanalysis/london_by_bike.ser"));
		gb.removeEdges(londonBikeGraph, 500);

		Layout<Vertex, Edge> layout = new ISOMLayout<Vertex, Edge>(londonBikeGraph);
		layout.setSize(new Dimension(1000, 1000));
		VisualizationViewer<Vertex, Edge> vv = new VisualizationViewer<Vertex, Edge>(layout);
		vv.setPreferredSize(new Dimension(1050, 1050));

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		DefaultModalGraphMouse<Vertex, Edge> gm = new DefaultModalGraphMouse<Vertex, Edge>();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);

		JFrame frame = new JFrame("Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
}