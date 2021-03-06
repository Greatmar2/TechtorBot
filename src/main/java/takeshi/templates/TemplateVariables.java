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

import java.util.HashMap;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.RoleImpl;
import net.dv8tion.jda.internal.entities.TextChannelImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import takeshi.main.BotContainer;

/**
 * The type Template variables.
 */
public class TemplateVariables {
	/**
	 * The constant EMPTY.
	 */
	public static final TemplateVariables EMPTY = new TemplateVariables();
	private static final HashMap<Class, TemplateVariableParser> mapper = new HashMap<>();

	static {
		init();
	}

	/**
	 * The User.
	 */
	public User user = null;
	/**
	 * The Channel.
	 */
	public TextChannel channel = null;
	/**
	 * The Guild.
	 */
	public Guild guild = null;
	/**
	 * The Role.
	 */
	public Role role = null;
	/**
	 * The Args.
	 */
	public String args = null;
	/**
	 * The Arg.
	 */
	public String[] arg = { null, null, null };

	private static void init() {
		mapper.put(User.class, (var, object) -> var.user = (User) object);
		mapper.put(UserImpl.class, (var, object) -> var.user = (User) object);
		mapper.put(TextChannel.class, (var, object) -> var.channel = (TextChannel) object);
		mapper.put(TextChannelImpl.class, (var, object) -> var.channel = (TextChannel) object);
		mapper.put(Guild.class, (var, object) -> var.guild = (Guild) object);
		mapper.put(GuildImpl.class, (var, object) -> var.guild = (Guild) object);
		mapper.put(Role.class, (var, object) -> var.role = (Role) object);
		mapper.put(RoleImpl.class, (var, object) -> var.role = (Role) object);

		mapper.put(String.class, (var, object) -> {
			if (var.args == null) {
				var.args = (String) object;
			}
			for (int i = 0; i < var.arg.length; i++) {
				if (var.arg[i] == null) {
					var.arg[i] = (String) object;
					break;
				}
			}
		});
		mapper.put(String[].class, (var, object) -> var.args = Joiner.on(" ").join((String[]) object));
	}

	/**
	 * Create template variables.
	 *
	 * @param vars the vars
	 * @return the template variables
	 */
	public static TemplateVariables create(Object... vars) {
		if (vars == null || vars.length == 0) {
			return EMPTY;
		}
		TemplateVariables tmp = new TemplateVariables();
		for (Object var : vars) {
			if (var == null) {
				continue;
			}
			if (mapper.containsKey(var.getClass())) {
				mapper.get(var.getClass()).apply(tmp, var);
			} else {
				BotContainer.LOGGER.warn("[template] UNMAPPED TYPE: " + var.getClass().getSimpleName());
			}
		}
		return tmp;
	}

	private interface TemplateVariableParser {
		/**
		 * Apply.
		 *
		 * @param var the var
		 * @param o   the o
		 */
		void apply(TemplateVariables var, Object o);
	}

}
