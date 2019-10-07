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

package takeshi.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.db.WebDb;
import takeshi.db.controllers.CBotPlayingOn;
import takeshi.db.controllers.CGuild;
import takeshi.db.controllers.CMusic;
import takeshi.db.controllers.CMusicLog;
import takeshi.db.controllers.CPlaylist;
import takeshi.db.controllers.CUser;
import takeshi.db.model.OMusic;
import takeshi.db.model.OPlaylist;
import takeshi.guildsettings.GSetting;
import takeshi.handler.audio.AudioPlayerSendHandler;
import takeshi.handler.audio.QueuedAudioTrack;
import takeshi.handler.discord.MessageMetaData;
import takeshi.main.DiscordBot;
import takeshi.main.Launcher;
import takeshi.permission.SimpleRank;
import takeshi.util.Emojibet;
import takeshi.util.MusicUtil;

/**
 * The type Music player handler.
 */
public class MusicPlayerHandler {
	private final static DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private final static Map<Long, MusicPlayerHandler> playerInstances = new ConcurrentHashMap<>();
	/**
	 * The Player.
	 */
	public final AudioPlayer player;
	private final DiscordBot bot;
	private final TrackScheduler scheduler;
	private final HashSet<User> skipVotes;
	private final long guildId;
	private volatile boolean inRepeatMode = false;
	private volatile boolean stopAfterTrack = false;
	private volatile int currentlyPlaying = 0;
	private volatile long currentSongLength = 0;
	private volatile long pauseStart = 0;
	private volatile boolean updateChannelTitle = false;
	private volatile long currentSongStartTimeInSeconds = 0;
	private volatile int activePlayListId;
	private volatile OPlaylist playlist;
	private Random rng;
	private volatile LinkedList<OMusic> queue;
	private final ArrayList<MessageMetaData> messagesToDelete = new ArrayList<>();

	/**
	 * Will delete the message when the currently playing track ends
	 *
	 * @param channelId the channel the message is put in
	 * @param messageId the id of the message
	 */
	public void deleteMessageAfterTrack(long channelId, long messageId) {
		messagesToDelete.add(new MessageMetaData(channelId, messageId));
	}

	private MusicPlayerHandler(Guild guild, DiscordBot bot) {

		rng = new Random();
		AudioManager guildManager = guild.getAudioManager();
		player = playerManager.createPlayer();
		this.bot = bot;
		this.guildId = guild.getIdLong();
		guildManager.setSendingHandler(new AudioPlayerSendHandler(player));
		queue = new LinkedList<>();
		scheduler = new TrackScheduler(player);
		player.addListener(scheduler);
		player.setVolume(Integer.parseInt(GuildSettings.get(guild.getIdLong()).getOrDefault(GSetting.MUSIC_VOLUME)));
		playerInstances.put(guild.getIdLong(), this);
		int savedPlaylist = Integer.parseInt(GuildSettings.get(guild.getIdLong()).getOrDefault(GSetting.MUSIC_PLAYLIST_ID));
		if (savedPlaylist > 0) {
			playlist = CPlaylist.findById(savedPlaylist);
		}
		if (savedPlaylist == 0 || playlist.id == 0) {
			playlist = CPlaylist.getGlobalList();
		}
		activePlayListId = playlist.id;
		skipVotes = new HashSet<>();
	}

	/**
	 * Init.
	 */
	public static void init() {
		AudioSourceManagers.registerRemoteSources(playerManager);
		playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
		playerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
	}

	/**
	 * Remove guild.
	 *
	 * @param guild the guild
	 */
	public static void removeGuild(Guild guild) {
		removeGuild(guild, false);
	}

	/**
	 * Remove guild.
	 *
	 * @param guild      the guild
	 * @param saveStatus the save status
	 */
	public static void removeGuild(Guild guild, boolean saveStatus) {
		if (playerInstances.containsKey(guild.getIdLong())) {
			if (saveStatus && playerInstances.get(guild.getIdLong()).isConnected()) {
				CBotPlayingOn.insert(guild.getId(), guild.getAudioManager().getConnectedChannel().getId());
			}
			playerInstances.get(guild.getIdLong()).leave();
			playerInstances.remove(guild.getIdLong());
		}
	}

	/**
	 * Gets for.
	 *
	 * @param guild the guild
	 * @return the for
	 */
	public static MusicPlayerHandler getFor(Guild guild) {
		return playerInstances.get(guild.getIdLong());
	}

