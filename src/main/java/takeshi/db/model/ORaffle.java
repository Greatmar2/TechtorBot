/*
 * Copyright 2018 Greatmar2
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

package takeshi.db.model;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import takeshi.db.AbstractModel;
import takeshi.handler.RaffleHandler;

/**
 * The type O raffle.
 */
public class ORaffle extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Guild id.
	 */
	public long guildId = 0L;
	/**
	 * The Owner id.
	 */
	public long ownerId = 0L;
	/**
	 * The Prize.
	 */
	public String prize = "Mystery Prize";
	/**
	 * The Description.
	 */
	public String description = "";
	/**
	 * The Duration.
	 */
	public int duration = 0;
	/**
	 * The Duration unit.
	 */
	public TimeUnit durationUnit = TimeUnit.DAYS;
	/**
	 * The Entrants.
	 */
	public int entrants = RaffleHandler.MAX_ENTRIES;
	/**
	 * The Winners.
	 */
	public int winners = 1;
	/**
	 * The Thumb.
	 */
	public String thumb = "";
	/**
	 * The Image.
	 */
	public String image = "";
	/**
	 * The Channel id.
	 */
// These are only used when a raffle has started
	public long channelId = 0L;
	/**
	 * The Message id.
	 */
	public long messageId = 0L;
	/**
	 * The Raffle end.
	 */
	public Timestamp raffleEnd = null;
	/**
	 * The Delete on end.
	 */
	public boolean deleteOnEnd = false;
}