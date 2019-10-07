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

package takeshi.templates;

import takeshi.main.BotConfig;

/**
 * The enum Template argument.
 */
public enum TemplateArgument {
	/**
	 * The Arg.
	 */
	ARG("arg1", "First input argument", e -> e.arg[0] != null ? e.arg[0] : ""),
	/**
	 * The Arg 2.
	 */
	ARG2("arg2", "Second argument", e -> e.arg[1] != null ? e.arg[1] : ""),
	/**
	 * The Arg 3.
	 */
	ARG3("arg3", "Third argument", e -> e.arg[2] != null ? e.arg[2] : ""),
	/**
	 * The Args.
	 */
	ARGS("allargs", "All input arguments", e -> e.args != null ? e.args : ""),

	/**
	 * User template argument.
	 */
	USER("user", "Username", e -> e.user != null ? e.user.getName() : ""),
	/**
	 * The User mention.
	 */
	USER_MENTION("user-mention", "Mentions user", e -> e.user != null ? e.user.getAsMention() : ""),
	/**
	 * The User id.
	 */
	USER_ID("user-id", "User's id", e -> e.user != null ? e.user.getId() : ""),
	/**
	 * The User descriminator.
	 */
	USER_DESCRIMINATOR("discrim", "Discriminator of the user", e -> e.user != null ? e.user.getDiscriminator() : ""),

	/**
	 * The Nickname.
	 */
	NICKNAME("nick", "Nickname of user", e -> e.user != null && e.guild != null ? e.guild.getMember(e.user) == null ? e.user.getName() : e.guild.getMember(e.user).getEffectiveName() : ""),
	/**
	 * The Guild.
	 */
	GUILD("guild", "Guild name", e -> e.guild != null ? e.guild.getName() : ""),
	/**
	 * The Guild id.
	 */
	GUILD_ID("guild-id", "Guild's id", e -> e.guild != null ? e.guild.getId() : ""),
	/**
	 * The Guild users.
	 */
	GUILD_USERS("guild-users", "Sums guild members", e -> e.guild != null ? Integer.toString(e.guild.getMembers().size()) : ""),

	/**
	 * The Channel.
	 */
	CHANNEL("channel", "Channel name", e -> e.channel != null ? e.channel.getName() : ""),
	/**
	 * The Channel id.
	 */
	CHANNEL_ID("channel-id", "Channel id", e -> e.channel != null ? e.channel.getId() : ""),
	/**
	 * The Channel mention.
	 */
	CHANNEL_MENTION("channel-mention", "Mentions channel", e -> e.channel != null ? e.channel.getAsMention() : ""),

	/**
	 * The Role.
	 */
	ROLE("role", "Role name", e -> e.role != null ? e.role.getName() : ""),
	/**
	 * The Role id.
	 */
	ROLE_ID("role-id", "Role's id", e -> e.role != null ? e.role.getId() : ""),
	/**
	 * The Role mention.
	 */
	ROLE_MENTION("role-mention", "mentions the role", e -> e.role != null ? e.role.isMentionable() ? e.role.getAsMention() : e.role.getName() : ""),;

    private final String pattern;
    private final TemplateParser parser;
    private final String description;

    TemplateArgument(String pattern, String description, TemplateParser parser) {
        this.pattern = BotConfig.TEMPLATE_QUOTE + pattern + BotConfig.TEMPLATE_QUOTE;
        this.parser = parser;
        this.description = description;
    }

	/**
	 * Gets pattern.
	 *
	 * @return the pattern
	 */
	public String getPattern() {
        return pattern;
    }

	/**
	 * Parse string.
	 *
	 * @param vars the vars
	 * @return the string
	 */
	public String parse(TemplateVariables vars) {
        return parser.apply(vars);
    }

	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
        return description;
    }
}
