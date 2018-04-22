package net.tetrakoopa.mdu4j.test.things;

import java.util.Date;

public class Truc {

	private Long id;

	private String name;

	private Date birthDate;

	private String bestMove;

	private Bidule firstBiduleEver;

	private Foobar foobar;

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

	public String getBestMove() {
		return bestMove;
	}

	public void setBestMove(String bestMove) {
		this.bestMove = bestMove;
	}

	public Bidule getFirstBiduleEver() {
		return firstBiduleEver;
	}

	public void setFirstBiduleEver(Bidule firstBiduleEver) {
		this.firstBiduleEver = firstBiduleEver;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Foobar getFoobar() {
		return foobar;
	}

	public void setFoobar(Foobar foobar) {
		this.foobar = foobar;
	}
}
