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

package takeshi.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import takeshi.core.Logger;
import takeshi.db.controllers.CAutoRole;
import takeshi.db.model.OAutoRole;
import takeshi.main.DiscordBot;

public class AutoRoleHandler {
	// {guild-id, role-id}}
	private final Map<Long, OAutoRole> listeners;
//	private final DiscordBot discordBot;

	public AutoRoleHandler(DiscordBot discordBot) {
//		this.discordBot = discordBot;
		listeners = new ConcurrentHashMap<>();
	}

	private synchronized boolean isListening(long guildId) {
		return listeners.containsKey(guildId) && listeners.get(guildId).roleId != 0L;
	}

	public synchronized boolean initGuild(long guildId, boolean forceReload) {
		if (!forceReload && listeners.containsKey(guildId)) {
			return true;
		}
		if (forceReload) {
			removeGuild(guildId);
		}
		OAutoRole role = CAutoRole.findBy(guildId);
		if (role.roleId > 0L) {
			listeners.put(guildId, role);
		}

		return false;
	}

	public synchronized void removeGuild(long guildId) {
		if (listeners.containsKey(guildId)) {
			listeners.remove(guildId);
		}
	}

	public synchronized boolean handle(Guild guild, Member member) {
		long guildId = guild.getIdLong();
		initGuild(guildId, false);
		if (!isListening(guildId)) {
			return false;
		}
		OAutoRole setRole = listeners.get(guildId);
		Role role = guild.getRoleById(setRole.roleId);
		if (role == null) {
			Logger.warn(String.format("Could not find role  %s (%s) in guild %s (%s).", setRole.roleName, setRole.roleId, guild.getName(), guild.getIdLong()));
			setRole.roleId = 0;
			CAutoRole.update(setRole);
			return false;
		}
		guild.getController().addSingleRoleToMember(member, role).queue();
		return true;
	}
}
