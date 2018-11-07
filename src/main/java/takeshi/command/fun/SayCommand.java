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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * !say make the bot say something
 */
public class SayCommand extends AbstractCommand {
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
		return new String[] { "say <anything>" };
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		boolean atLeastAdmin = bot.security.getSimpleRank(author, channel).isAtLeast(SimpleRank.GUILD_ADMIN);
		TextChannel targetChannel = null;
		List<Attachment> attachs = inputMessage.getAttachments();
		if (args.length > 0) {
			if (channel.getType() == ChannelType.TEXT) {
				if (DisUtil.isChannelMention(args[0])) {
					targetChannel = inputMessage.getMentionedChannels().get(0);
//					channel.sendMessage(inputMessage).queue();
					args = Arrays.copyOfRange(args, 1, args.length);
				}
			}
			String output = Joiner.on(" ").join(args);
			if (DisUtil.isUserMention(output) && !atLeastAdmin) {
				return Templates.command.SAY_CONTAINS_MENTION.formatGuild(channel);
			}
			// Calculate queue delay based on message length
			long queueDelay = 0L;
			if (targetChannel != null) {
				channel = targetChannel;
				channel.sendTyping().queue();
				queueDelay = (output.length() * 1000) / 8;
			}

			if (!output.trim().isEmpty()) {
				MessageBuilder outMessage = new MessageBuilder(output);

				// If the user is at least a guild admin, they can make the bot send an image.
				if (atLeastAdmin && attachs.size() > 0 && attachs.get(0).isImage()) {
					File temp = new File("tmp/" + author.getId() + "_" + attachs.get(0).getFileName());
					temp.mkdir();
					if (attachs.get(0).download(temp)) {
						if (targetChannel == null && PermissionUtil.checkPermission((Channel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
								Permission.MESSAGE_MANAGE)) {
							inputMessage.delete().queue();
						}
						bot.queue.add(channel.sendFile(temp, outMessage.build()), message -> temp.delete());
					}

				} else {
					channel.sendMessage(outMessage.build()).queueAfter(queueDelay, TimeUnit.MILLISECONDS);
				}
				return "";
			}

		}
		// If message is blank but there's still an image
		if (atLeastAdmin && attachs.size() > 0 && attachs.get(0).isImage()) {
			File temp = new File("tmp/" + author.getId() + "_" + attachs.get(0).getFileName());
			temp.mkdir();
			if (attachs.get(0).download(temp)) {
				if (targetChannel == null
						&& PermissionUtil.checkPermission((Channel) channel, ((TextChannel) channel).getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
					inputMessage.delete().queue();
				}
				bot.queue.add(channel.sendFile(temp), message -> temp.delete());
			}
			return "";
		}
		return Templates.command.SAY_WHATEXACTLY.formatGuild(channel);
	}
}