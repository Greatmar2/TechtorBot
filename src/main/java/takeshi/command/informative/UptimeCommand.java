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

package takeshi.command.informative;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;
import takeshi.util.TimeUtil;

/**
 * The type Uptime command.
 */
public class UptimeCommand extends AbstractCommand {
	/**
	 * Instantiates a new Uptime command.
	 */
	public UptimeCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "How long am I running for?";
    }

    @Override
    public String getCommand() {
        return "uptime";
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
        return Templates.command.uptime.upfor.formatGuild(channel, TimeUtil.getRelativeTime(bot.startupTimeStamp, false));
    }
}