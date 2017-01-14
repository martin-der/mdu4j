package net.tetrakoopa.mdu4j.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Contient des méthodes utilitaires pour des factories d'object de test
 */
public class RandomUtil {
	
	/** @return une chance sur deux de renvoyer <b>true</b> */
	public static boolean random() {
		return random(0.5);
	}
	
	/** @return <b>true</b> avec une probabilité de <code>proba</code> */
	public static boolean random(final double proba) {
		return proba > Math.random();
	}
	
	/** @return un entier inclus dans [0;upper-1] */
	public static int randomInt(final int upper) {
		if (upper < 1) {
			throw new IllegalArgumentException("Upper value must be greater than Zero");
		}
		return ((int) (Math.random() * Integer.MAX_VALUE)) % upper;
	}
	
	/** @return un élément pris au hazard dans le tableau */
	public static <E> E randomElement(final E[] array) {
		final int pos = randomInt(array.length);
		return array[pos];
	}
	
	/** @return un élément pris au hazard dans la collection */
	public static <E> E randomElement(final Collection<E> collection) {
		final int wantedPos = randomInt(collection.size());
		final Iterator<E> iterator = collection.iterator();
		E element = null;
		int pos = 0;
		try {
			while ((element = iterator.next()) != null) {
				if (pos >= wantedPos) {
					return element;
				}
				pos++;
			}
		} catch (final NoSuchElementException nsex) {
			throw new IndexOutOfBoundsException("No element with index " + wantedPos);
		}
		// Should never happend
		throw new IllegalStateException("No element with index " + wantedPos);
	}
	
	/** @return plusieurs éléments pris au hazard dans le tableau */
	@SuppressWarnings("unchecked")
	public static <E> E[] randomElements(final int count, final E[] array) {
		
		if (count < 0) {
			throw new IllegalArgumentException("Count must equals or be greater than Zero");
		}
		
		if (count > array.length) {
			throw new IllegalArgumentException("Requested " + count + " element(s) from an array with " + array.length + " element(s)");
		}
		
		final int elementsCount = array.length;
		
		if (elementsCount == 0) {
			return (E[]) new Object[0];
		}
		
		final boolean[] used = new boolean[elementsCount];
		
		final Object[] elements = new Object[count];
		
		int i;
		for (i = 0; i < count; i++) {
			int n = randomInt(elementsCount);
			while (used[n]) {
				n++;
				n = n % elementsCount;
			}
			elements[i] = array[n];
		}
		
		return (E[]) elements;
	}
	
	/** @return plusieurs éléments pris au hazard dans la collection */
	public static <E> List<E> randomElements(final int count, final Collection<E> collection) {
		final List<E> reserve = new ArrayList<E>(collection);
		final List<E> elements = new ArrayList<E>();
		
		int i;
		for (i = 0; i < count; i++) {
			final E element = randomElement(reserve);
			elements.add(element);
			reserve.remove(element);
		}
		
		return elements;
	}
	
}
