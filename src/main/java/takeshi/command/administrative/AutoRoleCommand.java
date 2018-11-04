package takeshi.command.administrative;

import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.db.controllers.CAutoRole;
import takeshi.db.model.OAutoRole;
import takeshi.main.DiscordBot;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;

public class AutoRoleCommand extends AbstractCommand {
	public AutoRoleCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Set a role that will automatically be assigned to users when they join the guild.";
	}

	@Override
	public String getCommand() {
		return "autorole";
	}

	@Override
	public String[] getUsage() {
		return new String[] { "autorole //Displays the current auto-assign role",
				"autorole <role> //Automatically assigns this role to users when they join the guild",
				"autorole remove     //Disable auto-assigning of roles" };
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "autor", "arole", "arl" };
	}

	@Override
	public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		TextChannel tchan = (TextChannel) channel;
		Guild guild = tchan.getGuild();

		if (args.length == 0) {
			OAutoRole guildRole = CAutoRole.findBy(guild.getIdLong());
			if (guildRole.id == 0 || guildRole.roleId == 0) {
				return "No auto role set.";
			}
			Role role = guild.getRoleById(guildRole.roleId);
			if (role == null) {
				return "Auto role was set to a role named `" + guildRole.roleName + "`, but the role can no longer be found.";
			}
			if (!role.getName().equalsIgnoreCase(guildRole.roleName)) {
				guildRole.roleName = role.getName();
				CAutoRole.update(guildRole);
			}
			return "Users will be assigned the role named `" + role.getName() + "` when they join the guild.";
		} else if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
			case "remove":
				OAutoRole guildRole = CAutoRole.findBy(guild.getIdLong());
				if (guildRole.id == 0 || guildRole.roleId == 0) {
					return "No auto role set.";
				}
				guildRole.roleId = 0L;
				CAutoRole.update(guildRole);
				return "Auto role removed.";
			default:
				List<Role> mentionedRoles = inputMessage.getMentionedRoles();
				Role role;
				// Try to find the role for the command
				if (mentionedRoles.isEmpty()) {
					String roleName = "";
					for (int i = 0; i < args.length; i++) {
						roleName += " " + args[i];
					}
					role = DisUtil.findRole(guild, roleName);
				} else {
					role = mentionedRoles.get(0);
				}
				if (role == null) {
					return "Role not found. Make sure the role name contains the words you're using, or that you're @mentioning the role.";
				}
				OAutoRole roleToStore = CAutoRole.findBy(guild.getIdLong());
				roleToStore.roleId = role.getIdLong();
				roleToStore.roleName = role.getName();
				roleToStore.guildId = guild.getIdLong();
				CAutoRole.insert(roleToStore);
				return "Auto role set to " + roleToStore.roleName + ".";
			}
		}

		return Templates.command.invalid_use.formatGuild(channel);
	}
}
