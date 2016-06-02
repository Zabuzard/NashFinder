package de.tischner.nashfinder.nash;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Daniel Tischner
 *
 * @param <ACTION>
 */
public final class NashStrategy<ACTION> {

	private final Map<ACTION, Number> mActionToProbability;

	public NashStrategy() {
		mActionToProbability = new LinkedHashMap<>();
	}

	public void addAction(final ACTION action, final Number probability) {
		if (probability.doubleValue() < 0 || probability.doubleValue() > 1) {
			throw new IllegalArgumentException(
					"The given probability must be between zero and one (both inclusive): " + probability);
		}
		mActionToProbability.put(action, probability);
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
		if (mActionToProbability == null) {
			if (other.mActionToProbability != null) {
				return false;
			}
		} else if (!mActionToProbability.equals(other.mActionToProbability)) {
			return false;
		}
		return true;
	}

	public Number getActionProbability(final ACTION action) {
		return mActionToProbability.get(action);
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
		result = prime * result + ((mActionToProbability == null) ? 0 : mActionToProbability.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mActionToProbability.toString();
	}
}
