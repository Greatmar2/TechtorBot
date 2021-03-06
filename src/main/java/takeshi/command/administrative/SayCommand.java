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

package takeshi.command.administrative;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * !say make the bot say something
 */
public class SayCommand extends AbstractCommand {
	/**
	 * Instantiates a new Say command.
	 */
	public SayCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "repeats you";
	}

	@Override
	public String getCommand() {
		return "say";
	}

	@Override
	public boolean isListed() {
		return false;
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"say [channel] <anything>\t//Repeats what you said, either in the current channel or in the channel mentioned just before the message" };
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
		// boolean atLeastAdmin = bot.security.getSimpleRank(author,
		// channel).isAtLeast(SimpleRank.BOT_ADMIN);
		if (bot.security.getSimpleRank(author, channel).isAtLeast(SimpleRank.GUILD_BOT_ADMIN)) {
			TextChannel targetChannel = null;
			List<Attachment> attachs = inputMessage.getAttachments();

			String output = " ";
			if (args.length > 0) {
				if (channel.getType() == ChannelType.TEXT) {
					List<TextChannel> channels = inputMessage.getMentionedChannels();
					if (channels.size() > 0) {
						targetChannel = channels.get(0);
//					channel.sendMessage(inputMessage).queue();
						args = Arrays.copyOfRange(args, 1, args.length);
					}
				}
				output = Joiner.on(" ").join(args);
			}
//			if (DisUtil.isUserMention(output) && !atLeastAdmin) {
//				return Templates.command.SAY_CONTAINS_MENTION.formatGuild(channel);
//			}
			// Calculate queue delay based on message length
			long queueDelay = 0L;
			final boolean sendingInOtherChannel = targetChannel != null;
			if (sendingInOtherChannel) {
				channel = targetChannel;
				channel.sendTyping().queue();
				queueDelay = Math.min((output.length() * 1000) / 7, 30000); // Cap at 30 seconds
				for (long i = 7500; i <= queueDelay; i += 7500) { // Typing status disappears after 10 seconds, make sure it doesn't.
					channel.sendTyping().queueAfter(i, TimeUnit.MILLISECONDS);
				}
			} else if (attachs.size() == 0) {
				DisUtil.tryDeleteMessage(inputMessage);
			}

			MessageBuilder outMessage = new MessageBuilder(output);

			// If the user is at least a guild admin, they can make the bot send an image.
			// Guild owner or above can send other files.
			if (attachs.size() > 0 && (attachs.get(0).isImage() || bot.security.getSimpleRank(author, channel).isAtLeast(SimpleRank.GUILD_OWNER))) { // atLeastAdmin
																																						// &&
				File tempFolder = new File("tmp");
				if (!tempFolder.isDirectory()) { // Make tmp folder if none exists
					tempFolder.mkdir();
				}
				File temp = new File("tmp/" + author.getId() + "_" + attachs.get(0).getFileName());
				final MessageChannel finalChannel = channel;
				attachs.get(0).downloadToFile(temp).thenRun(new Runnable() {
					@Override
					public void run() {
						bot.queue.add(finalChannel.sendMessage(outMessage.build()).addFile(temp), message -> {
							temp.delete();
							if (sendingInOtherChannel) {
								DisUtil.tryDeleteMessage(inputMessage);
							}
						});
					}
				});
//				if (attachs.get(0).downloadToFile(temp).isDone()) {
//					if (outMessage.length() > 0) {
//						bot.queue.add(channel.sendMessage(outMessage.build()).addFile(temp), message -> temp.delete());
//					} else {
//						bot.queue.add(channel.sendFile(temp), message -> temp.delete());
//					}
//				}

			} else if (!output.trim().isEmpty()) {
				channel.sendMessage(outMessage.build()).queueAfter(queueDelay, TimeUnit.MILLISECONDS);
			} else {
				return Templates.command.SAY_WHATEXACTLY.formatGuild(channel);
			}
			return "";
		} else {
			return Templates.no_permission.formatGuild(channel);
		}
	}
}