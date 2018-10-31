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

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandReactionListener;
import takeshi.command.meta.CommandVisibility;
import takeshi.command.meta.ICommandCleanup;
import takeshi.db.controllers.CMusic;
import takeshi.db.controllers.CPlaylist;
import takeshi.db.model.OMusic;
import takeshi.db.model.OPlaylist;
import takeshi.guildsettings.GSetting;
import takeshi.handler.CommandHandler;
import takeshi.handler.GuildSettings;
import takeshi.handler.MusicPlayerHandler;
import takeshi.main.BotConfig;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.Misc;
import takeshi.util.Pair;
import takeshi.util.YTSearch;
import takeshi.util.YTUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * !play
 * plays a youtube link
 * yea.. play is probably not a good name at the moment
 */
public class PlayCommand extends AbstractCommand implements ICommandCleanup {
    private YTSearch ytSearch;

    public PlayCommand() {
        super();
        ytSearch = new YTSearch();
    }

    public static String processTrack(MusicPlayerHandler player, DiscordBot bot, TextChannel channel, User invoker, String videoCode, String videoTitle, boolean useTemplates) {
        OMusic record = CMusic.findByYoutubeId(videoCode);
        try {
            CMusic.registerPlayRequest(record.id);
            player.addToQueue(videoCode, invoker);
            if (useTemplates) {
                return Templates.music.added_to_queue.formatGuild(channel, record.youtubeTitle);
            }
            return "\u25AA " + record.youtubeTitle;
        } catch (Exception e) {
            bot.getContainer().reportError(e, "ytcode", videoCode);
            return Templates.music.file_error.formatGuild(channel);
        }
    }

