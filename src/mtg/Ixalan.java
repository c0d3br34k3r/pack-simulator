package mtg;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Ixalan {

	public static void main(String[] args) throws IOException {

		Map<String, Card> raw = Raw.parseRaw(Paths.get("xln-raw.txt"));

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

				String name = images.get(0).attr("alt");
				Card card = raw.get(name);
				if (card == null) {
					System.out.printf("%-40s %s %s %s %s%n", name,
							colorCode(cardBlock.id().substring(3)), "", "", "");
				} else {
					System.out.printf("%-40s %s %s %s %s%n", name,
							colorCode(cardBlock.id().substring(3)), card.rarity, "?",
							card.type ? "Y" : "N");
				}

			}

		}

	}

	private static String colorCode(String color) {
		if (color.equalsIgnoreCase("blue")) {
			return "U";
		}
		return color.substring(0, 1).toUpperCase();
	}

}
