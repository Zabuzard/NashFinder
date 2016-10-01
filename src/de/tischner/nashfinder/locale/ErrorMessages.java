package de.tischner.nashfinder.locale;

/**
 * Utility class that provides error messages for the nash finder.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 * 
 */
public final class ErrorMessages {
	/**
	 * Thrown when a {@link de.tischner.nashfinder.game.StrategicGame
	 * StrategicGame} is in an illegal state while building support sets in
	 * {@link de.tischner.nashfinder.NashFinder#computeNashEquilibria()
	 * NashFinder#computeNashEquilibria()}.
	 */
	public static final String BUILD_SUPPORT_SETS_GAME_INVALID = "Could not built support sets. The given game may be corrupt.";
	/**
	 * Thrown when trying to build the cartesian product of fewer than two sets.
	 */
	public static final String CARTESIAN_PRODUCT_OF_FEWER_TWO = "Can not build a product of fewer than two sets.";
	/**
	 * Thrown when the payoff list argument in
	 * {@link de.tischner.nashfinder.game.StrategicGame#addPayoff(java.util.List, de.tischner.nashfinder.game.util.ActionProfile)
	 * StrategicGame#addPayoff(List, ActionProfile)} is in an illegal state.
	 */
	public static final String GAME_ADD_PAYOFF_ILLEGAL_PAYOFF = "Could not add payoff. The size of payoff must be equal to the amount of players and the size of the action profile.";
	/**
	 * Thrown when a {@link de.tischner.nashfinder.game.StrategicGame
	 * StrategicGame} could not find a given payoff.
	 */
	public static final String GAME_PAYOFF_NOT_FOUND = "Could not find the payoff for the given player. The internal structures may be corrupt.";
	/**
	 * Thrown when an error occurred while parsing a json file.
	 */
	public static final String JSON_PARSE_ERROR = "Could not parse the json object. The format may be corrupt.";
	/**
	 * Thrown when
	 * {@link de.tischner.nashfinder.nash.NashEquilibrium#extractFromLcpResults(net.sf.javailp.Result, de.tischner.nashfinder.game.StrategicGame)
	 * NashEquilibrium#extractFromLcpResults(Result, StrategicGame)} could not
	 * extract a equilibrium from the results.
	 */
	public static final String NASH_EQUILIBRIUM_COULD_NOT_EXTRACT_LCP = "Could not extract results. The given game may be corrupt.";
	/**
	 * Thrown when a probability exceeds its limits <tt>0</tt> and </tt> (both
	 * inclusive).
	 */
	public static final String PROBABILITY_EXCEEDS_LIMITS = "The given probability must be between zero and one (both inclusive).";
	/**
	 * Thrown when {@link de.tischner.nashfinder.SolveGame#main(String[])
	 * SolveGame#main(String[])} gets called with the wrong number of arguments.
	 */
	public static final String SOLVE_GAME_WRONG_ARGUMENT_NUMBER = "Wrong number of arguments. The first argument is not optional and specifies the game file to use for computation.";
	/**
	 * Thrown when a {@link de.tischner.nashfinder.game.util.SupportSet
	 * SupportSet} is in an illegal state.
	 */
	public static final String SUPPORT_SET_INVALID = "The given support set is not valid, it may be corrupt.";
	/**
	 * Thrown when trying to parse a support set in
	 * {@link de.tischner.nashfinder.util.SupportSetParser SupportSetParser} but
	 * the given string is in the wrong format.
	 */
	public static final String SUPPORT_SET_PARSE_ERROR_FORMAT = "Can not parse support sets. The support set may be in the wrong format.";
	/**
	 * Thrown when trying to parse a support set in
	 * {@link de.tischner.nashfinder.util.SupportSetParser SupportSetParser} but
	 * the amount of players does not match the amount of found support sets.
	 */
	public static final String SUPPORT_SET_PARSE_ERROR_PLAYER_SIZE = "Can not parse support sets. The size of players does not match the amount of found support sets. But every support set needs to be assigned to a player nd vice versa.";

	/**
	 * Utility class. No implementation.
	 */
	private ErrorMessages() {

	}
}