    @Override
    public void cleanup() {
        ytSearch.resetCache();
        if (!ytSearch.hasValidKey()) {
            for (String key : BotConfig.GOOGLE_API_KEY) {
                ytSearch.addYoutubeKey(key);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Plays a song from youtube";
    }

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public CommandVisibility getVisibility() {
        return CommandVisibility.PUBLIC;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "play <youtubelink>    //download and plays song",
                "play <part of title>  //shows search results",
                "play                  //just start playing something"
        };
    }

    @Override
    public String[] getAliases() {
        return new String[]{"p"};
    }

    private boolean isInVoiceWith(Guild guild, User author) {
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

    @Override
    public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        TextChannel txt = (TextChannel) channel;
        Guild guild = txt.getGuild();
        SimpleRank userRank = bot.security.getSimpleRank(author, channel);
        GuildSettings guildSettings = GuildSettings.get(guild);
        if (!guildSettings.canUseMusicCommands(author, userRank)) {
            Role role = guild.getRoleById(GuildSettings.getFor(channel, GSetting.MUSIC_ROLE_REQUIREMENT));
            return Templates.music.required_role_not_found.formatGuild(channel, role == null ? "UNKNOWN" : role.getName());
        }

        if (!PermissionUtil.checkPermission(txt, guild.getSelfMember(), Permission.MESSAGE_WRITE)) {
            return "";
        }
        MusicPlayerHandler player = MusicPlayerHandler.getFor(guild, bot);
        if (!isInVoiceWith(guild, author)) {
            VoiceChannel vc = guild.getMember(author).getVoiceState().getChannel();
            if (vc == null) {
                return "you are not in a voicechannel";
            }
            try {
                if (player.isConnected()) {
                    if (!userRank.isAtLeast(SimpleRank.GUILD_ADMIN)) {
                        return Templates.music.not_same_voicechannel.formatGuild(channel);
                    }
                    player.leave();
                }
                if (!PermissionUtil.checkPermission(vc, guild.getSelfMember(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
                    return Templates.music.join_no_permission.formatGuild(channel, vc.getName());
                }
                if (!PermissionUtil.checkPermission(vc, guild.getSelfMember(), Permission.MANAGE_CHANNEL)
                        && vc.getUserLimit() != 0 && vc.getUserLimit() <= vc.getMembers().size()) {
                    return Templates.music.join_channel_full.formatGuild(channel, vc.getName());
                }
                player.connectTo(vc);
            } catch (Exception e) {
                e.printStackTrace();
                return "Can't connect to you";
            }
        } else if (MusicPlayerHandler.getFor(guild, bot).getUsersInVoiceChannel().size() == 0) {
            return Templates.music.no_users_in_channel.formatGuild(channel);
        }
        if (args.length > 0) {
            final String videoTitle;
            String videoCode = YTUtil.isValidYoutubeCode(args[0]) ? args[0] : YTUtil.extractCodeFromUrl(args[0]);
            String playlistCode = YTUtil.getPlayListCode(args[0]);
            if (playlistCode != null) {
                if (!ytSearch.hasValidKey()) {
                    return Templates.music.no_valid_youtube_key.formatGuild(channel, YTUtil.nextApiResetTime());
                }
                if (userRank.isAtLeast(SimpleRank.BOT_ADMIN)) {
                    List<YTSearch.SimpleResult> items = ytSearch.getPlayListItems(playlistCode);
                    int playCount = 0;
                    for (YTSearch.SimpleResult track : items) {
                        processTrack(player, bot, (TextChannel) channel, author, track.getCode(), track.getTitle(), false);
                        if (++playCount == BotConfig.MUSIC_MAX_PLAYLIST_SIZE) {
                            break;
                        }
                    }
                    return String.format("Added **%s** items to the queue", playCount);
                }
            }
            if (!YTUtil.isValidYoutubeCode(videoCode)) {
                if (!ytSearch.hasValidKey()) {
                    return Templates.music.no_valid_youtube_key.formatGuild(channel, YTUtil.nextApiResetTime());
                }
                int maxResultCount = Integer.parseInt(guildSettings.getOrDefault(GSetting.MUSIC_RESULT_PICKER));
                String searchCriteria = Joiner.on(" ").join(args);
                if (maxResultCount > 1 && PermissionUtil.checkPermission(txt, guild.getSelfMember(), Permission.MESSAGE_ADD_REACTION)) {
                    List<YTSearch.SimpleResult> results = ytSearch.getResults(searchCriteria, maxResultCount);
                    String ret = "Results for: " + searchCriteria + "\n\n";
                    int i = 0;
                    final ArrayList<Pair<String, String>> reactions = new ArrayList<>();
                    for (YTSearch.SimpleResult result : results) {
                        ++i;
                        ret += String.format("%s %s\n", Misc.numberToEmote(i), result.getTitle());
                        reactions.add(new Pair<>(Misc.numberToEmote(i), result.getCode()));
                    }
                    ret += "\nYou can pick a song by clicking one of the reactions";
                    txt.sendMessage(ret).queue(msg -> {
                        CommandReactionListener<Integer> listener = new CommandReactionListener<>(author.getIdLong(), null);
                        for (Pair<String, String> reaction : reactions) {
                            listener.registerReaction(reaction.getKey(),
                                    message -> {
                                        listener.disable();
                                        message.editMessage(message.getContentRaw() + "\n\nyou picked " + reaction.getKey()).queue();
                                        AbstractCommand play = CommandHandler.getCommand("play");
                                        if (play != null) {
                                            play.simpleExecute(bot, new String[]{reaction.getValue()}, channel, author, null);
                                        }
                                    });
                        }
                        bot.commandReactionHandler.addReactionListener(guild.getIdLong(), msg, listener);
                    });
                    return "";
                }
                YTSearch.SimpleResult results = ytSearch.getResults(searchCriteria);
                if (results != null) {
                    videoCode = results.getCode();
                    videoTitle = EmojiUtils.shortCodify(results.getTitle());
                } else {
                    videoCode = null;
                    videoTitle = "";
                }
            } else {
                videoTitle = videoCode;
            }
            if (videoCode != null && YTUtil.isValidYoutubeCode(videoCode)) {
                return processTrack(player, bot, (TextChannel) channel, author, videoCode, videoTitle, true);
            } else {
                return Templates.command.play_no_results.formatGuild(channel);
            }
        } else {
            if (player.isPlaying()) {
                if (player.isPaused()) {
                    player.togglePause();
                }
                return "";
            }
            if (player.playRandomSong()) {
                return Templates.music.started_playing_random.formatGuild(channel);
            } else {
                OPlaylist pl = CPlaylist.findById(player.getActivePLaylistId());
                if (!pl.isGlobalList()) {
                    if (CPlaylist.getMusicCount(pl.id) == 0) {
                        return Templates.music.failed_playlist_empty.formatGuild(channel, pl.title);
                    }
                }
                return Templates.music.failed_to_start.formatGuild(channel);
            }
        }
    }
}