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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import sodekovs.graphanalysis.database.DatabaseConnection;

/**
 * @author thomas
 * 
 */
public class GraphBuilder2 {

	private static Connection connection = DatabaseConnection.getConnection();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphBuilder2 gb = new GraphBuilder2();

		String link = null;
		String city = null;

		link = "BY_BIKE";
		city = "London";
		ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> londonGraphBike = gb.fetchData(link, city);
		if (londonGraphBike != null && gb.validate(londonGraphBike, link, city)) {
			gb.printEdges(londonGraphBike, new File("graphanalysis2/london_by_bike.txt"));
			gb.serialize(londonGraphBike, new File("graphanalysis2/london_by_bike.ser"));
		}

		link = "BY_BIKE";
		city = "Washington";
		ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> washingtonGraphBike = gb.fetchData(link, city);
		if (washingtonGraphBike != null && gb.validate(washingtonGraphBike, link, city)) {
			gb.printEdges(washingtonGraphBike, new File("graphanalysis2/washington_by_bike.txt"));
			gb.serialize(washingtonGraphBike, new File("graphanalysis2/washington_by_bike.ser"));
		}

		link = "BY_TRUCK";
		city = "London";
		ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> londonGraphTruck = gb.fetchData(link, city);
		if (londonGraphTruck != null && gb.validate(londonGraphTruck, link, city)) {
			gb.printEdges(londonGraphTruck, new File("graphanalysis2/london_by_truck.txt"));
			gb.serialize(londonGraphTruck, new File("graphanalysis2/london_by_truck.ser"));
		}

		link = "BY_TRUCK";
		city = "Washington";
		ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> washingtonGraphTruck = gb.fetchData(link, city);
		if (washingtonGraphTruck != null && gb.validate(washingtonGraphTruck, link, city)) {
			gb.printEdges(washingtonGraphTruck, new File("graphanalysis2/washington_by_truck.txt"));
			gb.serialize(washingtonGraphTruck, new File("graphanalysis2/washington_by_truck.ser"));
		}
	}

	public ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> fetchData(String link, String city) {
		ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph = new ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);

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

				DefaultWeightedEdge edge = graph.getEdge(startNode, endNode);
				if (edge != null) {
					double weight = graph.getEdgeWeight(edge);
					weight++;
					graph.setEdgeWeight(edge, weight);
				} else {
					edge = new DefaultWeightedEdge();
					graph.addVertex(startNode);
					graph.addVertex(endNode);
					graph.addEdge(startNode, endNode, edge);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return graph;
	}

	public void printEdges(final ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph, File file) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));

			List<DefaultWeightedEdge> edges = new ArrayList<DefaultWeightedEdge>(graph.edgeSet());
			Collections.sort(edges, new Comparator<DefaultWeightedEdge>() {

				@Override
				public int compare(DefaultWeightedEdge o1, DefaultWeightedEdge o2) {
					double weight1 = graph.getEdgeWeight(o1);
					double weight2 = graph.getEdgeWeight(o2);

					return new Double(weight1).compareTo(new Double(weight2));
				}
			});
			Collections.reverse(edges);

			for (DefaultWeightedEdge edge : edges) {
				Vertex startNode = graph.getEdgeSource(edge);
				Vertex endNode = graph.getEdgeTarget(edge);

				bw.write(startNode + " --> " + endNode + ": " + graph.getEdgeWeight(edge) + "\n");
			}
			System.out.println("Finished writing file " + file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean validate(ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph, String link, String city) {
		int edgeWeightSum = 0;
		for (DefaultWeightedEdge edge : graph.edgeSet()) {
			edgeWeightSum += graph.getEdgeWeight(edge);
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

	public void serialize(ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph, File file) {
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
	public ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> deserialize(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph = (ListenableDirectedWeightedGraph<Vertex, DefaultWeightedEdge>) ois.readObject();

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
}
