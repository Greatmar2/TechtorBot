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

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

/**
 * !say
 * make the bot say something
 */
public class SayCommand extends AbstractCommand {
    public SayCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "repeats you";
    }

    @Override
    public String getCommand() {
        return "say";
    }

    @Override
    public boolean isListed() {
        return false;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"say <anything>"};
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }

    @Override
    public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        if (args.length > 0) {
            String output = Joiner.on(" ").join(args);
            if (DisUtil.isUserMention(output)) {
                if (bot.security.getSimpleRank(author, channel).isAtLeast(SimpleRank.GUILD_ADMIN)) {
                    return output;
                }
                return Templates.command.SAY_CONTAINS_MENTION.formatGuild(channel);
            }
            return output;
        } else {
            return Templates.command.SAY_WHATEXACTLY.formatGuild(channel);
        }
    }
}