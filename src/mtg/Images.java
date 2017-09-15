package mtg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.CharMatcher;

public class Images {

	public static void main(String[] args) throws IOException {

		Document document = Jsoup
				.connect("https://magic.wizards.com/en/articles/archive/card-image-gallery/ixalan")
				.get();
		Files.createDirectories(Paths.get("images"));
		for (Element cardBlock : document.select("div.activecardblock")) {
			for (Element container : cardBlock.select("*.rtecenter")) {
				for (Element image : container
						.select("img[src^=https://media.wizards.com/2017/xln")) {
					Files.write(Paths.get("images", toFilename(image.attr("alt")) + ".png"),
							Jsoup.connect(image.attr("src"))
									.ignoreContentType(true).execute().bodyAsBytes());
				}
			}
		}
	}

	private static final CharMatcher ALLOWED =
			CharMatcher.inRange('a', 'z').or(CharMatcher.is('_'));
	private static final CharMatcher TO_UNDERSCORE = CharMatcher.anyOf(" -");

	private static String toFilename(String cardName) {
		return ALLOWED.retainFrom(TO_UNDERSCORE.replaceFrom(cardName.toLowerCase(), '_'));
	}

}
