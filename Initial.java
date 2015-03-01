public class Initial {
	private Place target;
	private int weight;

	public Initial(Place p, int w) {
		this.target = p;
		this.weight = w;
	}

	public Place getTarget() {
		return this.target;
	}
	public int getWeight() {
		return this.weight;
	}

	public boolean equals(Initial i) {
		return this.target.equals(i.getTarget());
	}
}
