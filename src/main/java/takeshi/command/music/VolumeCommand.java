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

import net.dv8tion.jda.api.entities.*;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.handler.MusicPlayerHandler;
import takeshi.main.BotConfig;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;

/**
 * !volume [vol]
 * sets the volume of the music player
 * With no params returns the current volume
 */
public class VolumeCommand extends AbstractCommand {
    public VolumeCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "gets and sets the volume of the music";
    }

    @Override
    public String getCommand() {
        return "volume";
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "volume              //shows current volume",
                "volume <1 to 100>   //sets volume"};
    }

    @Override
    public CommandVisibility getVisibility() {
        return CommandVisibility.PUBLIC;
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "vol"
        };
    }

    @Override
    public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        Guild guild = ((TextChannel) channel).getGuild();
        MusicPlayerHandler player = MusicPlayerHandler.getFor(guild, bot);
        if (args.length > 0) {
            if (GuildSettings.getBoolFor(channel, GSetting.MUSIC_VOLUME_ADMIN) && !bot.security.getSimpleRank(author, channel).isAtLeast(SimpleRank.GUILD_ADMIN)) {
                return Templates.no_permission.formatGuild(channel);
            }
            int volume;
            try {
                volume = Integer.parseInt(args[0]);
                if (volume > 0 && volume <= BotConfig.MUSIC_MAX_VOLUME) {
                    player.setVolume(volume);
                    GuildSettings.get(guild).set(guild, GSetting.MUSIC_VOLUME, String.valueOf(player.getVolume()));
                    return Templates.command.volume_changed.formatGuild(channel, player.getVolume());
                }
            } catch (NumberFormatException ignored) {
            }
            return Templates.command.volume_invalid_parameters.formatGuild(channel);
        }
        return "Current volume: " + player.getVolume() + "%";
    }
}
