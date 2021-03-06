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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.handler.MusicPlayerHandler;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;

/**
 * !stop
 * make the bot stop playing music
 */
public class StopCommand extends AbstractCommand {
	/**
	 * Instantiates a new Stop command.
	 */
	public StopCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "stops playing music";
    }

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "stop          //stops playing and leaves the channel",
                "stop force    //stops playing and leaves the channel (admin, debug)",
                "stop afternp  //stops and leaves after the now playing track is over",
        };
    }

    @Override
    public CommandVisibility getVisibility() {
        return CommandVisibility.PUBLIC;
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "leave"
        };
    }

    @Override
    public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        Guild guild = ((TextChannel) channel).getGuild();
        SimpleRank userRank = bot.security.getSimpleRank(author, channel);
        if (!GuildSettings.get(guild).canUseMusicCommands(author, userRank)) {
            return Templates.music.required_role_not_found.formatGuild(channel, guild.getRoleById(GuildSettings.getFor(channel, GSetting.MUSIC_ROLE_REQUIREMENT)));
        }
        MusicPlayerHandler player = MusicPlayerHandler.getFor(guild, bot);
        if (args.length > 0) {
            if (args[0].equals("force") && userRank.isAtLeast(SimpleRank.GUILD_ADMIN)) {
                player.leave();
                return Templates.command.stop_success.formatGuild(channel);
            }
        }
        if (!player.isPlaying()) {
            player.leave();
            return Templates.command.currentlyplaying.nosong.formatGuild(channel);
        }
        if (player.isConnected()) {
            if (!player.canUseVoiceCommands(author, userRank)) {
                return Templates.music.not_same_voicechannel.formatGuild(channel);
            }
            if (!userRank.isAtLeast(SimpleRank.GUILD_ADMIN) && player.aListenerIsAtLeast(SimpleRank.GUILD_ADMIN)) {
                return Templates.music.not_while_admin_listening.formatGuild(channel);
            }
            if (args.length > 0 && args[0].equals("afternp")) {
                player.stopAfterTrack(true);
                return Templates.command.stop_after_track.formatGuild(channel);
            } else {
                player.leave();
            }
            return Templates.command.stop_success.formatGuild(channel);
        }
        return Templates.command.currentlyplaying.nosong.formatGuild(channel);

    }
}