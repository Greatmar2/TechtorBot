/*
 * Copyright 2017 github.com/kaaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package takeshi.command.fun;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.core.Logger;
import takeshi.db.controllers.CGuild;
import takeshi.db.controllers.CPoll;
import takeshi.db.model.OPoll;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;
import takeshi.util.Misc;

/**
 * The type Poll command.
 */
public class PollCommand extends AbstractCommand {

	/**
	 * Instantiates a new Poll command.
	 */
	public PollCommand() {
		super();
	}

	@Override
	public boolean isListed() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Strawpoll: propose a question and choices for the chat to vote on";
	}

	@Override
	public String getCommand() {
		return "poll";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "poll create [channel] <question> ;<option1>;<option2>;<etc.>   (max 9)", "              //creates a poll",
				"poll timed <m/h/d> <time> [channel] <question> ;<option1>;<option2>;<etc.>   (max 9)", "              //creates a timed poll",
				"poll single <m/h/d> <time> [channel] <question> ;<option1>;<option2>;<etc.>   (max 9)", "              //creates a single-choice timed poll",
				"poll cancel [channel]", "              //instantly ends any timed polls in the current or specified channel",
				"A specified channel must be #mentioned, otherwise the current channel will be used.",
				"Failure to do so shall result in it being included in the question." };
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		Guild guild = ((TextChannel) channel).getGuild();
		boolean debug = GuildSettings.getBoolFor(channel, GSetting.DEBUG);
		if (!PermissionUtil.checkPermission((TextChannel) channel, guild.getSelfMember(), Permission.MESSAGE_ADD_REACTION)) {
			return "Need permission to add reactions";
		}
		if (args.length == 0) {
			StringBuilder usage = new StringBuilder(":gear: **Options**:```php\n");
			for (String line : getUsage()) {
				usage.append(line).append("\n");
			}
			return usage.toString() + "```";
		}

		int argStart = 1;
//		long threadWaitTime = 0L;
		EmbedBuilder emPoll = new EmbedBuilder();
		OPoll poll = new OPoll();
		switch (args[0].toLowerCase()) {
		case "one":
		case "single":
			poll.single = true;
		case "time":
		case "timer":
		case "timed": // If it is a timed poll, first set up the time handlers. Also can start timer
						// thread later for times <= 10 min.
			argStart += 2;
//			Date now = new Date();
			TimeUnit unit;
			switch (args[1].toLowerCase()) {
//			case "s":
//				unit = TimeUnit.SECONDS;
//				break;
			case "m":
				unit = TimeUnit.MINUTES;
				break;
			case "h":
				unit = TimeUnit.HOURS;
				break;
			case "d":
				unit = TimeUnit.DAYS;
				break;
			default:
				return "Unrecognised time unit! Please use one of the following: `m`, `h`, or `d`";
			}
			try {
				long waitTime = unit.toMillis(Long.parseLong(args[2]));
				if (waitTime == 0) {
					return "Must last at least 1 minute!";
				}
				if (waitTime >= TimeUnit.DAYS.toMillis(30)) {
					return "May not be more than 30 days";
				}
//				long maxThreadTime = TimeUnit.MINUTES.toMillis(30);
				// Start timer threads for polls that last <= 30 min
//				if (waitTime <= maxThreadTime) {
//					threadWaitTime = Math.min(waitTime, maxThreadTime);
//				}
				poll.guildId = CGuild.getCachedId(inputMessage.getGuild().getIdLong());
				poll.message = inputMessage.getContentRaw();
				long epochMilli = new Date().getTime() + waitTime;
				poll.messageExpire = new Timestamp(epochMilli);
				emPoll.setTimestamp(Instant.ofEpochMilli(epochMilli));

				waitTime += 1000;
				bot.schedule(new Runnable() {
					@Override
					public void run() {
						bot.pollHandler.checkPolls(guild);
					}
				}, waitTime, TimeUnit.MILLISECONDS);
//				bot.schedule(bot.pollHandler.checkPolls(guild), waitTime, TimeUnit.MILLISECONDS);

				if (debug) {
					channel.sendMessage(String.format("[DEBUG] Created poll in %s with time of %s millisec", channel.getName(), waitTime)).queue();
				}
			} catch (NumberFormatException e) {
				Logger.warn(e.getMessage(), e.getStackTrace());
			}
		case "new":
		case "create": // Create the poll
			// Check if the poll must be single-choice
//			if(args[argStart].equalsIgnoreCase("single")) {
//				poll.single = true;
//				argStart++;
//			}
			// Check for channel argument
			List<TextChannel> channels = inputMessage.getMentionedChannels();
			if (!channels.isEmpty() && channels.get(0).getAsMention().equalsIgnoreCase(args[argStart])) {
				channel = channels.get(0);
				argStart++;
			}
			poll.channelId = channel.getIdLong();
			// Compile remaining arguments into the poll-argument (to be split by ";")
			String argument = "";
			for (int i = argStart; i < args.length; i++) {
				argument += " " + args[i];
			}
			String[] split = argument.split(";");
			if (split.length < 3) {
				return "Invalid usage! Need at least 2 options " + getUsage()[1];
			}
			if (split[0].trim().length() < 3) {
				return Templates.command.poll_question_too_short.formatGuild(channel);
			}
			emPoll.setAuthor(author.getName() + "'s Poll", null, author.getAvatarUrl());
			emPoll.setDescription("**" + split[0].trim() + "**");
			emPoll.setColor(Misc.randomCol());
			if (argStart >= 3) {
				String footer = "Poll ends";
				if (poll.single) {
					footer = "Single-choice poll | " + footer;
				}
				emPoll.setFooter(footer, null);
			}
			final int answers = Math.min(9, split.length);
			for (int i = 1; i < answers; i++) {
				emPoll.addField(Misc.numberToEmote(i), split[i].trim(), true);
			}
			bot.queue.add(channel.sendMessage(emPoll.build()), message -> {
				if (poll.channelId > 0) {
					poll.messageId = message.getIdLong();
					CPoll.insert(poll);
				}
				for (int i = 1; i < answers; i++) {
					message.addReaction(Misc.numberToEmote(i)).complete();
				}
			});

//			if (threadWaitTime > 0L) {
//				PollTimerThread timer = new PollTimerThread(bot.pollHandler, inputMessage.getGuild(), threadWaitTime + 1000);
//				timer.start();
//				if (debug) {
//					channel.sendMessage(String.format("[DEBUG] Poll length less than 30 min, started wait thread")).queue();
//				}
//			}
			return "";
		case "end":
		case "stop":
		case "cancel":
			List<TextChannel> menChannels = inputMessage.getMentionedChannels();
			if (!menChannels.isEmpty() && menChannels.get(0).getAsMention().equalsIgnoreCase(args[argStart])) {
				channel = menChannels.get(0);
			}
			bot.pollHandler.checkPolls(guild, channel.getIdLong());
			return "";
		default:
			return "Invalid usage! See help for more info";
		}
	}
}