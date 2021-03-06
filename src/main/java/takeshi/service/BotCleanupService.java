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

package takeshi.service;

import java.util.concurrent.TimeUnit;

import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.ICommandCleanup;
import takeshi.core.AbstractService;
import takeshi.handler.CommandHandler;
import takeshi.main.BotContainer;
import takeshi.main.DiscordBot;

/**
 * delete cached stuff, etc.
 */
public class BotCleanupService extends AbstractService {
    private int runCount = 0;

	/**
	 * Instantiates a new Bot cleanup service.
	 *
	 * @param b the b
	 */
	public BotCleanupService(BotContainer b) {
        super(b);
    }

    @Override
    public String getIdentifier() {
        return "bot_cleanup_service";
    }

    @Override
    public long getDelayBetweenRuns() {
        return TimeUnit.MINUTES.toMillis(1);
    }

    @Override
    public boolean shouldIRun() {
        return bot.allShardsReady();
    }

    @Override
    public void beforeRun() {
    }

    @Override
    public void run() {
        runCount++;
        for (DiscordBot shard : bot.getShards()) {
            if (shard == null || !shard.isReady()) {
                continue;
            }
            shard.commandReactionHandler.cleanCache();
            shard.chatBotHandler.cleanCache();
            shard.gameHandler.cleanCache();
        }
        if (runCount < 60) {
            return;
        }
        runCount = 0;
        for (AbstractCommand abstractCommand : CommandHandler.getCommandObjects()) {
            if (abstractCommand instanceof ICommandCleanup) {
                ((ICommandCleanup) abstractCommand).cleanup();
            }
        }
    }

    @Override
    public void afterRun() {
    }
}