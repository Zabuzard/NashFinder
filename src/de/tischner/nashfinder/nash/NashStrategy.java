package de.tischner.nashfinder.nash;

import java.util.LinkedHashMap;
import java.util.Map;

import de.tischner.nashfinder.locale.ErrorMessages;

/**
 * A nash strategy specifies, for a player, which actions he should play with
 * what probability, so that it results in a nash equilibrium.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <ACTION>
 *            Class of the actions
 */
public final class NashStrategy<ACTION> {

	/**
	 * Inclusive lower bound for probabilities.
	 */
	private static final int PROBABILITY_LOWER_BOUND = 0;
	/**
	 * Inclusive upper bound for probabilities.
	 */
	private static final int PROBABILITY_UPPER_BOUND = 1;

	/**
	 * Data structure that allows a fast access to the probability for a given
	 * action.
	 */
	private final Map<ACTION, Number> mActionToProbability;

	/**
	 * Creates a new empty nash strategy.
	 */
	public NashStrategy() {
		this.mActionToProbability = new LinkedHashMap<>();
	}

	/**
	 * Adds an action with a given probability to the strategy.
	 * 
	 * @param action
	 *            Action to add
	 * @param probability
	 *            The probability of the given action between <tt>0</tt> and
	 *            <tt>1</tt> (both inclusive)
	 * @throws IllegalArgumentException
	 *             If the given probability is not between <tt>0</tt> and
	 *             <tt>1</tt> (both inclusive)
	 */
	public void addAction(final ACTION action, final Number probability) {
		if (probability.doubleValue() < PROBABILITY_LOWER_BOUND
				|| probability.doubleValue() > PROBABILITY_UPPER_BOUND) {
			throw new IllegalArgumentException(ErrorMessages.PROBABILITY_EXCEEDS_LIMITS + " Got: " + probability);
		}
		this.mActionToProbability.put(action, probability);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NashStrategy)) {
			return false;
		}
		NashStrategy<?> other = (NashStrategy<?>) obj;
		if (this.mActionToProbability == null) {
			if (other.mActionToProbability != null) {
				return false;
			}
		} else if (!this.mActionToProbability.equals(other.mActionToProbability)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the probability of the given action.
	 * 
	 * @param action
	 *            Action to get the probability for
	 * @return The probability of the given action between <tt>0</tt> and
	 *         <tt>1</tt> (both inclusive)
	 */
	public Number getActionProbability(final ACTION action) {
		return this.mActionToProbability.get(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mActionToProbability == null) ? 0 : this.mActionToProbability.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.mActionToProbability.toString();
	}
}
