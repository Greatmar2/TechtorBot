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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import takeshi.core.Logger;
import takeshi.db.controllers.CReactionRole;
import takeshi.db.model.OReactionRoleKey;
import takeshi.db.model.OReactionRoleMessage;
import takeshi.guildsettings.GSetting;
import takeshi.main.DiscordBot;

public class RoleReactionHandler {
	// {guild-id, {message-id, {emoji, role-id}}
	private final Map<Long, Map<Long, Map<String, Long>>> listeners;
//	private final DiscordBot discordBot;
//	private boolean lock = false;

	public RoleReactionHandler(DiscordBot discordBot) {
//		this.discordBot = discordBot;
		listeners = new ConcurrentHashMap<>();
	}

	public synchronized void addMessage(long guildId, long messageId) {
		if (!listeners.containsKey(guildId)) {
			listeners.put(guildId, new ConcurrentHashMap<>());
		}
		if (!listeners.get(guildId).containsKey(messageId)) {
			listeners.get(guildId).put(messageId, new ConcurrentHashMap<>());
		}
	}

	private synchronized boolean isListening(long guildId, long messageId) {
		return listeners.containsKey(guildId) && listeners.get(guildId).containsKey(messageId);
	}

	public synchronized void removeMessage(long guildId, long id) {
		if (listeners.containsKey(guildId))
			listeners.get(guildId).remove(id);
	}

	public synchronized boolean initGuild(long guildId, boolean forceReload) {
		if (!forceReload && listeners.containsKey(guildId)) {
			return true;
		}
		if (forceReload) {
			removeGuild(guildId);
		}
		List<OReactionRoleKey> keys = CReactionRole.getKeysForGuild(guildId);
		for (OReactionRoleKey key : keys) {
			if (key.messageId <= 0) {
				continue;
			}
			addMessage(guildId, key.messageId);
			List<OReactionRoleMessage> reactions = CReactionRole.getReactionsForKey(key.id);
			for (OReactionRoleMessage r : reactions) {
				addMessageReaction(guildId, key.messageId, r.emoji, r.roleId);
			}
		}

		return false;
	}

	private void addMessageReaction(long guildId, long messageId, String emoji, long roleId) {
		listeners.get(guildId).get(messageId).put(emoji, roleId);
	}

	public synchronized void removeGuild(long guildId) {
		if (listeners.containsKey(guildId)) {
			listeners.remove(guildId);
		}
	}

	private boolean isListeningToReaction(long guildId, long msgId, String emoji) {
		return listeners.get(guildId).get(msgId).containsKey(emoji);
	}

	public synchronized boolean handle(String messageId, TextChannel channel, User invoker, MessageReaction.ReactionEmote emote, boolean isAdding) {
		boolean ret = false;
//		if (!lock) {
//		MessageReaction.ReactionEmote emote = reaction.getReactionEmote();
		long guildId = channel.getGuild().getIdLong();
		long msgId = Long.valueOf(messageId);
		initGuild(guildId, false);
		String theEmote;
		if (emote.getId() == null) {
			theEmote = EmojiUtils.shortCodify(emote.getName());
		} else {
			theEmote = emote.getId();
		}
		if (!isListening(guildId, msgId)) {
			ret = false;
		} else if (isListeningToReaction(guildId, msgId, theEmote)) {
			if (GuildSettings.getBoolFor(channel, GSetting.DEBUG)) {
				channel.sendMessage(String.format("[DEBUG] Detect role reaction `%s`", theEmote)).queue();
			}
			Long roleId = listeners.get(guildId).get(msgId).get(theEmote);
			Role role = channel.getGuild().getRoleById(roleId);
			try {

				if (isAdding) {
					channel.getGuild().addRoleToMember(channel.getGuild().getMember(invoker), role).queue();
					if (GuildSettings.getBoolFor(channel, GSetting.DEBUG)) {
						channel.sendMessage(String.format("[DEBUG] Giving the role '%s' to %s", role.getName(), invoker.getName())).queue();
					}
				} else {
					channel.getGuild().removeRoleFromMember(channel.getGuild().getMember(invoker), role).queue();
					if (GuildSettings.getBoolFor(channel, GSetting.DEBUG)) {
						channel.sendMessage(String.format("[DEBUG] Removing the role '%s' to %s", role.getName(), invoker.getName())).queue();
					}
				}
			} catch (HierarchyException | InsufficientPermissionException e) {
				channel.sendMessage("I cannot manage the role " + role.getName());
				Logger.warn(e.getMessage(), e.getStackTrace());
			}
			ret = true;
		}
//			lock = ret;
//		} else {
//			lock = false;
//		}
//
//		reaction.removeReaction(invoker).queueAfter(10L, TimeUnit.MILLISECONDS);

		return ret;
	}
}
