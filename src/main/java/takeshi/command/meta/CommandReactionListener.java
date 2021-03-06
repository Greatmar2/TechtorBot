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

package takeshi.command.meta;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.Message;

/**
 * The type Command reaction listener.
 *
 * @param <T> the type parameter
 */
public class CommandReactionListener<T> {

	private final LinkedHashMap<String, Consumer<Message>> reactions;
	private final long userId;
	private volatile T data;
	private Long expiresIn, lastAction;
	private boolean active;

	/**
	 * Instantiates a new Command reaction listener.
	 *
	 * @param userId the user id
	 * @param data   the data
	 */
	public CommandReactionListener(long userId, T data) {
		this.data = data;
		this.userId = userId;
		reactions = new LinkedHashMap<>();
		active = true;
		lastAction = System.currentTimeMillis();
		expiresIn = TimeUnit.MINUTES.toMillis(5);
	}

	/**
	 * Is active boolean.
	 *
	 * @return the boolean
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets active.
	 *
	 * @param active the active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Disable.
	 */
	public void disable() {
		this.active = false;
	}

	/**
	 * The time after which this listener expires which is now + specified time
	 * Defaults to now+5min
	 *
	 * @param timeUnit time units
	 * @param time     amount of time units
	 */
	public void setExpiresIn(TimeUnit timeUnit, long time) {
		expiresIn = timeUnit.toMillis(time);
	}

	/**
	 * Check if this listener has specified emote
	 *
	 * @param emote the emote to check for
	 * @return does this listener do anything with this emote?
	 */
	public boolean hasReaction(String emote) {
		return reactions.containsKey(emote);
	}

	/**
	 * React to the reaction :')
	 *
	 * @param emote   the emote used
	 * @param message the message bound to the reaction
	 */
	public void react(String emote, Message message) {
//		System.out.println(emote);
//		System.out.println(reactions.keySet());
//		if (reactions.containsKey(emote)) {
		reactions.get(emote).accept(message);
//		} else if (reactions.containsKey(EmojiUtils.shortCodify(emote))) {
//			reactions.get(EmojiUtils.shortCodify(emote)).accept(message);
//		}
	}

	/**
	 * Gets data.
	 *
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * Sets data.
	 *
	 * @param data the data
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * Register a consumer for a specified emote Multiple emote's will result in
	 * overriding the old one
	 *
	 * @param emote    the emote to respond to
	 * @param consumer the behaviour when emote is used
	 */
	public void registerReaction(String emote, Consumer<Message> consumer) {
		reactions.put(emote, consumer);
	}

	/**
	 * Gets emotes.
	 *
	 * @return list of all emotes used in this reaction listener
	 */
	public Set<String> getEmotes() {
		return reactions.keySet();
	}

	/**
	 * updates the timestamp when the reaction was last accessed
	 */
	public void updateLastAction() {
		lastAction = System.currentTimeMillis();
	}

	/**
	 * When does this reaction listener expire?
	 *
	 * @return timestamp in millis
	 */
	public Long getExpiresInTimestamp() {
		return lastAction + expiresIn;
	}

	/**
	 * Gets user id.
	 *
	 * @return the user id
	 */
	public long getUserId() {
		return userId;
	}
}
