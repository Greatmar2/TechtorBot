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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.main.DiscordBot;
import takeshi.util.DisUtil;

/**
 * The type Tic tac toe command.
 */
public class TicTacToeCommand extends AbstractCommand {
	/**
	 * Instantiates a new Tic tac toe command.
	 */
	public TicTacToeCommand() {
        super();
    }

    @Override
    public boolean isListed() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Deprecated, see game";
    }

    @Override
    public String getCommand() {
        return "tic";
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                ""};
    }

    @Override
    public CommandVisibility getVisibility() {
        return CommandVisibility.PUBLIC;
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }

    @Override
    public String stringExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        return "The games have been moved to the `" + DisUtil.getCommandPrefix(channel) + "game` command";
    }
}