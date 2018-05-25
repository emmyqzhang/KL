import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Graph {
	final private Map<Node, Map<Node, Edge>> nodes;
	final private Map<Edge, Pair<Node>> edges;

	public Graph() {
		nodes = new HashMap<Node, Map<Node, Edge>>();
		edges = new HashMap<Edge, Pair<Node>>();
	}

	public boolean addNode(Node v) {
		if (containsNode(v))
			return false;
		nodes.put(v, new HashMap<Node, Edge>());
		return true;
	}

	public boolean addEdge(Edge edge, Node v1, Node v2) {

		if (!containsNode(v1) || !containsNode(v2))
			return false;
		if (findEdge(v1, v2) != null)
			return false;

		Pair<Node> pair = new Pair<Node>(v1, v2);
		edges.put(edge, pair);
		nodes.get(v1).put(v2, edge);
		nodes.get(v2).put(v1, edge);

		return true;
	}

	public boolean containsNode(Node v) {
		return nodes.containsKey(v);
	}

	public boolean containsEdge(Edge e) {
		return edges.containsKey(e);
	}

	/** Finds an edge if any between v1 and v2 **/
	public Edge findEdge(Node v1, Node v2) {
		if (!containsNode(v1) || !containsNode(v2))
			return null;
		return nodes.get(v1).get(v2);
	}

	/** Gets the nodes directly connected to v **/
	public Collection<Node> getNeighbors(Node v) {
		if (!containsNode(v))
			return null;
		return nodes.get(v).keySet();
	}

	public Set<Edge> getEdges() {
		return edges.keySet();
	}

	public Set<Node> getNodes() {
		return nodes.keySet();
	}

	/** Returns a pair of nodes that connects by edge e **/
	public Pair<Node> getEndpoints(Edge e) {
		return edges.get(e);
	}

}
