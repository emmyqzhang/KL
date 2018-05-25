import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class KLPartitioning {

	private Graph graph;
	private int partitionSize;

	private NodeGroup A, B;
	private NodeGroup unswappedA, unswappedB;

	public KLPartitioning() {
		this.graph = new Graph();
		HashMap<String, Node> names = new HashMap<String, Node>();
		InputStream is = this.getClass().getResourceAsStream("/resource/data.txt");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int i = 0;
		Node node = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				i++;
				if (line.trim() != "") {
					if (i == 1) {
						String[] nodes = line.split(",");
						for (String name : nodes) {
							node = new Node(name);
							graph.addNode(node);
							names.put(name, node);
						}
					} else if (i == 2) {
						String[] edges = line.split(",");
						String[] items = null;
						for (String edge : edges) {
							items = edge.split(":");
							Double weight = Double.parseDouble(items[2]);
							graph.addEdge(new Edge(weight), names.get(items[0]), names.get(items[1]));
						}
					} else {
						break;
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void process() {
		this.partitionSize = this.graph.getNodes().size() / 2;

		if (this.graph.getNodes().size() != partitionSize * 2)
			throw new RuntimeException("Size of nodes must be even");

		A = new NodeGroup();
		B = new NodeGroup();

		int i = 0;
		for (Node v : this.graph.getNodes()) {
			(++i > partitionSize ? B : A).add(v);
		}
		unswappedA = new NodeGroup(A);
		unswappedB = new NodeGroup(B);

		doAllSwaps();
	}

	public NodeGroup getGroupA() {
		return A;
	}

	public NodeGroup getGroupB() {
		return B;
	}

	public Graph getGraph() {
		return graph;
	}

	private void doAllSwaps() {

		LinkedList<Pair<Node>> swaps = new LinkedList<Pair<Node>>();
		double minCost = Double.POSITIVE_INFINITY;
		int minId = -1;

		for (int i = 0; i < partitionSize; i++) {
			double cost = doSingleSwap(swaps);
			if (cost < minCost) {
				minCost = cost;
				minId = i;
			}
		}

		while (swaps.size() - 1 > minId) {
			Pair<Node> pair = swaps.pop();
			swapNodes(A, pair.second, B, pair.first);
		}
	}
	private static void swapNodes(NodeGroup a, Node va, NodeGroup b, Node vb) {
		if (!a.contains(va) || a.contains(vb) || !b.contains(vb) || b.contains(va))
			throw new RuntimeException("Invalid swap");
		a.remove(va);
		a.add(vb);
		b.remove(vb);
		b.add(va);
	}

	private double doSingleSwap(Deque<Pair<Node>> swaps) {

		Pair<Node> maxPair = null;
		double maxGain = Double.NEGATIVE_INFINITY;

		for (Node v_a : unswappedA) {
			for (Node v_b : unswappedB) {

				Edge e = graph.findEdge(v_a, v_b);
				double edge_cost = (e != null) ? e.weight : 0;
				double gain = getNodeCost(v_a) + getNodeCost(v_b) - 2 * edge_cost;

				if (gain > maxGain) {
					maxPair = new Pair<Node>(v_a, v_b);
					maxGain = gain;
				}

			}
		}

		swapNodes(A, maxPair.first, B, maxPair.second);
		swaps.push(maxPair);
		unswappedA.remove(maxPair.first);
		unswappedB.remove(maxPair.second);

		return getCutCost();
	}


	private double getNodeCost(Node v) {

		double cost = 0;

		boolean v1isInA = A.contains(v);

		for (Node v2 : graph.getNeighbors(v)) {

			boolean v2isInA = A.contains(v2);
			Edge edge = graph.findEdge(v, v2);

			if (v1isInA != v2isInA) // external
				cost += edge.weight;
			else
				cost -= edge.weight;
		}
		return cost;
	}

	private double getCutCost() {
		double cost = 0;

		for (Edge edge : graph.getEdges()) {
			Pair<Node> endpoints = graph.getEndpoints(edge);

			boolean firstInA = A.contains(endpoints.first);
			boolean secondInA = A.contains(endpoints.second);

			if (firstInA != secondInA) // external
				cost += edge.weight;
		}
		return cost;
	}

	public static void main(String[] args) {
		KLPartitioning partitioning = new KLPartitioning();
		partitioning.process();

		System.out.print("Group A:   ");
		for (Node x : partitioning.getGroupA()) {
			System.out.print(x);
			System.out.print("  ");
		}
		System.out.print("\nGroup B:   ");
		for (Node x : partitioning.getGroupB()) {
			System.out.print(x);
			System.out.print("  ");
		}
		System.out.println("\nCut cost:   " + partitioning.getCutCost());
	}
}
