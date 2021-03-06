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

package takeshi.command.administrative;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import takeshi.command.administrative.modactions.AbstractModActionCommand;
import takeshi.db.model.OModerationCase;
import takeshi.main.DiscordBot;

/**
 * The type Temp ban command.
 */
public class TempBanCommand extends AbstractModActionCommand {
    @Override
    public String getDescription() {
        return "Bans a user for a while";
    }

    @Override
    public String getCommand() {
        return "tempban";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    protected OModerationCase.PunishType getPunishType() {
        return OModerationCase.PunishType.TMP_BAN;
    }

    @Override
    protected Permission getRequiredPermission() {
        return Permission.BAN_MEMBERS;
    }

    @Override
    protected boolean punish(DiscordBot bot, Guild guild, Member member) {
        bot.queue.add(guild.ban(member, 5), t -> guild.unban(member.getUser()).complete());
        return true;
    }
}