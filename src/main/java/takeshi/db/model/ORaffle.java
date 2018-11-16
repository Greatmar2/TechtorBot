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

public class ORaffle extends AbstractModel {
	public int id = 0;
	public long guildId = 0L;
	public long ownerId = 0L;
	public String prize = "Mystery Prize";
	public String description = "";
	public int duration = 0;
	public TimeUnit durationUnit = TimeUnit.DAYS;
	public int entrants = RaffleHandler.MAX_ENTRIES;
	public int winners = 1;
	public String thumb = "";
	public String image = "";
	// These are only used when a raffle has started
	public long channelId = 0L;
	public long messageId = 0L;
	public Timestamp raffleEnd = null;
	public boolean deleteOnEnd = false;
}