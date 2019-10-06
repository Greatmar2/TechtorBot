/*
 * Copyright 2017 github.com/kaaz and Greatmar2
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

package takeshi.command.bot_administration;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.main.BotConfig;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * !reply
 */
public class ReplyCommand extends AbstractCommand {
	public ReplyCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Repeats your message in a text or private channel specified by ID. If enabled in the bot's boot config file, the bot will forward non-command messages from private messages or guild text channels where it is mentioned. If a guild chat is replied to, all messages will be forwarded from that chat for the next "
				+ BotConfig.CHANNEL_WATCH_DURATION + " minutes (specified in bot config).";
	}

	@Override
	public String getCommand() {
		return "reply";
	}

	@Override
	public boolean isListed() {
		return true;
	}

	@Override
	public String[] getUsage() {
		return new String[] { "reply <channel ID> <message>\t//Repeats what you said in the channel specified by an ID before the message" };
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.BOTH;
	}

	@Override
	public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		// boolean atLeastAdmin = bot.security.getSimpleRank(author,
		// channel).isAtLeast(SimpleRank.BOT_ADMIN);
		if (bot.security.getSimpleRank(author, channel).isAtLeast(SimpleRank.BOT_ADMIN)) {
			MessageChannel targetChannel = null;
			List<Attachment> attachs = inputMessage.getAttachments();
			String output = " ";
			// Must have an argument specifying the channel ID
			if (args.length > 0 || (attachs.size() > 0 && args.length >= 0)) {
				// Find the channel that the message must be sent to
				String channelId = DisUtil.extractId(args[0]);
				if (channelId != null) {
					targetChannel = bot.getJda().getTextChannelById(channelId);
					if (targetChannel == null) {
						targetChannel = bot.getJda().getPrivateChannelById(channelId);
					}
				}
				// Calculate queue delay based on message length
				long queueDelay = 0L;
				if (targetChannel != null && DisUtil.hasPermission(targetChannel, bot.getJda().getSelfUser(), Permission.MESSAGE_WRITE)) {
					args = Arrays.copyOfRange(args, 1, args.length);
					output = Joiner.on(" ").join(args);

					targetChannel.sendTyping().queue();
					queueDelay = Math.min((output.length() * 1000) / 7, 20000); // Cap at 20 seconds
					for (long i = 7500; i <= queueDelay; i += 7500) { // Typing status disappears after 10 seconds, make sure it doesn't.
						targetChannel.sendTyping().queueAfter(i, TimeUnit.MILLISECONDS);
					}
					MessageBuilder outMessage = new MessageBuilder(output);

					// Check if there is an attached file
					if (attachs.size() > 0) {
						File tempFolder = new File("tmp");
						if (!tempFolder.isDirectory()) { // Make tmp folder if none exists
							tempFolder.mkdir();
						}
						File temp = new File("tmp/" + author.getId() + "_" + attachs.get(0).getFileName());
						final MessageChannel finalChannel = targetChannel;
						attachs.get(0).downloadToFile(temp).thenRun(new Runnable() {
							@Override
							public void run() {
								bot.queue.add(finalChannel.sendMessage(outMessage.build()).addFile(temp), message -> {
									temp.delete();
								});
							}
						});
					} else if (!output.trim().isEmpty()) {
						targetChannel.sendMessage(outMessage.build()).queueAfter(queueDelay, TimeUnit.MILLISECONDS);
					}

					if (targetChannel.getType() == ChannelType.TEXT) {
						// Add listener for more messages
						for (ReplyListener listener : bot.replyListeners) {
							if (listener.getChannelID() == targetChannel.getIdLong()) {
								bot.replyListeners.remove(listener);
								break;
							}
						}
						bot.replyListeners.add(new ReplyListener(targetChannel, OffsetDateTime.now()));
					}

					return "Message sent to " + targetChannel.getName();
				} else {
					return "Channel not found. I will only remember channels that I have received messages from recently.";
					// I expect it will be from the most recent boot, but will need to test
				}
			} else {
				return Templates.command.SAY_WHATEXACTLY.formatGuild(channel);
			}
		} else {
			return Templates.no_permission.formatGuild(channel);
		}
		// return Templates.invalid_use.formatGuild(channel);
	}

	public class ReplyListener {
		private MessageChannel channel;
		private OffsetDateTime timeCreated;

		public ReplyListener(MessageChannel channel, OffsetDateTime timeCreated) {
			this.channel = channel;
			this.timeCreated = timeCreated;
		}

		public MessageChannel getChannel() {
			return channel;
		}

		public void setChannel(MessageChannel channel) {
			this.channel = channel;
		}

		public OffsetDateTime getTimeCreated() {
			return timeCreated;
		}

		public void setTimeCreated(OffsetDateTime timeCreated) {
			this.timeCreated = timeCreated;
		}

		public long getChannelID() {
			return channel.getIdLong();
		}
	}
}