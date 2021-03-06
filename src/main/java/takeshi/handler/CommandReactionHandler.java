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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import takeshi.command.meta.CommandReactionListener;

/**
 * The type Command reaction handler.
 */
public class CommandReactionHandler {
	private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, CommandReactionListener<?>>> reactions;
//	private boolean lock = false;

	/**
	 * Instantiates a new Command reaction handler.
	 */
	public CommandReactionHandler() {
		reactions = new ConcurrentHashMap<>();
	}

	/**
	 * Add reaction listener.
	 *
	 * @param guildId the guild id
	 * @param message the message
	 * @param handler the handler
	 */
	public void addReactionListener(long guildId, Message message, CommandReactionListener<?> handler) {
		if (handler == null) {
			return;
		}
		if (message.getChannelType().equals(ChannelType.TEXT)) {
			if (!PermissionUtil.checkPermission(message.getTextChannel(), message.getGuild().getSelfMember(), Permission.MESSAGE_ADD_REACTION)) {
				return;
			}
		}
		if (!reactions.containsKey(guildId)) {
			reactions.put(guildId, new ConcurrentHashMap<>());
		}
		if (!reactions.get(guildId).containsKey(message.getIdLong())) {
			for (String emote : handler.getEmotes()) {
//				message.addReaction(emote).complete();
				message.addReaction(emote).queue();
			}
			reactions.get(guildId).put(message.getIdLong(), handler);
		}
	}

	/**
	 * Handles the reaction
	 *
	 * @param channel   TextChannel of the message
	 * @param messageId id of the message
	 * @param userId    id of the user reacting
	 * @param reaction  the reaction
	 * @param adding    the adding
	 */
	public void handle(TextChannel channel, long messageId, long userId, MessageReaction reaction, boolean adding) {
		// long botId = channel.getJDA().getSelfUser().getIdLong();
		// System.out.println("User " + userId + "\tBot" + botId);
		// if (userId != botId) {
		// if (reaction.getCount() == 2) {
//		if (!lock) {
		if (adding) {
			CommandReactionListener<?> listener = reactions.get(channel.getGuild().getIdLong()).get(messageId);
			if (!listener.isActive() || listener.getExpiresInTimestamp() < System.currentTimeMillis()) {
				reactions.get(channel.getGuild().getIdLong()).remove(messageId);
			} else if (listener.hasReaction(reaction.getReactionEmote().getName()) && listener.getUserId() == userId) {
				reactions.get(channel.getGuild().getIdLong()).get(messageId).updateLastAction();
				Message message = channel.retrieveMessageById(messageId).complete();
				listener.react(reaction.getReactionEmote().getName(), message);
			}
			// reaction.removeReaction(channel.getJDA().getUserById(userId)).complete();
//			lock = true;
			// listener.setActive(false);
			reaction.removeReaction(channel.getJDA().getUserById(userId)).queueAfter(10L, TimeUnit.MILLISECONDS);
			// listener.setActive(true);
			// try {
			// wait(1000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// lock = false;
//		} else {
			// reaction.removeReaction();
			// channel.retrieveMessageById(messageId).complete().addReaction(reaction.getReactionEmote().getEmote());
			// Misc.clearReactions(message, userId);
//			lock = false;
		}
//		reaction.removeReaction(channel.getJDA().getUserById(userId)).queueAfter(10L, TimeUnit.MILLISECONDS);
	}

	/**
	 * Do we have an event for a message?
	 *
	 * @param guildId   discord guild-id of the message
	 * @param messageId id of the message
	 * @return do we have an handler?
	 */
	public boolean canHandle(long guildId, long messageId) {
		return reactions.containsKey(guildId) && reactions.get(guildId).containsKey(messageId);
	}

	/**
	 * Remove guild.
	 *
	 * @param guildId the guild id
	 */
	public synchronized void removeGuild(long guildId) {
		reactions.remove(guildId);
	}

	/**
	 * Delete expired handlers
	 */
	public synchronized void cleanCache() {
		long now = System.currentTimeMillis();
		for (Iterator<Map.Entry<Long, ConcurrentHashMap<Long, CommandReactionListener<?>>>> iterator = reactions.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<Long, ConcurrentHashMap<Long, CommandReactionListener<?>>> mapEntry = iterator.next();
			mapEntry.getValue().values().removeIf(listener -> !listener.isActive() || listener.getExpiresInTimestamp() < now);
			if (mapEntry.getValue().values().isEmpty()) {
				reactions.remove(mapEntry.getKey());
			}
		}
	}
}
