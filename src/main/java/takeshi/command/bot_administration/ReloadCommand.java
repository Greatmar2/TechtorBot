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

package takeshi.command.bot_administration;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.handler.SecurityHandler;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;

/**
 * !reload
 * reloads config
 */
public class ReloadCommand extends AbstractCommand {
    public ReloadCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "reloads the configuration";
    }

    @Override
    public String getCommand() {
        return "reload";
    }

    @Override
    public String[] getUsage() {
        return new String[]{};
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }

    @Override
    public CommandVisibility getVisibility() {
        return CommandVisibility.PUBLIC;
    }

    @Override
    public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        Guild guild = ((TextChannel) channel).getGuild();
        SimpleRank rank = bot.security.getSimpleRank(author, channel);
        if (rank.isAtLeast(SimpleRank.BOT_ADMIN)) {
            SecurityHandler.initialize();
            return Templates.command.reload.success.formatGuild(channel);
        }
        if (rank.isAtLeast(SimpleRank.GUILD_ADMIN)) {
            bot.loadGuild(guild);
            return Templates.command.reload.success.formatGuild(channel);
        }
        return Templates.no_permission.formatGuild(channel);
    }
}