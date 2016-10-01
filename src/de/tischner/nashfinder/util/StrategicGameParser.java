package de.tischner.nashfinder.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.tischner.nashfinder.game.StrategicGame;
import de.tischner.nashfinder.game.util.ActionProfile;
import de.tischner.nashfinder.locale.ErrorMessages;
import jdk.nashorn.internal.runtime.JSONListAdapter;

/**
 * Utility class that provides methods for parsing
 * {@link de.tischner.nashfinder.game.StrategicGame StrategicGames} out of
 * various sources.
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class StrategicGameParser {

	/**
	 * Key in the json format for the actions.
	 */
	private static final String JSON_KEY_ACTIONS = "Actions";
	/**
	 * Key in the json format for the payoffs.
	 */
	private static final String JSON_KEY_PAYOFFS = "Values";
	/**
	 * Key in the json format for the players.
	 */
	private static final String JSON_KEY_PLAYERS = "Agents";
	/**
	 * Scripting engine command to convert the given object to a java usable
	 * json object.
	 */
	private static final String SCRIPT_CMD_TO_JAVA_JSON = "Java.asJSONCompatible";
	/**
	 * Script engine to use for parsing json objects.
	 */
	private static final String SCRIPT_ENGINE = "javascript";

	/**
	 * Parses a strategic game out of a json file.
	 * 
	 * @param file
	 *            File in the json format that contains the game to parse
	 * @return The parsed game
	 */
	@SuppressWarnings("unchecked")
	public static StrategicGame<String, String> parseStrategicGameJson(final File file) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByName(SCRIPT_ENGINE);

		Map<String, Object> contents = null;
		try {
			String json = new String(Files.readAllBytes(file.toPath()));
			String script = SCRIPT_CMD_TO_JAVA_JSON + "(" + json + ")";
			Object result = engine.eval(script);
			if (result instanceof Map<?, ?>) {
				contents = (Map<String, Object>) result;
			}
		} catch (IOException | ScriptException e) {
			exitJsonParseError();
		}
		if (contents == null || contents.isEmpty()) {
			exitJsonParseError();
		}

		return parseStrategicGameJson(contents);
	}

	/**
	 * Parses a strategic game out of a json object, created by the javascript
	 * engine nashorn.
	 * 
	 * @param json
	 *            Json object, created by the javascript engine nashorn, that
	 *            contains the game to parse
	 * @return The parsed game
	 */
	public static StrategicGame<String, String> parseStrategicGameJson(final Map<String, Object> json) {
		if (json == null || json.isEmpty() || !json.containsKey(JSON_KEY_PLAYERS) || !json.containsKey(JSON_KEY_ACTIONS)
				|| !json.containsKey(JSON_KEY_PAYOFFS) || !(json.get(JSON_KEY_PLAYERS) instanceof JSONListAdapter)
				|| !(json.get(JSON_KEY_ACTIONS) instanceof JSONListAdapter)
				|| !(json.get(JSON_KEY_PAYOFFS) instanceof JSONListAdapter)) {
			exitJsonParseError();
		}

		StrategicGame<String, String> game = new StrategicGame<>();

		// Add players
		JSONListAdapter players = (JSONListAdapter) json.get(JSON_KEY_PLAYERS);
		for (Object player : players) {
			if (!(player instanceof String)) {
				exitJsonParseError();
			}
			String playerAsString = (String) player;
			game.addPlayer(playerAsString);
		}

		// Add actions
		JSONListAdapter actions = (JSONListAdapter) json.get(JSON_KEY_ACTIONS);
		if (actions.size() != players.size()) {
			exitJsonParseError();
		}
		for (int i = 0; i < actions.size(); i++) {
			Object actionsForPlayer = actions.get(i);
			if (!(actionsForPlayer instanceof JSONListAdapter)) {
				exitJsonParseError();
			}
			JSONListAdapter actionsForPlayerAsList = (JSONListAdapter) actionsForPlayer;

			String currentPlayer = (String) players.get(i);
			for (Object actionForPlayer : actionsForPlayerAsList) {
				if (!(actionForPlayer instanceof String)) {
					exitJsonParseError();
				}
				String actionForPlayerAsString = (String) actionForPlayer;

				game.addAction(actionForPlayerAsString, currentPlayer);
			}
		}

		// Add payoffs
		if (actions.size() != 2) {
			// At this point, we only allow a player size of two.
			// This is because the payoff matrix for greater values would not be
			// representable by the given format (2x2).
			exitJsonParseError();
		}
		JSONListAdapter values = (JSONListAdapter) json.get(JSON_KEY_PAYOFFS);
		JSONListAdapter actionsOfFirstPlayer = (JSONListAdapter) actions.get(0);
		JSONListAdapter actionsOfSecondPlayer = (JSONListAdapter) actions.get(1);
		for (int i = 0; i < actionsOfFirstPlayer.size(); i++) {
			for (int j = 0; j < actionsOfSecondPlayer.size(); j++) {
				Object matrixRow = values.get(i);
				if (!(matrixRow instanceof JSONListAdapter)) {
					exitJsonParseError();
				}
				JSONListAdapter matrixRowAsList = (JSONListAdapter) matrixRow;
				Object matrixEntry = matrixRowAsList.get(j);
				if (!(matrixEntry instanceof JSONListAdapter)) {
					exitJsonParseError();
				}
				JSONListAdapter matrixEntryAsList = (JSONListAdapter) matrixEntry;

				List<Integer> payoffAsList = new LinkedList<Integer>();
				for (Object singlePayoff : matrixEntryAsList) {
					if (!(singlePayoff instanceof Integer)) {
						exitJsonParseError();
					}
					payoffAsList.add((Integer) singlePayoff);
				}

				ActionProfile<String> actionProfile = new ActionProfile<>();
				actionProfile.addAction((String) actionsOfFirstPlayer.get(i));
				actionProfile.addAction((String) actionsOfSecondPlayer.get(j));

				game.addPayoff(payoffAsList, actionProfile);
			}
		}

		return game;
	}

	/**
	 * Parses a strategic game out of a json file.
	 * 
	 * @param filePath
	 *            Path to the file in the json format that contains the game to
	 *            parse
	 * @return The parsed game
	 */
	public static StrategicGame<String, String> parseStrategicGameJson(final String filePath) {
		return parseStrategicGameJson(new File(filePath));
	}

	/**
	 * Exits the method by throwing an {@link IllegalArgumentException} because
	 * there occurred a parse error.
	 * 
	 * @throws IllegalArgumentException
	 *             This method always throws this exception
	 */
	private static void exitJsonParseError() {
		throw new IllegalArgumentException(ErrorMessages.JSON_PARSE_ERROR);
	}

	/**
	 * Utility class. No implementation.
	 */
	private StrategicGameParser() {

	}
}
