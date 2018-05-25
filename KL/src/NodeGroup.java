import java.util.HashSet;

public class NodeGroup extends HashSet<Node> {
	private static final long serialVersionUID = 263765800950142647L;

	public NodeGroup(HashSet<Node> clone) {
		super(clone);
	}

	public NodeGroup() {
	}
}
