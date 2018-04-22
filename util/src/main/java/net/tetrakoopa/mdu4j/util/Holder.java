package net.tetrakoopa.mdu4j.util;

public class Holder<E> {

	private E e;

	public Holder(E e) {
		this.e = e;
	}

	public E get() {
		return e;
	}

	public void set(E e) {
		this.e = e;
	}
}
