import java.util.*;

public class TransitionImpl implements Transition {
	private String name;
	private int xpos;
	private int ypos;
	private Set<Place> preset;
	private Set<Place> postset;

	public TransitionImpl(String n, int x, int y) {
		this.name = n;
		this.xpos = x;
		this.ypos = y;
		this.preset = new HashSet<Place>();
		this.postset = new HashSet<Place>();
	}
	public Set<Place> preSet() {
		return this.preset;
	}
	public Set<Place> postSet() {
		return this.postset;
	}
	public void moveBy(int dx, int dy) {
		this.xpos += dx;
		this.ypos += dy;
	}
	public String getName() {
		return this.name;
	}
	public int getX() {
		return this.xpos;
	}
	public int getY() {
		return this.ypos;
	}
	public String toString() {
		return this.name + "(" + this.xpos + "," + this.ypos + ")";
	}
	public boolean equals(Transition t) {
		return this.name.equals(t.getName());
	}
	public void addListener(TransitionListener pl) {
		//
	}
	public void removeListener(TransitionListener pl) {
		//
	}
}
