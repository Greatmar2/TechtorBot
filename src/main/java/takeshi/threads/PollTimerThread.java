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

package takeshi.threads;

import net.dv8tion.jda.api.entities.Guild;
import takeshi.core.Logger;
import takeshi.handler.PollHandler;

public class PollTimerThread extends Thread {
	private final Guild GUILD;
	private final PollHandler HANDLER;
	private final long WAIT;

	/**
	 * Will wait for specified number of milliseconds, then tell the handler to
	 * check its polls
	 * 
	 * @param handler
	 * @param guild
	 * @param timeToWaitMilli
	 */
	public PollTimerThread(PollHandler handler, Guild guild, long timeToWaitMilli) {
		this.GUILD = guild;
		this.HANDLER = handler;
		this.WAIT = timeToWaitMilli;
	}

	@Override
	public void run() {
//		System.out.println("[DEBUG] Timer thread about to sleep");
		try {
			sleep(WAIT);
//			System.out.println("[DEBUG] Thread awake, checking polls.");
			HANDLER.checkPolls(GUILD);
		} catch (InterruptedException e) {
			Logger.fatal(e, "Poll timer thread for guild " + GUILD.getName() + " interupted! Poll will not end itself.");
		}
	}
}
