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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import takeshi.core.AbstractService;
import takeshi.core.ExitCode;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.main.BotConfig;
import takeshi.main.BotContainer;
import takeshi.main.DiscordBot;
import takeshi.main.Launcher;
import takeshi.main.ProgramVersion;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;
import takeshi.util.UpdateUtil;

import java.util.concurrent.TimeUnit;

/**
 * Checks if there is an update for the bot and restarts if there is
 */
public class BotSelfUpdateService extends AbstractService {

    private boolean usersHaveBeenWarned = false;

	/**
	 * Instantiates a new Bot self update service.
	 *
	 * @param b the b
	 */
	public BotSelfUpdateService(BotContainer b) {
        super(b);
    }

    @Override
    public String getIdentifier() {
        return "bot_self_update";
    }

    @Override
    public long getDelayBetweenRuns() {
        return TimeUnit.MINUTES.toMillis(2);
    }

    @Override
    public boolean shouldIRun() {
        return BotConfig.BOT_AUTO_UPDATE && !usersHaveBeenWarned;
    }

    @Override
    public void beforeRun() {
    }

    @Override
    public void run() {
        boolean isUpdating = false;
        ProgramVersion latestVersion = UpdateUtil.getLatestVersion();
        if (latestVersion.isHigherThan(Launcher.getVersion()) || bot.isTerminationRequested()) {
            bot.schedule(() -> {
                if (latestVersion.isHigherThan(Launcher.getVersion())) {
                    Launcher.stop(ExitCode.UPDATE);
                } else if (bot.isTerminationRequested()) {
                    Launcher.stop(bot.getRebootReason());
                } else {
                    Launcher.stop(ExitCode.NEED_MORE_SHARDS);
                }
            }, 1L, TimeUnit.MINUTES);
            usersHaveBeenWarned = true;
            String message = Templates.announce_reboot.format();
            if (latestVersion.isHigherThan(Launcher.getVersion())) {
                message = Templates.bot_self_update_restart.format(Launcher.getVersion().toString(), latestVersion.toString());
                isUpdating = true;
            } else if (bot.isTerminationRequested()) {
                switch (bot.getRebootReason()) {
                    case NEED_MORE_SHARDS:
                        message = Templates.bot_reboot_more_shards.format();
                        break;
                    default:
                        message = Templates.announce_reboot.format();
                }
            }
            for (TextChannel channel : getSubscribedChannels()) {
                sendTo(channel, message);
            }
            for (DiscordBot discordBot : this.bot.getShards()) {
                for (Guild guild : discordBot.getJda().getGuilds()) {
                    String announce = GuildSettings.get(guild).getOrDefault(GSetting.BOT_UPDATE_WARNING);
                    switch (announce.toLowerCase()) {
                        case "off":
                            continue;
                        case "playing":
                            if (!guild.getAudioManager().isConnected()) {
                                break;
                            }
                        case "always":
                            String extraContent = "";
                            TextChannel defaultChannel = discordBot.getDefaultChannel(guild);
                            if (defaultChannel == null || !defaultChannel.canTalk()) {
                                break;
                            }
                            if (isUpdating) {
                                extraContent += "\n\nYou can view the changes with `" + DisUtil.getCommandPrefix(defaultChannel) + "changelog`";
                            }
                            discordBot.out.sendAsyncMessage(defaultChannel, message + extraContent, null);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

    }

    @Override
    public void afterRun() {
    }
}