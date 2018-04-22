package net.tetrakoopa.mdu4j.test.things;

public class Foobar {

	public long size;

	private int id;

	private String name;

	private boolean ready;

	private CaptainRecursive captainRecursive;

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public CaptainRecursive getCaptainRecursive() {
		return captainRecursive;
	}

	public void setCaptainRecursive(CaptainRecursive captainRecursive) {
		this.captainRecursive = captainRecursive;
	}
}
