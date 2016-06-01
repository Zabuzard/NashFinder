package de.zabuza.nashfinder.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.zabuza.nashfinder.game.StrategicGame;
import de.zabuza.nashfinder.game.util.ActionProfile;
import jdk.nashorn.internal.runtime.JSONListAdapter;

/**
 * 
 * @author Daniel Tischner
 *
 */
public final class StrategicGameParser {

	@SuppressWarnings("unchecked")
	public static StrategicGame<String, String> parseStrategicGameJson(final File file) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByName("javascript");

		Map<String, Object> contents = null;
		try {
			String json = new String(Files.readAllBytes(file.toPath()));
			String script = "Java.asJSONCompatible(" + json + ")";
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

	public static StrategicGame<String, String> parseStrategicGameJson(final Map<String, Object> json) {
		if (json == null || json.isEmpty() || !json.containsKey("Agents") || !json.containsKey("Actions")
				|| !json.containsKey("Values") || !(json.get("Agents") instanceof JSONListAdapter)
				|| !(json.get("Actions") instanceof JSONListAdapter)
				|| !(json.get("Values") instanceof JSONListAdapter)) {
			exitJsonParseError();
		}

		StrategicGame<String, String> game = new StrategicGame<>();

		// Add players
		JSONListAdapter players = (JSONListAdapter) json.get("Agents");
		for (Object player : players) {
			if (!(player instanceof String)) {
				exitJsonParseError();
			}
			String playerAsString = (String) player;
			game.addPlayer(playerAsString);
		}

		// Add actions
		JSONListAdapter actions = (JSONListAdapter) json.get("Actions");
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
		// TODO Extend this operation to support arbitrary amount of players
		if (actions.size() != 2) {
			exitJsonParseError();
		}
		JSONListAdapter values = (JSONListAdapter) json.get("Values");
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

	public static StrategicGame<String, String> parseStrategicGameJson(final String fileName) {
		return parseStrategicGameJson(new File(fileName));
	}

	private static void exitJsonParseError() {
		throw new IllegalArgumentException("Could not parse the json object. The format may be corrupt.");
	}

	/**
	 * Utility class. No implementation.
	 */
	private StrategicGameParser() {

	}
}
