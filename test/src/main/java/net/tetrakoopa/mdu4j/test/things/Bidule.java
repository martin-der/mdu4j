package net.tetrakoopa.mdu4j.test.things;

public class Bidule {

	private Long id;

	private String name;

	private float ratio;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	};

	public String toString() {
		return "{ id:" + id + ", name:" + name + " }";
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

}
