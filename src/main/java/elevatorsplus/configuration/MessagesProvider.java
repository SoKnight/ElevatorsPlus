package elevatorsplus.configuration;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import elevatorsplus.ElevatorsPlus;
import lombok.Getter;
import ru.soknight.lib.configuration.Messages;

@Getter
public class MessagesProvider {

	private static final List<String> LOCALES = Arrays.asList("en", "ru");
	
	private Messages messages;
	
	public MessagesProvider(ElevatorsPlus plugin, Config config) {
		String locale = config.getString("messages.locale", "en").toLowerCase();
		
		if(!LOCALES.contains(locale)) {
			plugin.getPluginLogger().error("Unknown localization '" + locale + "', returns to English...");
			locale = "en";
		}
		
		String filename = "messages_" + locale + ".yml";
		InputStream source = plugin.getClass().getResourceAsStream("/locales/" + filename);
		
		if(source != null) {
			this.messages = new Messages(plugin, source, filename);
		} else plugin.getPluginLogger().error("Failed to get internal resource of messages file.");
	}

}
