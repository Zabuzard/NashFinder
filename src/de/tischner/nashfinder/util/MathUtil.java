package de.tischner.nashfinder.util;

import java.math.BigDecimal;

public final class MathUtil {
	public static Number roundNumberTo(final Number number, final int decimalScale) {
		BigDecimal decimal = new BigDecimal(number.doubleValue());
		decimal = decimal.setScale(decimalScale, BigDecimal.ROUND_HALF_DOWN);
		return decimal.doubleValue();
	}

	/**
	 * Utility class. No implementation.
	 */
	private MathUtil() {

	}
}
