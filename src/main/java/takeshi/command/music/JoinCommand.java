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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.handler.MusicPlayerHandler;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;
import takeshi.util.Misc;

/**
 * !joinme
 * make the bot join the channel of the user
 */
public class JoinCommand extends AbstractCommand {
	/**
	 * Instantiates a new Join command.
	 */
	public JoinCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "joins a voicechannel";
    }

    @Override
    public String getCommand() {
        return "join";
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "join                //attempts to join you",
                "join <channelname>  //attempts to join channelname"
        };
    }

    @Override
    public CommandVisibility getVisibility() {
        return CommandVisibility.PUBLIC;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        TextChannel chan = (TextChannel) channel;
        MusicPlayerHandler player = MusicPlayerHandler.getFor(chan.getGuild(), bot);
        if (args.length == 0) {
            VoiceChannel voiceChannel = chan.getGuild().getMember(author).getVoiceState().getChannel();
            if (voiceChannel == null) {
                return Templates.command.join.cantfindyou.formatGuild(channel);
            }
            if (player.isConnectedTo(voiceChannel)) {
                return Templates.command.join.already_there.formatGuild(channel);
            }
            if (!PermissionUtil.checkPermission(voiceChannel, voiceChannel.getGuild().getSelfMember(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
                return Templates.music.join_no_permission.formatGuild(channel, voiceChannel.getName());
            }
            player.connectTo(voiceChannel);
            return Templates.command.join.joinedyou.formatGuild(channel);
        } else {
            String channelname = Misc.concat(args);
            VoiceChannel targetChannel = null;
            for (VoiceChannel vc : chan.getGuild().getVoiceChannels()) {
                if (vc.getName().equalsIgnoreCase(channelname)) {
                    targetChannel = vc;
                    break;
                }
            }
            if (targetChannel != null) {
                if (player.isConnectedTo(targetChannel)) {
                    return Templates.command.join.already_there.formatGuild(channel);
                }
                if (!PermissionUtil.checkPermission(targetChannel, targetChannel.getGuild().getSelfMember(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
                    return Templates.music.join_no_permission.formatGuild(channel, targetChannel.getName());
                }
                player.leave();
                player.connectTo(targetChannel);
//					return Template.get("command_join_nopermssiontojoin");
                return Templates.command.join.joinedyou.formatGuild(channel);
            }
            return Templates.command.join.cantfindyou.formatGuild(channel);
        }
    }
}