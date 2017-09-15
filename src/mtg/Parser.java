package mtg;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.ImmutableSet;
import com.google.gson.GsonBuilder;

import mtg.Card.Flip;

public class Parser {

	private static final Set<String> BASIC =
			ImmutableSet.of("Plains", "Island", "Swamp", "Mountain", "Forest");

	public static void main(String[] args) throws IOException {

		Map<String, Card> cards = new TreeMap<>();
		Document document = Jsoup
				.connect("https://magic.wizards.com/en/articles/archive/card-image-gallery/ixalan")
				.get();
		for (Element cardBlock : document.select("div.activecardblock")) {
			for (Element container : cardBlock.select("*.rtecenter")) {
				Elements images = container.select("img[src^=https://media.wizards.com/2017/xln");
				// if (images.size() == 1) {
				// System.out.println(images.get(0).attr("alt"));
				// } else if (images.size() == 2){
				// System.out.println(images.get(0).attr("alt") + " -> " +
				// images.get(1).attr("alt"));
				// } else {
				// throw new IllegalStateException(container.toString());
				// }
				if (images.isEmpty()) {
					throw new IllegalStateException(container.toString());
				}

				String name = images.get(0).attr("alt").replace('’', '\'');
				if (BASIC.contains(name)) {
					continue;
				}
				Card card = new Card();
				card.name = name;
				card.image = images.get(0).attr("src");
				if (images.size() == 2) {
					Flip flip = new Flip();
					flip.name = images.get(1).attr("alt").replace('’', '\'');
					flip.image = images.get(1).attr("src");
					card.flip = flip;
				}
				cards.put(name, card);
			}
		}

		List<Card> result = new ArrayList<>();
		Pattern pattern = Pattern.compile("(.+?) {2,}([WUBRGCML]) ([CURMSL]) (\\d+|X) (Y|N)");
		for (String line : Files.readAllLines(Paths.get("xln.txt"), StandardCharsets.UTF_8)) {
			Matcher matcher = pattern.matcher(line.trim());
			if (!matcher.matches()) {
				throw new IllegalArgumentException("no match: " + line);
			}
			Card card = cards.remove(matcher.group(1));
			if (card == null) {
				throw new IllegalArgumentException("no card: " + matcher.group(1));
			}
			card.color = matcher.group(2);
			card.rarity = matcher.group(3);
			card.manaCost =
					matcher.group(4).equals("X") ? null : Integer.parseInt(matcher.group(4));
			card.type = matcher.group(5).equals("Y");
			result.add(card);
		}
		System.out.println(new GsonBuilder().disableHtmlEscaping().create().toJson(result));
		System.out.println("MISSING: " + cards.keySet());
	}

}
