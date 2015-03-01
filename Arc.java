public class Arc {
	private Place place;
	private Transition transition;
	private int weight;

	public Arc(Place p, Transition t, int w) {
		this.place = p;
		this.transition = t;
		this.weight = w;
	}

	public Arc(Transition t, Place p, int w) {
		this.transition = t;
		this.place = p;
		this.weight = w;
	}

	public boolean equals(Arc a) {
		if (a.getPlace().equals(this.place) && a.getTransition().equals(this.transition)) {
			this.weight += a.getWeight();
			return true;
		}
		return false;
	}

	public Place getPlace() {
		return this.place;
	}
	public Transition getTransition() {
		return this.transition;
	}
	public int getWeight() {
		return this.weight;
	}
}
