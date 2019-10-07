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

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import takeshi.db.controllers.CGuild;
import takeshi.guildsettings.GSetting;
import takeshi.handler.GuildSettings;
import takeshi.main.BotConfig;

/**
 * The type Template.
 */
public class Template {
	final private TemplateArgument[] templateArguments;
	final private TemplateArgument[] optionalArgs;
	private String key;

	/**
	 * Instantiates a new Template.
	 *
	 * @param templateArguments the template arguments
	 */
	public Template(TemplateArgument... templateArguments) {
		this(templateArguments, null);
	}

	/**
	 * Instantiates a new Template.
	 *
	 * @param requiredArguments the required arguments
	 * @param optionalArgs      the optional args
	 */
	public Template(TemplateArgument[] requiredArguments, TemplateArgument[] optionalArgs) {
		if (requiredArguments == null) {
			templateArguments = new TemplateArgument[] {};
		} else {
			templateArguments = requiredArguments;
		}
		if (optionalArgs == null) {
			this.optionalArgs = new TemplateArgument[] {};
		} else {
			this.optionalArgs = optionalArgs;
		}
	}

	/**
	 * Gets key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets key.
	 *
	 * @param key the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Get required arguments template argument [ ].
	 *
	 * @return the template argument [ ]
	 */
	public TemplateArgument[] getRequiredArguments() {
		return templateArguments;
	}

	/**
	 * Is valid template boolean.
	 *
	 * @param template the template
	 * @return the boolean
	 */
	public boolean isValidTemplate(String template) {
		if (template == null) { //|| template.isEmpty()) {
			return false;
		}
		if (templateArguments.length == 0) {
			return true;
		}
		for (TemplateArgument argument : templateArguments) {
			if (!template.contains(argument.getPattern())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Format string.
	 *
	 * @param vars the vars
	 * @return the string
	 */
	public String format(Object... vars) {
		return formatFull(0, false, vars);
	}

	/**
	 * Format guild string.
	 *
	 * @param channel the channel
	 * @param vars    the vars
	 * @return the string
	 */
	public String formatGuild(MessageChannel channel, Object... vars) {
		if (channel.getType().equals(ChannelType.TEXT)) {
			return formatFull(((TextChannel) channel).getGuild().getIdLong(), false, vars);
		}
		return formatFull(0, false, vars);
	}

	/**
	 * Format guild string.
	 *
	 * @param guildId the guild id
	 * @param vars    the vars
	 * @return the string
	 */
	public String formatGuild(long guildId, Object... vars) {
		return formatFull(guildId, false, vars);
	}

	/**
	 * Format full string.
	 *
	 * @param guildId    the guild id
	 * @param forceDebug the force debug
	 * @param vars       the vars
	 * @return the string
	 */
	public String formatFull(long guildId, boolean forceDebug, Object... vars) {
		boolean showTemplates = forceDebug || BotConfig.SHOW_KEYPHRASE;
		if (!forceDebug && guildId > 0) {
			showTemplates = GuildSettings.get(guildId).getBoolValue(GSetting.SHOW_TEMPLATES);
		}
		if (templateArguments.length == 0 && optionalArgs.length == 0) {
			if (showTemplates) {
				return "`" + getKey() + "`";
			}
			if (guildId == 0) {
				return TemplateCache.getGlobal(getKey());
			}
			return TemplateCache.getGuild(CGuild.getCachedId(guildId), getKey());
		}
		TemplateVariables env = TemplateVariables.create(vars);
		if (showTemplates) {
			StringBuilder sb = new StringBuilder();
			sb.append("Template: `").append(getKey()).append("`");
			sb.append("\nAvailable arguments:\n```\n");
			if (templateArguments.length > 0) {
				sb.append("Required:\n\n");
				for (TemplateArgument arg : templateArguments) {
					sb.append(String.format("%-17s -> %s\n", arg.getPattern(), arg.getDescription()));
					sb.append(String.format("%-17s -> %s\n", " |-> value -> ", arg.parse(env)));
				}
			}
			if (optionalArgs.length > 0) {
				sb.append("\nOptional:\n\n");
				for (TemplateArgument arg : optionalArgs) {
					sb.append(String.format("%-17s -> %s\n", arg.getPattern(), arg.getDescription()));
					String var = arg.parse(env);
					if (!var.isEmpty()) {
						sb.append(String.format("%-17s -> %s\n", " |-> value -> ", arg.parse(env)));
					}
				}
			}
			sb.append("```");
			return sb.toString();
		} else {
			String tmp = guildId > 0 ? TemplateCache.getGuild(CGuild.getCachedId(guildId), getKey())
					: TemplateCache.getGlobal(getKey());
			for (TemplateArgument arg : templateArguments) {
				tmp = tmp.replace(arg.getPattern(), arg.parse(env));
			}
			for (TemplateArgument arg : optionalArgs) {
				if (tmp.contains(arg.getPattern())) {
					tmp = tmp.replace(arg.getPattern(), arg.parse(env));
				}
			}
			return tmp;
		}
	}
}
