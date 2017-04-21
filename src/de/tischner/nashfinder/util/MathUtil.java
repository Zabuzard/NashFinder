package de.tischner.nashfinder.util;

import java.math.BigDecimal;

/**
 * Class that provides utility methods for math.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class MathUtil {
	/**
	 * Rounds a given number to a given decimal scale by using the
	 * {@link BigDecimal#ROUND_HALF_DOWN} strategy.
	 * 
	 * @param number
	 *            Number to round
	 * @param decimalScale
	 *            Decimal scale to round to
	 * @return The rounded number
	 */
	public static Number roundNumberTo(final Number number, final int decimalScale) {
		BigDecimal decimal = new BigDecimal(number.doubleValue());
		decimal = decimal.setScale(decimalScale, BigDecimal.ROUND_HALF_DOWN);
		return Double.valueOf(decimal.doubleValue());
	}

	/**
	 * Utility class. No implementation.
	 */
	private MathUtil() {

	}
}
