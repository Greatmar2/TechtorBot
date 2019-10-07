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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringEscapeUtils;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.modules.reddit.RedditScraper;
import takeshi.modules.reddit.pojo.Image;
import takeshi.modules.reddit.pojo.ImagePreview;
import takeshi.modules.reddit.pojo.Post;
import takeshi.templates.Templates;

/**
 * !r show something from reddit :)
 */
public class RedditCommand extends AbstractCommand {

	private static final Set<String> whitelistedDomains = new HashSet<>(
			Arrays.asList("imgur.com", "i.imgur.com", "i.redd.it", "pbs.twimg.com", "gfycat.com", "file1.answcdn.com", "i.reddituploads.com", "youtube.com"));

	/**
	 * Instantiates a new Reddit command.
	 */
	public RedditCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Posts something from reddit";
	}

	@Override
	public String getCommand() {
		return "reddit";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "r <subreddit>" };
	}

	@Override
	public String[] getAliases() {
		return new String[] { "r" };
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		String subReddit = "funny";
		if (args.length > 0) {
			subReddit = args[0];
		}
		List<Post> dailyTop = RedditScraper.getWeeklyTop(subReddit);
		if (dailyTop.size() == 0) {
			return Templates.command.reddit_sub_not_found.formatGuild(channel);
		}
		Random rng = new Random();
		Post post;
		do {
			int index = rng.nextInt(dailyTop.size());
			post = dailyTop.remove(index);
			if (!post.data.over_18) {
				if (post.data.is_self) {
					break;
				}
				if (whitelistedDomains.contains(post.data.domain)) {
					break;
				}
			}
		} while (dailyTop.size() > 0);
		if (post.data.is_self) {
			// "Debug: Is nsfw: " + post.data.over_18 +
			return "\n:newspaper:\n" + post.data.getTitle() + "\n" + post.data.getSelftext();
		}
		if (post.data.url != null && post.data.url.length() > 20) {
			return post.data.title + "\n" + post.data.url;
		}
		ImagePreview preview = post.data.getPreview();
		if (preview != null && preview.images.size() > 0) {
			if (channel.getType().equals(ChannelType.TEXT) && !PermissionUtil.checkPermission((TextChannel) channel,
					((TextChannel) channel).getGuild().getSelfMember(), Permission.MESSAGE_ATTACH_FILES)) {
				return Templates.permission_missing.formatGuild(channel, "MESSAGE_ATTACH_FILES");
			}
			for (Image image : preview.images) {
				try (InputStream in = new URL(StringEscapeUtils.unescapeHtml4(image.source.url)).openStream()) {
					File outputfile = new File("tmp_" + channel.getId() + ".jpg");
					ImageIO.write(ImageIO.read(in), "jpg", outputfile);
					bot.queue.add(channel.sendMessage(new MessageBuilder().append(post.data.title).build()).addFile(outputfile),
							message -> outputfile.delete());
					return "";
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Templates.command.reddit_nothing.formatGuild(channel);
	}
}