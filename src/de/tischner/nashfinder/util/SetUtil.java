package de.tischner.nashfinder.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class SetUtil {

	public static <K> Set<Set<K>> cartesianProduct(final List<Set<K>> sets) {
		if (sets.size() < 2) {
			throw new IllegalArgumentException(
					"Can not build a product of fewer than two sets (got " + sets.size() + ")");
		}
		return cartesianProductHelper(0, sets);
	}

	public static <K> Set<Set<K>> cartesianProduct(final Set<K> set) {
		ArrayList<Set<K>> sets = new ArrayList<>(2);
		sets.add(set);
		sets.add(set);
		return cartesianProduct(sets);
	}

	public static <K> Set<Set<K>> powerSet(Set<K> set) {
		Set<Set<K>> sets = new LinkedHashSet<>();
		if (set.isEmpty()) {
			// Abort with the empty set
			sets.add(new LinkedHashSet<K>());
			return sets;
		}
		List<K> list = new ArrayList<K>(set);
		// Fix the head and add the other results with and without the head
		K head = list.get(0);
		Set<K> rest = new LinkedHashSet<K>(list.subList(1, list.size()));
		for (Set<K> otherSet : powerSet(rest)) {
			Set<K> currentSet = new LinkedHashSet<K>();
			currentSet.add(head);
			currentSet.addAll(otherSet);
			sets.add(currentSet);
			sets.add(otherSet);
		}
		return sets;
	}

	private static <K> Set<Set<K>> cartesianProductHelper(final int index, final List<Set<K>> sets) {
		Set<Set<K>> result = new LinkedHashSet<>();
		if (index == sets.size()) {
			// Abort with the empty set
			result.add(new LinkedHashSet<>());
		} else {
			// Fix the set at index and take the product with all other results
			for (K element : sets.get(index)) {
				for (Set<K> set : cartesianProductHelper(index + 1, sets)) {
					set.add(element);
					result.add(set);
				}
			}
		}
		return result;
	}

	/**
	 * Utility class. No implementation.
	 */
	private SetUtil() {

	}
}