	/**
	 * Gets for.
	 *
	 * @param guild the guild
	 * @param bot   the bot
	 * @return the for
	 */
	public static MusicPlayerHandler getFor(Guild guild, DiscordBot bot) {
		if (playerInstances.containsKey(guild.getIdLong())) {
			return playerInstances.get(guild.getIdLong());
		} else {
			return new MusicPlayerHandler(guild, bot);
		}
	}

	/**
	 * Go to time.
	 *
	 * @param millis the millis
	 */
	public void goToTime(Long millis) {
		player.getPlayingTrack().setPosition(millis);
	}

	/**
	 * Gets playlist.
	 *
	 * @return the playlist
	 */
	public OPlaylist getPlaylist() {
		return playlist;
	}

	/**
	 * Gets guild.
	 *
	 * @return the guild
	 */
	public long getGuild() {
		return guildId;
	}

	/**
	 * Is in voice with boolean.
	 *
	 * @param guild  the guild
	 * @param author the author
	 * @return the boolean
	 */
	public boolean isInVoiceWith(Guild guild, User author) {
		VoiceChannel channel = guild.getMember(author).getVoiceState().getChannel();
		if (channel == null) {
			return false;
		}
		for (Member user : channel.getMembers()) {
			if (user.getUser().getId().equals(guild.getJDA().getSelfUser().getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if a user meets the requirements to use the music commands
	 *
	 * @param user the user
	 * @param rank the rank
	 * @return bool boolean
	 */
	public boolean canUseVoiceCommands(User user, SimpleRank rank) {
		Guild guild = user.getJDA().getGuildById(guildId);
		if (PermissionUtil.checkPermission(guild.getMember(user), Permission.ADMINISTRATOR)) {
			return true;
		}
		if (!GuildSettings.get(guild).canUseMusicCommands(user, rank)) {
			return false;
		}
		GuildVoiceState voiceStatus = guild.getMember(user).getVoiceState();
		if (voiceStatus == null) {
			return false;
		}
		VoiceChannel userVoice = voiceStatus.getChannel();
		if (userVoice == null) {
			return false;
		}
		if (guild.getAudioManager().getConnectedChannel() != null) {
			return guild.getAudioManager().getConnectedChannel().equals(userVoice);
		}
		return true;
	}

	/**
	 * Is in repeat mode boolean.
	 *
	 * @return the boolean
	 */
	public synchronized boolean isInRepeatMode() {
		return inRepeatMode;
	}

	/**
	 * Sets repeat.
	 *
	 * @param repeatMode the repeat mode
	 */
	public synchronized void setRepeat(boolean repeatMode) {
		inRepeatMode = repeatMode;
	}

	/**
	 * Gets active p laylist id.
	 *
	 * @return the active p laylist id
	 */
	public int getActivePLaylistId() {
		return activePlayListId;
	}

	/**
	 * Sets the active playlist, or refreshes the currently cached one
	 *
	 * @param id internal id of the playlist
	 */
	public synchronized void setActivePlayListId(int id) {
		playlist = CPlaylist.findById(id);
		if (activePlayListId != playlist.id) {
			activePlayListId = playlist.id;
			GuildSettings.get(guildId).set(null, GSetting.MUSIC_PLAYLIST_ID, "" + id);
		}
	}

	private synchronized void trackEnded() {
		currentSongLength = 0;
		boolean keepGoing = false;
		if (!messagesToDelete.isEmpty()) {
			for (MessageMetaData messageMetaData : messagesToDelete) {
				TextChannel chan = getJDA().getTextChannelById(messageMetaData.getChannelId());
				if (chan != null) {
					chan.deleteMessageById(messageMetaData.getMessageId()).queue();
				}
			}
			messagesToDelete.clear();
		}

		if (scheduler.queue.isEmpty()) {
			if (queue.isEmpty()) {
				if (!stopAfterTrack && !GuildSettings.get(guildId).getBoolValue(GSetting.MUSIC_QUEUE_ONLY)) {
					keepGoing = true;
					if (!playRandomSong()) {
						player.destroy();
						bot.queue.add(bot.getMusicChannel(guildId).sendMessage("Stopped playing because the playlist is empty"));
						bot.schedule(() -> MusicPlayerHandler.removeGuild(bot.getJda().getGuildById(guildId)), 10L, TimeUnit.SECONDS);
						return;
					}
				} else {
					stopAfterTrack = false;
					bot.schedule(() -> MusicPlayerHandler.removeGuild(bot.getJda().getGuildById(guildId)), 10L, TimeUnit.SECONDS);
					return;
				}
			}
			final OMusic trackToAdd = queue.poll();
			if (trackToAdd == null) {
				return;
			}
			boolean finalKeepGoing = keepGoing;
			playerManager.loadItemOrdered(player, trackToAdd.youtubecode, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					if (track.getSourceManager().getSourceName().equals("youtube")) {
						OMusic rec = CMusic.findByYoutubeId(track.getInfo().identifier);
						if (rec.id == 0 || rec.duration == 0) {
							rec.artist = track.getInfo().author;
							rec.duration = (int) (track.getInfo().length / 1000L);
							rec.youtubeTitle = track.getInfo().title;
							CMusic.update(rec);
						}
					}
					scheduler.queue(new QueuedAudioTrack(trackToAdd.requestedBy, track));
					startPlaying();
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
				}

				@Override
				public void noMatches() {
				}

				@Override
				public void loadFailed(FriendlyException exception) {
					TextChannel musicChannel = bot.getMusicChannel(guildId);
					if (musicChannel != null) {
						bot.queue.add(musicChannel.sendMessage(String.format("Can't play `%s`. Reason: %s", trackToAdd.youtubecode, exception.getMessage())));
					}
					if (finalKeepGoing) {
						trackEnded();
					}
				}
			});
		}
	}

	private synchronized void trackStarted() {
		if (currentlyPlaying != 0 && pauseStart > 0) {
			pauseStart = 0;
			return;
		}
		skipVotes.clear();
		currentSongStartTimeInSeconds = System.currentTimeMillis() / 1000L;
		OMusic record;
		final String messageType = GuildSettings.get(guildId).getOrDefault(GSetting.MUSIC_PLAYING_MESSAGE);
		AudioTrackInfo info = player.getPlayingTrack().getInfo();
		if (info != null) {
			record = CMusic.findByYoutubeId(info.identifier);
			if (record.id > 0) {
				if (((scheduler.getLastRequester() != null) && !scheduler.getLastRequester().isEmpty()) || !playlist.isGlobalList()) {
					if (scheduler.getLastRequester() != null && !scheduler.getLastRequester().isEmpty()) {
						record.playCount++;
					}
					record.lastplaydate = System.currentTimeMillis() / 1000L;
					CMusic.update(record);
				}
				currentlyPlaying = record.id;
				currentSongLength = record.duration;
				CMusicLog.insert(CGuild.getCachedId(guildId), record.id, 0);
				if (!playlist.isGlobalList()) {
					CPlaylist.updateLastPlayed(playlist.id, record.id);
				}
			}
		} else {
			record = new OMusic();
		}
		TextChannel musicChannel = bot.getMusicChannel(guildId);
		if (GuildSettings.get(guildId).getBoolValue(GSetting.MUSIC_CHANNEL_TITLE)) {
			Guild guild = bot.getJda().getGuildById(guildId);
			if (musicChannel != null && PermissionUtil.checkPermission(musicChannel, guild.getSelfMember(), Permission.MANAGE_CHANNEL)) {
				if (!isUpdateChannelTitle()) {
					bot.queue.add(musicChannel.getManager().setTopic("\uD83C\uDFB6 " + record.youtubeTitle));
				}
			}
		}
		if (!"off".equals(messageType) && record.id > 0) {
			if (musicChannel == null || !musicChannel.canTalk()) {
				return;
			}
			Consumer<Message> callback = (message) -> {
				if (messageType.equals("clear")) {
					deleteMessageAfterTrack(message.getChannel().getIdLong(), message.getIdLong());
				}
				bot.musicReactionHandler.clearGuild(guildId);
				Guild guild = bot.getJda().getGuildById(guildId);
				if (PermissionUtil.checkPermission(message.getTextChannel(), guild.getSelfMember(), Permission.MESSAGE_ADD_REACTION,
						Permission.MESSAGE_HISTORY)) {
					message.addReaction(Emojibet.STAR).complete();
					message.addReaction(Emojibet.NEXT_TRACK).complete();
					if (aListenerIsAtLeast(SimpleRank.BOT_ADMIN)) {
						message.addReaction(Emojibet.NO_ENTRY).complete();
					}
					bot.musicReactionHandler.addMessage(guildId, message.getIdLong());
				}
			};
			Guild guild = bot.getJda().getGuildById(guildId);
			if (!PermissionUtil.checkPermission(musicChannel, guild.getSelfMember(), Permission.MESSAGE_EMBED_LINKS)) {
				bot.queue.add(musicChannel.sendMessage(MusicUtil.nowPlayingMessageNoEmbed(this, record)), callback);
			} else {
				Member member = null;
				if (scheduler.getLastRequester() != null && !scheduler.getLastRequester().isEmpty()) {
					member = guild.getMemberById(scheduler.getLastRequester());
				}
				bot.queue.add(musicChannel.sendMessage(MusicUtil.nowPlayingMessage(this, record, member)), callback);
			}
		}
	}

	/**
	 * Is connected to boolean.
	 *
	 * @param channel the channel
	 * @return the boolean
	 */
	public boolean isConnectedTo(VoiceChannel channel) {
		return channel != null && channel.equals(channel.getJDA().getGuildById(guildId).getAudioManager().getConnectedChannel());
	}

	/**
	 * Connect to.
	 *
	 * @param channel the channel
	 */
	public synchronized void connectTo(VoiceChannel channel) {
		if (channel != null && !isConnectedTo(channel)) {
			Guild guild = channel.getJDA().getGuildById(guildId);
			guild.getAudioManager().openAudioConnection(channel);
		}
	}

	/**
	 * Is connected boolean.
	 *
	 * @return the boolean
	 */
	public boolean isConnected() {
		Guild guildById = bot.getJda().getGuildById(guildId);
		return guildById != null && guildById.getAudioManager().getConnectedChannel() != null;
	}

	/**
	 * Leave boolean.
	 *
	 * @return the boolean
	 */
	public boolean leave() {
		if (isConnected()) {
			stopMusic();
		}
		Guild guild = bot.getJda().getGuildById(guildId);
		if (guild != null) {
			guild.getAudioManager().closeAudioConnection();
		}
		return true;
	}

	/**
	 * Gets currently playing.
	 *
	 * @return the currently playing
	 */
	public int getCurrentlyPlaying() {
		return this.currentlyPlaying;
	}

	/**
	 * When did the currently playing song start?
	 *
	 * @return timestamp in seconds
	 */
	public long getCurrentSongStartTime() {
		if (!player.isPaused()) {
			return currentSongStartTimeInSeconds;
		}
		return currentSongStartTimeInSeconds + (System.currentTimeMillis() / 1000L - pauseStart);
	}

	/**
	 * track duration of current song
	 *
	 * @return duration in seconds
	 */
	public long getCurrentSongLength() {
		return currentSongLength;
	}

	/**
	 * Unregister vote skip.
	 *
	 * @param user the user
	 */
	public synchronized void unregisterVoteSkip(User user) {
		skipVotes.remove(user);
	}

	/**
	 * Vote skip boolean.
	 *
	 * @param user the user
	 * @return the boolean
	 */
	public synchronized boolean voteSkip(User user) {
		if (skipVotes.contains(user)) {
			return false;
		}
		skipVotes.add(user);
		return true;
	}

	/**
	 * retrieves the amount skip votes
	 *
	 * @return votes vote count
	 */
	public synchronized int getVoteCount() {
		return skipVotes.size();
	}

	/**
	 * Retrieves the amount of required votes in order to skip the track
	 *
	 * @return required votes
	 */
	public synchronized int getRequiredVotes() {
		return Math.max(1,
				(int) (Double.parseDouble(GuildSettings.get(guildId).getOrDefault(GSetting.MUSIC_VOTE_PERCENT)) / 100D * getUsersInVoiceChannel().size()));
	}

	/**
	 * Forcefully Skips the currently playing song
	 */
	public synchronized void forceSkip() {
		scheduler.skipTrack();
	}

	/**
	 * retreives a random file from the music directory
	 *
	 * @return filename OR null when the music table is empty
	 */
	private String getRandomSong() {
		ArrayList<String> potentialSongs = new ArrayList<>();
		if (!playlist.isGlobalList()) {
			return CPlaylist.getNextTrack(playlist.id, playlist.getPlayType());
		}
		try (ResultSet rs = WebDb.get().select("SELECT filename, youtube_title, lastplaydate, youtubecode " + "FROM music " + "WHERE banned = 0 "
				+ "ORDER BY lastplaydate ASC " + "LIMIT 100")) {
			// + "AND play_count > 25 "
			while (rs.next()) {
				potentialSongs.add(rs.getString("youtubecode"));
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
			bot.getContainer().reportError(e);
		}
		if (potentialSongs.isEmpty()) {
			return null;
		}
		return potentialSongs.get(rng.nextInt(potentialSongs.size()));
	}

	/**
	 * Adds a random song from the music directory to the add if a track fails, try
	 * the next one
	 *
	 * @return successfully started playing
	 */
	public synchronized boolean playRandomSong() {
		while (true) {
			String randomSong = getRandomSong();
			if (randomSong != null) {
				if (addToQueue(randomSong, null)) {
					return true;
				}
			} else
				return false;
		}
	}

	/**
	 * Is playing boolean.
	 *
	 * @return the boolean
	 */
	public synchronized boolean isPlaying() {
		return player.getPlayingTrack() != null;
	}

	/**
	 * Start playing.
	 */
	public synchronized void startPlaying() {
		if (!isPlaying()) {
			if (player.isPaused()) {
				player.setPaused(false);
			} else {
				scheduler.skipTrack();
			}
			Launcher.log("Start playing", "music", "start", "guild-id", guildId);
		}
	}

	/**
	 * Add to queue boolean.
	 *
	 * @param filename the filename
	 * @param user     the user
	 * @return the boolean
	 */
	public synchronized boolean addToQueue(String filename, User user) {
		OMusic record = CMusic.findByYoutubeId(filename);
		if (record.id == 0) { // If the record do
			record.youtubecode = filename;
//			CMusic.insert(record); // Create record

			// Get track details and update
			playerManager.loadItemOrdered(player, record.youtubecode, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					if (track.getSourceManager().getSourceName().equals("youtube")) {
						if (record.id == 0 || record.duration == 0) {
							record.artist = track.getInfo().author;
							record.duration = (int) (track.getInfo().length / 1000L);
							record.youtubeTitle = track.getInfo().title;
							CMusic.update(record);
						}
					}
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
				}

				@Override
				public void noMatches() {
				}

				@Override
				public void loadFailed(FriendlyException exception) {
				}
			});

		}
		if (user != null) {
			record.requestedBy = user.getId();
		}
		if (!playlist.isGlobalList() && user != null) {
			Guild guild = user.getJDA().getGuildById(guildId);
			if (playlist.isGuildList() && guild.isMember(user)) {
				switch (playlist.getEditType()) {
				case PRIVATE_AUTO:
					if (!PermissionUtil.checkPermission(guild.getMember(user), Permission.ADMINISTRATOR)) {
						break;
					}
				case PUBLIC_AUTO:
					CPlaylist.addToPlayList(playlist.id, record.id);
				default:
					break;
				}
			} else if (playlist.isPersonal()) {
				switch (playlist.getEditType()) {
				case PRIVATE_AUTO:
					if (playlist.ownerId != CUser.getCachedId(user.getIdLong())) {
						break;
					}
				case PUBLIC_AUTO:
					CPlaylist.addToPlayList(playlist.id, record.id);
					break;
				default:
					break;
				}
			}
		}
		queue.offer(record);
		startPlaying();
		return true;
	}

	/**
	 * Gets volume.
	 *
	 * @return the volume
	 */
	public int getVolume() {
		return player.getVolume();
	}

	/**
	 * Sets volume.
	 *
	 * @param volume the volume
	 */
	public void setVolume(int volume) {
		player.setVolume(volume);
	}

	/**
	 * retrieves a list of users who can listen and use voice commands generating
	 * images is easy to make messy for now
	 *
	 * @return list of users
	 */
	public List<Member> getUsersInVoiceChannel() {
		ArrayList<Member> userList = new ArrayList<>();
		VoiceChannel currentChannel = bot.getJda().getGuildById(guildId).getAudioManager().getConnectedChannel();
		if (currentChannel != null) {
			List<Member> connectedUsers = currentChannel.getMembers();
			userList.addAll(connectedUsers.stream()
					.filter(user -> !user.getUser().isBot() && !user.getVoiceState().isDeafened() && GuildSettings.get(currentChannel.getGuild())
							.canUseMusicCommands(user.getUser(), bot.security.getSimpleRankForGuild(user.getUser(), currentChannel.getGuild())))
					.collect(Collectors.toList()));
		}
		return userList;
	}

	/**
	 * Is there a listener of at least this rank?
	 *
	 * @param rank the rank to be
	 * @return found a user?
	 */
	public boolean aListenerIsAtLeast(SimpleRank rank) {
		VoiceChannel currentChannel = bot.getJda().getGuildById(guildId).getAudioManager().getConnectedChannel();
		if (currentChannel != null) {
			for (Member member : currentChannel.getMembers()) {
				if (member.getVoiceState().isDeafened() || member.getUser().isBot()) {
					continue;
				}
				if (bot.security.getSimpleRank(member.getUser()).isAtLeast(rank)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * check if the player can be paused to start with
	 *
	 * @return if its either playing or already paused
	 */
	public synchronized boolean canTogglePause() {
		return player.getPlayingTrack() != null || player.isPaused();
	}

	/**
	 * toggle paused
	 *
	 * @return true if paused, false otherwise
	 */
	public synchronized boolean togglePause() {
		if (!player.isPaused()) {
			pauseStart = System.currentTimeMillis() / 1000L;
			player.setPaused(true);
		} else {
			currentSongStartTimeInSeconds += (System.currentTimeMillis() / 1000L) - pauseStart;
			player.setPaused(false);
		}
		return player.isPaused();
	}

	/**
	 * Is paused boolean.
	 *
	 * @return the boolean
	 */
	public synchronized boolean isPaused() {
		return player.isPaused();
	}

	/**
	 * Stop music.
	 */
	public synchronized void stopMusic() {
		currentlyPlaying = 0;
		player.destroy();
		Launcher.log("Stop playing", "music", "stop", "guild-id", guildId);
	}

	/**
	 * Gets queue.
	 *
	 * @return the queue
	 */
	public List<OMusic> getQueue() {
		return queue.stream().collect(Collectors.toList());
	}

	/**
	 * Add stream.
	 *
	 * @param url the url
	 */
	public synchronized void addStream(String url) {

	}

	/**
	 * Clear queue.
	 */
	public synchronized void clearQueue() {
		queue.clear();
	}

	/**
	 * Is update channel title boolean.
	 *
	 * @return the boolean
	 */
	public boolean isUpdateChannelTitle() {
		return updateChannelTitle;
	}

	/**
	 * Sets update channel title.
	 *
	 * @param updateChannelTitle the update channel title
	 */
	public void setUpdateChannelTitle(boolean updateChannelTitle) {
		this.updateChannelTitle = updateChannelTitle;
	}

	/**
	 * Gets jda.
	 *
	 * @return the jda
	 */
	public JDA getJDA() {
		return bot.getJda();
	}

	/**
	 * Stop after track.
	 *
	 * @param stopAfter the stop after
	 */
	public synchronized void stopAfterTrack(boolean stopAfter) {
		this.stopAfterTrack = stopAfter;
	}

	/**
	 * The type Track scheduler.
	 */
	public class TrackScheduler extends AudioEventAdapter {
		private final AudioPlayer player;
		private final BlockingQueue<QueuedAudioTrack> queue;
		private volatile String lastRequester = "";

		/**
		 * Instantiates a new Track scheduler.
		 *
		 * @param player the player
		 */
		public TrackScheduler(AudioPlayer player) {
			this.player = player;
			this.queue = new LinkedBlockingQueue<>();
		}

		/**
		 * Queue.
		 *
		 * @param track the track
		 */
		public void queue(QueuedAudioTrack track) {
			if (queue.isEmpty()) {
				lastRequester = track.getUserId();
			}
			if (!player.startTrack(track.getTrack(), true)) {
				queue.offer(track);
			}
		}

		/**
		 * Gets last requester.
		 *
		 * @return the last requester
		 */
		public synchronized String getLastRequester() {
			return lastRequester;
		}

		@Override
		public void onTrackStart(AudioPlayer player, AudioTrack track) {
			trackStarted();
		}

		/**
		 * Skip track.
		 */
		public void skipTrack() {
			trackEnded();
			if (isInRepeatMode() && player.getPlayingTrack() != null) {
				player.startTrack(player.getPlayingTrack().makeClone(), false);
				return;
			}
			player.stopTrack();
			QueuedAudioTrack poll = queue.poll();
			if (poll != null) {
				lastRequester = poll.getUserId();
				player.startTrack(poll.getTrack(), false);
			}
		}

		@Override
		public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
			if (endReason.mayStartNext) {
				if (isInRepeatMode()) {
					player.startTrack(track.makeClone(), false);
					return;
				}
				skipTrack();
			}
		}
	}
}
