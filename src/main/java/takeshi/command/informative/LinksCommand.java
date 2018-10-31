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

package takeshi.command.informative;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.BotConfig;
import takeshi.main.DiscordBot;
import takeshi.util.QuickEmbedBuilder;

public class LinksCommand extends AbstractCommand {
	public LinksCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "general info about how to contribute or donate to " + BotConfig.BOT_NAME;
	}

	@Override
	public String getCommand() {
		return "links";
	}

	@Override
	public String[] getUsage() {
		return new String[] {};
	}

	@Override
	public String[] getAliases() {
		return new String[] { "contribute", "donate" };
	}

	@Override
	public MessageBuilder execute(DiscordBot bot, String[] args, MessageChannel channel, User author,
			Message inputMessage) {
		EmbedBuilder emMes = new EmbedBuilder();

		emMes.setColor(QuickEmbedBuilder.ELEK_COL);
		emMes.setDescription("Here is where you can find ElektronX");
		emMes.addField("Social Media",
				"[Tumblr](https://elektronx.tumblr.com) ([art tag](https://elektronx.tumblr.com/tagged/elek-doodles)) | "
						+ "[Twitter](https://twitter.com/elektronxz) | [Instagram](https://www.instagram.com/elektronxz/) | "
						+ "[YouTube](https://www.youtube.com/c/ElektronXz) | "
						+ "[Twitch](https://picarto.tv/elektronx) | [Picarto](https://picarto.tv/elektronx)",
				true);
		emMes.addField("Donate", "[Ko-fi](https://ko-fi.com/elektronx) | [PayPal](https://www.paypal.me/elektronx)",
				true);

		// emMes.setThumbnail(bot.getJda().getSelfUser().getAvatarUrl());
		User elek = bot.getJda().getUserById(150718934734209024L);
		emMes.setThumbnail(elek.getAvatarUrl());
		emMes.setAuthor(elek.getName(), null, elek.getAvatarUrl());

		return new MessageBuilder(emMes);
	}

	@Override
	public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author,
			Message inputMessage) {
		return "Use full execution";
	}
	// @Override
	// public String simpleExecute(DiscordBot bot, String[] args, MessageChannel
	// channel, User author, Message inputMessage) {
	// String prefix = DisUtil.getCommandPrefix(channel);
	// return "You're interested in contributing, that's great!\n \n" + "**Found a
	// bug!**\n"
	// + "You can report them on either *" + prefix + "discord* or *" + prefix +
	// "github*\n \n"
	// + "**Want to contribute or share your thoughts?**\n" + "Feel free to join *"
	// + prefix
	// + "discord* and let your voice be heard! Feedback and suggestions are always
	// welcome!\n \n"
	// + "**You know how to speak 0101?**\n" + "Check out *" + prefix
	// + "github* and feel free to pick up one of the open issues\n \n"
	// + "If you've ascended beyond 0101 and know multiple numbers, consider
	// following the project on github to see whats happening\n";
	// }
}