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

import com.google.common.base.Joiner;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;

/**
 * !changename
 * changes the bots name
 */
public class ChangeName extends AbstractCommand {
    /**
     * Instantiates a new Change name.
     */
    public ChangeName() {
        super();
    }

    @Override
    public String getDescription() {
        return "Changes my name";
    }

    @Override
    public String getCommand() {
        return "changename";
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
    public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        SimpleRank rank = bot.security.getSimpleRank(author);
        if (!rank.isAtLeast(SimpleRank.CREATOR)) {
            return Templates.no_permission.formatGuild(channel);
        }
        if (args.length > 0) {
            bot.setUserName(Joiner.on(" ").join(args));
            return "You can call me **" + Joiner.on(" ").join(args) + "** from now :smile:";
        }
        return ":face_palm: I expected you to know how to use it";
    }
}