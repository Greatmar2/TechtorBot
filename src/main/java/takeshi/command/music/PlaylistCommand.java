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

package takeshi.command.music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandReactionListener;
import takeshi.command.meta.CommandVisibility;
import takeshi.command.meta.ICommandReactionListener;
import takeshi.command.meta.PaginationInfo;
import takeshi.db.controllers.CGuild;
import takeshi.db.controllers.CMusic;
import takeshi.db.controllers.CPlaylist;
import takeshi.db.controllers.CUser;
import takeshi.db.model.OMusic;
import takeshi.db.model.OPlaylist;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.handler.MusicPlayerHandler;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DebugUtil;
import takeshi.util.DisUtil;
import takeshi.util.Emojibet;
import takeshi.util.Misc;
import takeshi.util.YTUtil;

/**
 * !playlist shows the current songs in the add
 */
public class PlaylistCommand extends AbstractCommand implements ICommandReactionListener<PaginationInfo<OPlaylist>> {
	private final static int ITEMS_PER_PAGE = 20;

	/**
	 * Instantiates a new Playlist command.
	 */
	public PlaylistCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "information about the playlists";
	}

	@Override
	public String getCommand() {
		return "playlist";
	}

	@Override
	public String[] getUsage() {
		return new String[] { // "-- using playlists ",
				"playlist mine          //use your default", "playlist mine <code>   //use your playlist with code",
				"playlist lists         //see what you have ", "playlist guildlists    //see what the guild has",
				"playlist guild         //use the guild's default", "playlist guild <code>  //use the guild's playlist with code",
				"playlist global        //use the global playlist", "playlist settings      //check the settings",
				"playlist               //info about current playlist", "playlist list <page>   //Shows the music", "",
//				"-- Adding and removing music from the playlist",
				// "playlist show <pagenumber> //shows music in the playlist",
				"playlist add                  //adds the current music", "playlist add guild           //adds the current to the guild list",
				// "playlist add <youtubelink> //adds the link to the playlist",
				"playlist remove               //removes the current music",
				// "playlist remove <youtubelink> //removes song from playlist",
				"playlist removeall            //removes ALL songs", "", // "-- Changing the settings of the playlist",
				"playlist title <new title>    //edit the title", "playlist edit <new type>     //change the edit-type",
				"playlist play <id>            //plays a track", "playlist playtype <new type> //change the play-type",
				// "playlist visibility <new visibility> //change who can see the playlist",
				// "playlist reset //reset settings to default",
		};
	}

	@Override
	public String[] getAliases() {
		return new String[] { "pl" };
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	@Override
	public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		Guild guild = ((TextChannel) channel).getGuild();
		MusicPlayerHandler player = MusicPlayerHandler.getFor(guild, bot);
		SimpleRank userRank = bot.security.getSimpleRank(author, channel);
		int nowPlayingId = player.getCurrentlyPlaying();
		OMusic musicRec = CMusic.findById(nowPlayingId);
		if (!GuildSettings.get(guild).canUseMusicCommands(author, userRank)) {
			return Templates.music.required_role_not_found.formatGuild(channel,
					guild.getRoleById(GuildSettings.getFor(channel, GSetting.MUSIC_ROLE_REQUIREMENT)));
		}
		OPlaylist playlist = CPlaylist.findById(player.getActivePLaylistId());
		if (playlist.id == 0) {
			playlist = CPlaylist.getGlobalList();
		}
		String cp = DisUtil.getCommandPrefix(channel);
		if (args.length == 0) {
			if (playlist.isGlobalList()) {
				return Templates.music.playlist_using.formatGuild(channel, playlist.title) + " See `" + cp + "pl help` for more info\n"
						+ "You can switch to a different playlist with `" + cp + "pl guild` to the guild's list or `" + cp + "pl mine` to your own one";
			}
			return Templates.music.playlist_using.formatGuild(channel, playlist.title) + "Settings " + makeSettingsTable(playlist)
					+ "To add the currently playing music to the playlist use `" + DisUtil.getCommandPrefix(channel) + "pl add`, check out `"
					+ DisUtil.getCommandPrefix(channel) + "help pl` for more info";
		}
		OPlaylist newlist = null;
		ArrayList<OPlaylist> playlists = null;
		StringBuilder out = null;
		switch (args[0].toLowerCase()) {
		case "lists":
			playlists = CPlaylist.getPlaylistsForUser(CUser.getCachedId(author.getIdLong()));
			out = new StringBuilder("You have the following playlists:\n");
		case "guildlist":
		case "guildlists":
			if (playlists == null) {
				playlists = CPlaylist.getPlaylistsForGuild(CGuild.getCachedId(guild.getIdLong()));
				out = new StringBuilder("The guild has the following plalists: \n");
			}
			if (playlists.isEmpty()) {
				return "No playlists found";
			}
			for (OPlaylist list : playlists) {
				out.append("`").append(list.code).append("`").append(" - ").append(list.title).append("\n");
			}
			return out.toString();
		case "export":
			if (args.length == 1) {
				newlist = findPlaylist("mine", "default", author, guild);
			} else {
				newlist = findPlaylist("mine", Misc.joinStrings(args, 1), author, guild);
			}
			out = new StringBuilder();
			out.append(newlist.code).append(" - ").append(newlist.title).append("\n");
			for (OMusic music : CPlaylist.getMusic(newlist.id, 1000, 0)) {
				out.append("youtube.com/watch?v=").append(music.youtubecode).append("\n");
			}
			if (out.length() > 0) {
				DebugUtil.handleDebug(bot, channel, out.toString());
				return "";
			}
			return "Playlist is empty";
		case "mine":
		case "guild":
		case "global":
			String playlistCode = null;
			if (args.length > 1) {
				playlistCode = Misc.joinStrings(args, 1);
				if (playlistCode.length() > 32) {
					playlistCode = playlistCode.substring(0, 32);
				}
			}
			if (playlistCode == null || playlistCode.isEmpty()) {
				playlistCode = "default";
			}
			newlist = findPlaylist(args[0], playlistCode, author, guild);
			break;
		}
		if (newlist != null) {
			player.setActivePlayListId(newlist.id);
			return Templates.music.playlist_changed.formatGuild(channel, newlist.title);
		}

		switch (args[0].toLowerCase()) {
		case "add":
		case "+":
			if (nowPlayingId == 0) {
				return Templates.command.currentlyplaying.nosong.formatGuild(channel);
			}
			if (canAddTracks(playlist, (TextChannel) channel, author, userRank)) {
				if (CPlaylist.isInPlaylist(playlist.id, nowPlayingId)) {
					return Templates.playlist.music_already_added.formatGuild(channel, musicRec.youtubeTitle, playlist.title);
				}
				CPlaylist.addToPlayList(playlist.id, nowPlayingId);
				return Templates.playlist.music_added.formatGuild(channel, musicRec.youtubeTitle, playlist.title);
			}
			return Templates.no_permission.formatGuild(channel);
		case "removeall":
			if (isPlaylistAdmin(playlist, (TextChannel) channel, author, userRank)) {
				CPlaylist.resetPlaylist(playlist.id);
				return Templates.playlist.music_removed_all.formatGuild(channel, playlist.title);
			}
			return Templates.no_permission.formatGuild(channel);
		case "remove":
		case "del":
		case "-":
			if (args.length > 1 && (args[1].equals("guild") || args[1].equals("g"))) {
				playlist = CPlaylist.findBy(0, CGuild.getCachedId(guild.getIdLong()));
			} else if (args.length > 1 && args[1].matches("^\\d+$")) {
				musicRec = CMusic.findById(Integer.parseInt(args[1]));
				nowPlayingId = musicRec.id;
			} else if (args.length > 1 && YTUtil.isValidYoutubeCode(args[1])) {
				musicRec = CMusic.findByYoutubeId(args[1]);
				nowPlayingId = musicRec.id;
			}
			if (!canRemoveTracks(playlist, (TextChannel) channel, author, userRank)) {
				return Templates.no_permission.formatGuild(channel);
			}
			CPlaylist.removeFromPlayList(playlist.id, nowPlayingId);
			return Templates.playlist.music_removed.formatGuild(channel, musicRec.youtubeTitle, playlist.title);
		case "list":
		case "music":
			if (playlist.isGlobalList()) {
				return Templates.playlist.global_readonly.formatGuild(channel);
			}
			final int currentPage = 1;
			int totalTracks = CPlaylist.getMusicCount(playlist.id);
			int maxPage = (int) Math.ceil((double) totalTracks / (double) ITEMS_PER_PAGE);
			OPlaylist finalPlaylist = playlist;
			bot.queue.add(channel.sendMessage(makePage(guild, playlist, currentPage, maxPage)), msg -> {
				if (maxPage > 1) {
					bot.commandReactionHandler.addReactionListener(((TextChannel) channel).getGuild().getIdLong(), msg,
							getReactionListener(author.getIdLong(), new PaginationInfo<>(currentPage, maxPage, guild, finalPlaylist)));
				}
			});
			return "";

		default:
			break;
		}
		if (args[0].equals("settings")) {
			return makeSettingsTable(playlist);
		}
		if (playlist.isGlobalList()) {
			return Templates.playlist.global_readonly.formatGuild(channel);
		}
		boolean isPlaylistAdmin = isPlaylistAdmin(playlist, (TextChannel) channel, author, userRank);
		switch (args[0].toLowerCase()) {
		case "title":
			if (args.length == 1 || !isPlaylistAdmin) {
				return Templates.command.playlist_title.formatGuild(channel, playlist.title);
			}
			playlist.title = EmojiUtils.shortCodify(Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)));
			CPlaylist.update(playlist);
			player.setActivePlayListId(playlist.id);
			return Templates.playlist.title_updated.formatGuild(channel, playlist.title);
		case "edit-type":
		case "edittype":
		case "edit":
			if (args.length == 1 || !isPlaylistAdmin) {
				List<List<String>> tbl = new ArrayList<>();
				for (OPlaylist.EditType editType : OPlaylist.EditType.values()) {
					if (editType.getId() < 1)
						continue;
					tbl.add(Arrays.asList((editType == playlist.getEditType() ? "*" : " ") + editType.getId(), editType.toString(), editType.getDescription()));
				}
				return "the edit-type of the playlist. A `*` indicates the selected option\n"
						+ Misc.makeAsciiTable(Arrays.asList("#", "Code", "Description"), tbl, null) + "\n" + "To change the type use the \\#, for instance `"
						+ DisUtil.getCommandPrefix(channel) + "pl edit 3` sets it to PUBLIC_ADD \n\n"
						+ "Private in a guild context refers to users with admin privileges";
			}
			if (args[1].matches("^\\d+$")) {
				OPlaylist.EditType editType = OPlaylist.EditType.fromId(Integer.parseInt(args[1]));
				if (editType.equals(OPlaylist.EditType.UNKNOWN)) {
					Templates.playlist.setting_invalid.formatGuild(channel, args[1], "edittype");
				}
				playlist.setEditType(editType);
				CPlaylist.update(playlist);
				player.setActivePlayListId(playlist.id);
				return Templates.playlist.setting_updated.formatGuild(channel, "edittype", args[1]);
			}
			return Templates.playlist.setting_not_numeric.formatGuild(channel, "edittype");
		case "vis":
		case "visibility":
			if (args.length == 1 || !isPlaylistAdmin) {
				List<List<String>> tbl = new ArrayList<>();
				for (OPlaylist.Visibility visibility : OPlaylist.Visibility.values()) {
					if (visibility.getId() < 1)
						continue;
					tbl.add(Arrays.asList((visibility == playlist.getVisibility() ? "*" : " ") + visibility.getId(), visibility.toString(),
							visibility.getDescription()));
				}
				return "the visibility-type of the playlist. A `*` indicates the selected option\n"
						+ Misc.makeAsciiTable(Arrays.asList("#", "Code", "Description"), tbl, null) + "\n" + "To change the type use the \\#, for instance `"
						+ DisUtil.getCommandPrefix(channel) + "pl vis 3` sets it to guild \n\n"
						+ "Private in a guild-setting refers to users with admin privileges, use the number in the first column to set it";
			}
			if (args.length > 1 && args[1].matches("^\\d+$")) {
				OPlaylist.Visibility visibility = OPlaylist.Visibility.fromId(Integer.parseInt(args[1]));
				if (visibility.equals(OPlaylist.Visibility.UNKNOWN)) {
					Templates.playlist.setting_invalid.formatGuild(channel, args[1], "visibility");
				}
				playlist.setVisibility(visibility);
				CPlaylist.update(playlist);
				player.setActivePlayListId(playlist.id);
				return Templates.playlist.setting_updated.formatGuild(channel, "visibility", args[1]);
			}
			return Templates.playlist.setting_not_numeric.formatGuild(channel, "visibility");
		case "play":
			if (args.length > 1) {
				OMusic record = null;
				if (args[1].matches("^\\d+$")) {
					record = CMusic.findById(Integer.parseInt(args[1]));
				} else if (YTUtil.isValidYoutubeCode(args[1])) {
					record = CMusic.findByYoutubeId(args[1]);
				}
				if (record != null && record.id > 0) {
					if (player.canUseVoiceCommands(author, userRank)) {
						player.connectTo(guild.getMember(author).getVoiceState().getChannel());
						player.addToQueue(record.youtubecode, author);
						return Templates.music.added_to_queue.formatGuild(channel, record.youtubeTitle);
					}
				}
				return Templates.music.not_added_to_queue.formatGuild(channel, args[1]);
			}
			return Templates.invalid_use.formatGuild(channel);
		case "playtype":
		case "play-type":
			if (args.length == 1) {
				List<List<String>> tbl = new ArrayList<>();
				for (OPlaylist.PlayType playType : OPlaylist.PlayType.values()) {
					if (playType.getId() < 1)
						continue;
					tbl.add(Arrays.asList((playType == playlist.getPlayType() ? "*" : " ") + playType.getId(), playType.toString(), playType.getDescription()));
				}
				return "the play-type of the playlist. A `*` indicates the selected option\n"
						+ Misc.makeAsciiTable(Arrays.asList("#", "Code", "Description"), tbl, null) + "\n"
						+ "Private in a guild-setting refers to users with admin privileges, use the number in the first column to set it";
			}
			if (args[1].matches("^\\d+$")) {
				OPlaylist.PlayType playType = OPlaylist.PlayType.fromId(Integer.parseInt(args[1]));
				playlist.setPlayType(playType);
				CPlaylist.update(playlist);
				player.setActivePlayListId(playlist.id);
				return Templates.playlist.setting_updated.formatGuild(channel, "play-type", args[1]);
			}
			return Templates.playlist.setting_not_numeric.formatGuild(channel, "play-type");
		}
		return Templates.invalid_use.formatGuild(channel);
	}

	/**
	 * @see PlaylistCommand#canEditPlaylist(OPlaylist, TextChannel, User,
	 *      SimpleRank, boolean)
	 */
	private boolean canAddTracks(OPlaylist playlist, TextChannel channel, User invoker, SimpleRank userRank) {
		return canEditPlaylist(playlist, channel, invoker, userRank, true);
	}

	/**
	 * @see PlaylistCommand#canEditPlaylist(OPlaylist, TextChannel, User,
	 *      SimpleRank, boolean)
	 */
	private boolean canRemoveTracks(OPlaylist playlist, TextChannel channel, User invoker, SimpleRank userRank) {
		return canEditPlaylist(playlist, channel, invoker, userRank, false);
	}

	/**
	 * Check if a user has admin privilege on a playlist
	 *
	 * @param playlist the playlist to check
	 * @param channel  the channel where its invoked
	 * @param invoker  the user who invoked
	 * @param userRank rank of the user
	 * @return true if the user is at least a guild admin
	 */
	private boolean isPlaylistAdmin(OPlaylist playlist, TextChannel channel, User invoker, SimpleRank userRank) {
		if (playlist.isGlobalList() && userRank.isAtLeast(SimpleRank.CREATOR)) {
			return false;
		}
		if (playlist.isGuildList()) {
			return userRank.isAtLeast(SimpleRank.GUILD_ADMIN);
		}
		if (playlist.isPersonal()) {
			return CUser.getCachedId(invoker.getIdLong()) == playlist.ownerId;
		}
		return userRank.isAtLeast(SimpleRank.CREATOR);
	}

	/**
	 * can an invoker add/remove tracks?
	 *
	 * @param playlist the playlist to check
	 * @param channel  the channel where its invoked
	 * @param invoker  the user who invoked
	 * @param userRank rank of the user
	 * @param isAdding adding or removing? false for removing
	 * @return can the user add/remove tracks?
	 */
	private boolean canEditPlaylist(OPlaylist playlist, TextChannel channel, User invoker, SimpleRank userRank, boolean isAdding) {
		switch (playlist.getEditType()) {
		case PUBLIC_AUTO:
		case PUBLIC_FULL:
			return true;
		case PRIVATE_AUTO:
			if (playlist.isGuildList()) {
				if (!userRank.isAtLeast(SimpleRank.GUILD_ADMIN)) {
					return false;
				}
			} else if (playlist.isPersonal()) {
				return CUser.getCachedId(invoker.getIdLong()) == playlist.ownerId;
			}
		case PUBLIC_ADD:
			if (!isAdding) {
				if (playlist.isGuildList()) {
					return userRank.isAtLeast(SimpleRank.GUILD_ADMIN);
				} else if (playlist.isPersonal()) {
					return CUser.getCachedId(invoker.getIdLong()) == playlist.ownerId;
				}
				return false;
			}
			return true;
		case PRIVATE:
			if (playlist.isGuildList() && playlist.guildId == CGuild.getCachedId(channel.getGuild().getIdLong())
					&& userRank.isAtLeast(SimpleRank.GUILD_ADMIN)) {
				return true;
			} else if (playlist.isPersonal()) {
				return CUser.getCachedId(invoker.getIdLong()) == playlist.ownerId;
			}
			return false;
		}
		return false;
	}

	private OPlaylist findPlaylist(String search, String code, User user, Guild guild) {
		int userId;
		int guildId = CGuild.getCachedId(guild.getIdLong());
		OPlaylist playlist;
		String title;
		switch (search.toLowerCase()) {
		case "mine":
			title = user.getName() + "'s " + code + " list";
			userId = CUser.getCachedId(user.getIdLong(), user.getName());
			playlist = CPlaylist.findBy(userId, 0, code);
			break;
		case "guild":
			title = EmojiUtils.shortCodify(guild.getName()) + "'s " + code + " list";
			playlist = CPlaylist.findBy(0, guildId, code);
			break;
		case "global":
		default:
			title = "Global";
			playlist = CPlaylist.findBy(0, 0);
			break;
		}
		if (playlist.id == 0) {
			playlist.title = title;
			playlist.code = code;
			if (playlist.isPersonal()) {
				playlist.setEditType(OPlaylist.EditType.PRIVATE_AUTO);
			}
			CPlaylist.insert(playlist);
		}
		return playlist;
	}

	private String makeSettingsTable(OPlaylist playlist) {
		List<List<String>> body = new ArrayList<>();
		String owner = playlist.isGlobalList() ? "Techtor"
				: playlist.isGuildList() ? CGuild.findById(playlist.guildId).name : CUser.findById(playlist.ownerId).name;
		body.add(Arrays.asList("Title", playlist.title));
		if (!playlist.isGlobalList()) {
			body.add(Arrays.asList("code", playlist.code));
		}
		body.add(Arrays.asList("Owner", owner));
		body.add(Arrays.asList("edit-type", playlist.getEditType().getDescription()));
		body.add(Arrays.asList("play-type", playlist.getPlayType().getDescription()));
		return Misc.makeAsciiTable(Arrays.asList("Name", "Value"), body, null);
	}

	private String makePage(Guild guild, OPlaylist playlist, int currentPage, int maxPage) {
		List<OMusic> items = CPlaylist.getMusic(playlist.id, ITEMS_PER_PAGE, (currentPage - 1) * ITEMS_PER_PAGE);
		if (items.isEmpty()) {
			return "The playlist is empty!";
		}
		StringBuilder playlistTable = new StringBuilder("\n");
		for (OMusic item : items) {
			playlistTable.append(String.format("`%11s` | %s\n", item.youtubecode, item.youtubeTitle));
		}
		return String.format("Music in the playlist: %s\n", playlist.title) + playlistTable + "\n" + String.format("Showing [page %s/%s]", currentPage, maxPage)
				+ "\n\n" + "_You can use the `#` to remove an item from the playlist._" + "\n\n" + "_Example:_ `" + DisUtil.getCommandPrefix(guild)
				+ "pl del QnTYIBU7Ueg`";
	}

	@Override
	public CommandReactionListener<PaginationInfo<OPlaylist>> getReactionListener(long userId, PaginationInfo<OPlaylist> initialData) {
		CommandReactionListener<PaginationInfo<OPlaylist>> listener = new CommandReactionListener<>(userId, initialData);
		listener.setExpiresIn(TimeUnit.MINUTES, 2);
		listener.registerReaction(Emojibet.PREV_TRACK, o -> {
			if (listener.getData().previousPage()) {
				String txt = makePage(initialData.getGuild(), initialData.getExtra(), listener.getData().getCurrentPage(), listener.getData().getMaxPage());
				if (txt.length() > 2000) {
					txt = txt.substring(0, 1999);
				}
				o.editMessage(txt).complete();
			}
		});
		listener.registerReaction(Emojibet.NEXT_TRACK, o -> {
			if (listener.getData().nextPage()) {
				String txt = makePage(initialData.getGuild(), initialData.getExtra(), listener.getData().getCurrentPage(), listener.getData().getMaxPage());
				if (txt.length() > 2000) {
					txt = txt.substring(0, 1999);
				}
				o.editMessage(txt).complete();
			}
		});
		return listener;
	}
}