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

package takeshi.command.adventure;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.main.DiscordBot;
import takeshi.modules.profile.ProfileImageV1;
import takeshi.modules.profile.ProfileImageV3;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

import java.io.File;

/**
 * Profile command
 */
public class ProfileCommand extends AbstractCommand {
    public ProfileCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "Shows your profile in a fancy way";
    }

    @Override
    public String getCommand() {
        return "profile";
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "profile",
                "profile <@user>  //shows the profile of @user"
        };
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "avatar"
        };
    }

    @Override
    public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
        User user = author;
        if (args.length > 0) {
            if (DisUtil.isUserMention(args[0])) {
                user = channel.getJDA().getUserById(DisUtil.mentionToId(args[0]));
            } else {
                Member member = DisUtil.findUserIn((TextChannel) channel, Joiner.on(" ").join(args).toLowerCase());
                if (member != null) {
                    user = member.getUser();
                } else {
                    user = null;
                }
            }
            if (user == null) {
                return Templates.config.cant_find_user.formatGuild(channel, args[0]);
            }
        }
        try {
            File file;
            if (args.length > 0 && args[0].equals("v1")) {
                ProfileImageV1 version1 = new ProfileImageV1(user);
                file = version1.getProfileImage();
            } else {
                ProfileImageV3 version2 = new ProfileImageV3(user);
                file = version2.getProfileImage();
            }
            bot.queue.add(channel.sendFile(file), (message) -> file.delete());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getStackTrace();
            return "Error in creating image :(";
        }
        return "";
    }

    @Override
    public boolean isListed() {
        return false;
    }
}