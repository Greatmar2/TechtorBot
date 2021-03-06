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

package takeshi.util;

import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import takeshi.db.model.OMusic;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.handler.MusicPlayerHandler;

/**
 * The type Music util.
 */
public class MusicUtil {
	private final static String BLOCK_INACTIVE = "\u25AC";
	private final static String BLOCK_ACTIVE = "\uD83D\uDD18";
	private final static String SOUND_CHILL = "\uD83D\uDD09";
	private final static String SOUND_LOUD = "\uD83D\uDD0A";
	private final static float SOUND_TRESHHOLD = 0.4F;
	private final static int BLOCK_PARTS = 10;

	/**
	 * Returns a fancy now playing message
	 *
	 * @param player the musicplayer
	 * @param record the record playing
	 * @param member the song requester
	 * @return an embedded message
	 */
	public static MessageEmbed nowPlayingMessage(MusicPlayerHandler player, OMusic record, Member member) {
		EmbedBuilder embed = new EmbedBuilder();
		Guild guild = player.getJDA().getGuildById(player.getGuild());

		embed.setThumbnail("https://i.ytimg.com/vi/" + record.youtubecode + "/0.jpg");
		try {
			embed.setAuthor(record.artist, "https://www.youtube.com/results?search_query=" + record.artist);
		} catch (java.lang.IllegalArgumentException ex) {

		}
		embed.setTitle("\uD83C\uDFB6 " + record.youtubeTitle, null);
		embed.setDescription("[Source](https://www.youtube.com/watch?v=" + record.youtubecode + ") | `" + DisUtil.getCommandPrefix(player.getGuild()) + "pl` - "
				+ player.getPlaylist().title);
		embed.addField("Duration",
//				Misc.getDurationString(player.player.getPlayingTrack().getPosition() / 1000) + " / " + 
				Misc.getDurationString(record.duration), true);
		String optionsField = "";
		if (player.getRequiredVotes() != 1) {
			optionsField += "Skips req.: " + player.getRequiredVotes() + "\n";
		}
		String requiredRole = GuildSettings.get(player.getGuild()).getOrDefault(GSetting.MUSIC_ROLE_REQUIREMENT);
		if (!requiredRole.equals("false")) {
			Role role = guild.getRoleById(requiredRole);
			if (role != null) {
				optionsField += "Role req.: " + role.getName() + "\n";
			}
		}
		if (!GuildSettings.get(player.getGuild()).getBoolValue(GSetting.MUSIC_QUEUE_ONLY)) {
			optionsField += "Random after queue";
		} else {
			optionsField += "Stop after queue";
		}
		embed.addField("Options:", optionsField, true);
		List<OMusic> queue = player.getQueue();
		int show = 3;
		if (!queue.isEmpty()) {
			StringBuilder x = new StringBuilder();
			for (int i = 0; i < Math.min(show, queue.size()); i++) {
				x.append(queue.get(i).youtubeTitle).append("\n");
			}
			if (queue.size() > show) {
				x.append("... and **").append(queue.size() - 3).append("** more");
			}
			embed.addField("Next up", x.toString(), true);
		}
		if (member != null) {
			embed.setFooter("Requested by " + member.getEffectiveName(), member.getUser().getAvatarUrl());
		} else {
			embed.setFooter("Add to your playlist or vote to skip with reactions", null);
		}
		return embed.build();
	}

	/**
	 * Now playing message no embed string.
	 *
	 * @param player the player
	 * @param record the record
	 * @return the string
	 */
	public static String nowPlayingMessageNoEmbed(MusicPlayerHandler player, OMusic record) {
		return "[`" + DisUtil.getCommandPrefix(player.getGuild()) + "pl` " + player.getPlaylist().title + "] \uD83C\uDFB6 " + record.youtubeTitle;
	}

	/**
	 * Gets mediaplayer progressbar.
	 *
	 * @param startTime timestamp (in seconds) of the moment the song started                  playing
	 * @param duration  current song length in seconds
	 * @param volume    volume of the player
	 * @param isPaused  the is paused
	 * @return a formatted mediaplayer
	 */
	public static String getMediaplayerProgressbar(long startTime, long duration, float volume, boolean isPaused) {
		long current = System.currentTimeMillis() / 1000 - startTime;
		StringBuilder bar = new StringBuilder(isPaused ? "\u23EF" : "\u23F8 ");
		int activeBLock = (int) ((float) current / (float) duration * BLOCK_PARTS);
		for (int i = 0; i < BLOCK_PARTS; i++) {
			if (i == activeBLock) {
				bar.append(BLOCK_ACTIVE);
			} else {
				bar.append(BLOCK_INACTIVE);
			}
		}
		bar.append(" [").append(Misc.getDurationString(current)).append("/").append(Misc.getDurationString(duration)).append("] ");
		if (volume >= SOUND_TRESHHOLD) {
			bar.append(SOUND_LOUD);
		} else {
			bar.append(SOUND_CHILL);
		}
		return bar.toString();
	}
}
