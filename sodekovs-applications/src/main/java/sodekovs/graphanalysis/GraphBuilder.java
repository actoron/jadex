/**
 * 
 */
package sodekovs.graphanalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sodekovs.graphanalysis.database.DatabaseConnection;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

/**
 * @author thomas
 * 
 */
public class GraphBuilder {

	private static Connection connection = DatabaseConnection.getConnection();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphBuilder gb = new GraphBuilder();

		String link = null;
		String city = null;

		link = "BY_BIKE";
		city = "London";
		DirectedSparseMultigraph<Vertex, Edge> londonGraphBike = gb.fetchData(link, city);
		if (londonGraphBike != null && gb.validate(londonGraphBike, link, city)) {
			gb.printEdges(londonGraphBike, new File("graphanalysis/london_by_bike.txt"));
			gb.serialize(londonGraphBike, new File("graphanalysis/london_by_bike.ser"));
		}

		link = "BY_BIKE";
		city = "Washington";
		DirectedSparseMultigraph<Vertex, Edge> washingtonGraphBike = gb.fetchData(link, city);
		if (washingtonGraphBike != null && gb.validate(washingtonGraphBike, link, city)) {
			gb.printEdges(washingtonGraphBike, new File("graphanalysis/washington_by_bike.txt"));
			gb.serialize(washingtonGraphBike, new File("graphanalysis/washington_by_bike.ser"));
		}

		link = "BY_TRUCK";
		city = "London";
		DirectedSparseMultigraph<Vertex, Edge> londonGraphTruck = gb.fetchData(link, city);
		if (londonGraphTruck != null && gb.validate(londonGraphTruck, link, city)) {
			gb.printEdges(londonGraphTruck, new File("graphanalysis/london_by_truck.txt"));
			gb.serialize(londonGraphTruck, new File("graphanalysis/london_by_truck.ser"));
		}

		link = "BY_TRUCK";
		city = "Washington";
		DirectedSparseMultigraph<Vertex, Edge> washingtonGraphTruck = gb.fetchData(link, city);
		if (washingtonGraphTruck != null && gb.validate(washingtonGraphTruck, link, city)) {
			gb.printEdges(washingtonGraphTruck, new File("graphanalysis/washington_by_truck.txt"));
			gb.serialize(washingtonGraphTruck, new File("graphanalysis/washington_by_truck.ser"));
		}
	}

	public DirectedSparseMultigraph<Vertex, Edge> fetchData(String link, String city) {
		DirectedSparseMultigraph<Vertex, Edge> graph = new DirectedSparseMultigraph<Vertex, Edge>();
		int edgeId = 1;

		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT startStation, endStation FROM rental WHERE link LIKE ? and city LIKE ?");
			stmt.setString(1, link);
			stmt.setString(2, city);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String startStation = rs.getString("startStation");
				String endStation = rs.getString("endStation");

				Vertex startNode = new Vertex(startStation);
				Vertex endNode = new Vertex(endStation);

				Edge edge = graph.findEdge(startNode, endNode);
				if (edge != null) {
					edge.incrementWeight();
				} else {
					edge = new Edge(edgeId++);
					graph.addEdge(edge, startNode, endNode);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return graph;
	}

	public void printEdges(DirectedSparseMultigraph<Vertex, Edge> graph, File file) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));

			List<Edge> edges = new ArrayList<Edge>(graph.getEdges());
			Collections.sort(edges);
			Collections.reverse(edges);

			for (Edge edge : edges) {
				Vertex startNode = graph.getSource(edge);
				Vertex endNode = graph.getDest(edge);

				bw.write(startNode + " --> " + endNode + ": " + edge.getWeight() + "\n");
			}
			System.out.println("Finished writing file " + file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean validate(DirectedSparseMultigraph<Vertex, Edge> graph, String link, String city) {
		int edgeWeightSum = 0;
		for (Edge edge : graph.getEdges()) {
			edgeWeightSum += edge.getWeight();
		}

		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT count(*) from rental WHERE link LIKE ? and city LIKE ?");
			stmt.setString(1, link);
			stmt.setString(2, city);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int result = rs.getInt(1);

				return result == edgeWeightSum;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void serialize(DirectedSparseMultigraph<Vertex, Edge> graph, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(graph);

			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public DirectedSparseMultigraph<Vertex, Edge> deserialize(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			DirectedSparseMultigraph<Vertex, Edge> graph = (DirectedSparseMultigraph<Vertex, Edge>) ois.readObject();

			return graph;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public DirectedSparseMultigraph<Vertex, Edge> removeEdges(DirectedSparseMultigraph<Vertex, Edge> graph, int weight) {
		List<Edge> removableEdges = new ArrayList<Edge>();
		for (Edge edge : graph.getEdges()) {
			if (edge.getWeight() <= weight) {
				removableEdges.add(edge);
			}
		}
		for (Edge edge : removableEdges) {
			graph.removeEdge(edge);
		}

		List<Vertex> removableVertices = new ArrayList<Vertex>();
		for (Vertex vertex : graph.getVertices()) {
			Collection<Vertex> neighors = graph.getNeighbors(vertex);

			if (neighors == null || neighors.isEmpty()) {
				removableVertices.add(vertex);
			}
		}
		for (Vertex vertex : removableVertices) {
			graph.removeVertex(vertex);
		}

		return graph;
	}
}
