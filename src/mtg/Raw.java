package mtg;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;

public class Raw {

	public static Map<String, Card> parseRaw(Path file) throws IOException {
		Map<String, Card> cards = new HashMap<>();
		Splitter splitter = Splitter.on("|").trimResults();
		for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
			List<String> parts = splitter.splitToList(line);
			Card card = new Card();
			card.name = parts.get(0);
			card.type = parts.get(1).toLowerCase().contains("creature");
			card.rarity = parts.get(2).substring(0, 1).toUpperCase();
			cards.put(card.name, card);
		}
		return cards;
	}
	
}
