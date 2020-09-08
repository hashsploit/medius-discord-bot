package net.hashsploit.mediusdiscordbot;

import java.util.HashSet;

import javax.security.auth.login.LoginException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.hashsploit.mediusdiscordbot.commands.ClearCommand;
import net.hashsploit.mediusdiscordbot.commands.HelpCommand;
import net.hashsploit.mediusdiscordbot.commands.ShutdownCommand;

public class MediusBot {

	private static final Logger logger = LoggerFactory.getLogger(MediusBot.class);
	public static final String NAME = "Medius Discord Bot";
	public static final String VERSION = "0.1.1";
	private static MediusBot bot;
	
	public MediusBotConfig config;
	public HashSet<Command> commands;
	public JDA jda;

	public MediusBot(JSONObject json) {
		
		if (bot != null) {
			return;
		}
		
		bot = this;
		logger.info("Starting " + NAME + " v" + VERSION);
		
		try {
			this.config = new MediusBotConfig(json);
		} catch (JSONException e) {
			logger.error("Error while loading configuration.");
			e.printStackTrace();
			return;
		}
		
		this.commands = new HashSet<Command>();
		
		// Initialize commands
		commands.add(new HelpCommand());
		commands.add(new ShutdownCommand());
		commands.add(new ClearCommand());
		
		// Initialize JDA
		try {
			jda = JDABuilder.createLight(config.getToken())
				// Enable automatic clean-up
				.setEnableShutdownHook(true)
				// Enable the bulk delete event
				.setBulkDeleteSplittingEnabled(false)
				// Build
				.build();
			jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(config.getPrefix() + "help"));
			jda.addEventListener(new MessageListener());
			
		} catch (LoginException | JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gracefully shutdown the bot.
	 */
	public void shutdown() {
		jda.shutdown();
	}
	
	/**
	 * Set your online status.
	 */
	public void setStatus(OnlineStatus status) {
		jda.getPresence().setStatus(status);
	}

	/**
	 * Get all the registered command objects.
	 * @return
	 */
	public HashSet<Command> getCommands() {
		return commands;
	}
	
	/**
	 * Get the bot configuration file.
	 * @return
	 */
	public MediusBotConfig getConfig() {
		return config;
	}
	
	/**
	 * Get the JDA instance.
	 * @return
	 */
	public JDA getJDA() {
		return jda;
	}
	
	/**
	 * Get the current running instance of Medius Bot.
	 * @return
	 */
	public static MediusBot getInstance() {
		return bot;
	}
	
}