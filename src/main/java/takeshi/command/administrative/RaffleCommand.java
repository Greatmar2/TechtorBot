/*
 * Copyright 2018 github.com/greatmar2
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

package takeshi.command.administrative;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.utils.PermissionUtil;
import takeshi.command.meta.AbstractCommand;
import takeshi.command.meta.CommandVisibility;
import takeshi.db.controllers.CRaffle;
import takeshi.db.controllers.CRaffleBlacklist;
import takeshi.db.model.ORaffle;
import takeshi.db.model.ORaffleBlacklist;
import takeshi.handler.RaffleHandler;
import takeshi.main.DiscordBot;
import takeshi.permission.SimpleRank;
import takeshi.templates.Templates;
import takeshi.util.DisUtil;
import takeshi.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RaffleCommand extends AbstractCommand {

	public RaffleCommand() {
		super();
	}

	@Override
	public boolean isListed() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Allows for the holding of raffles with randomly-selected winner(s).";
	}

	@Override
	public String getCommand() {
		return "raffle";
	}

	@Override
	public String[] getUsage() {
		return new String[] {"raf list",
				"raf new [#chan] [owner] [prize]    //Creates a new raffle. If a channel is mentioned, the raffle will immediately be started.",
				"raf start <id> [chan] [del]    //Optionally auto-deletes raffle at end. Default: Current channel", "raf end <id>", "raf cancel <id>",
				"raf delete <id>", "raf preview <id>    //Displays a preview of the raffle",
				"raf owner <id> [owner]    //Raffle owner is shown and mentioned at end. Default: Command issuer",
				"raf prize <id> [prize]    //Default: Mystery Prize", "raf description <id> [description]    //Default: None",
				"raf duration <id> [<m/h/d> <time>]    //Example: h 3 Default: No limit",
				"raf entrants <id> [entrants]    //Max entrants before the raffle auto-ends. Default: " + RaffleHandler.MAX_ENTRIES + " (current cap)",
				"raf winners <id> [winners]    //Number of winners selected. Default: 1",
				"raf thumb <id>    //Attatched image displays in the top right. Default: None",
				"raf image <id>    //Attatched image displays at the bottom. Default: None", "raf blacklist [<user> [id] [y/n]]"};
	}

	@Override
	public String[] getAliases() {
		return new String[] {"raf", "rfl"};
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	@Override
	public String simpleExecute(DiscordBot bot, String[] args, MessageChannel channel, User author, Message inputMessage) {
		TextChannel chan = (TextChannel) channel;
		Guild guild = (chan).getGuild();
//		boolean debug = GuildSettings.getBoolFor(chan, GSetting.DEBUG);
		if (!(bot.security.isBotAdmin(author.getIdLong()) || bot.security.getSimpleRank(author, chan).isAtLeast(SimpleRank.BOT_ADMIN))) {
			return Templates.no_permission.formatGuild(guild.getIdLong());
		}
		if (!PermissionUtil.checkPermission(chan, guild.getSelfMember(), Permission.MESSAGE_ADD_REACTION)) {
			return Templates.permission_missing.formatGuild(guild.getIdLong(), Permission.MESSAGE_ADD_REACTION);
		}
		if (args.length == 0) {
			StringBuilder usage = new StringBuilder(
					":gear: **Usage**:\nYou can make a quick-raffle that will instantly start by mentioning a channel with the `new` command. This raffle will be deleted after it completes.\nIf you wish to set up any fields beside the owner and prize (or to keep a raffle after it has ended), you must create a raffle (and copy the new raffle ID) and then set each field individually (using the raffle's ID as a key).\nYou may want to set up a raffle in a hidden bot admin channel, then start the completed raffle in your desired public channel.\nWhen specifying a user, either @mention them, provide their user ID, or provide one word that will be searched.\n```php\n");
			for (String line : getUsage()) {
				usage.append(line).append("\n");
			}
			return usage.toString() + "```";
		}

		if (args.length > 0) {
//			if (debug) {
//				channel.sendMessage("[DEBUG] Args:\n" + Joiner.on("\t").join(args)).queue();
//			}

			ORaffle r = new ORaffle();
			int argStart = 1;

			// New, list and blacklist commands are the only ones that doesn't specify the
			// ID. All others will have the ID after the option.
			if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) { // List existing raffles
				List<ORaffle> raffles = CRaffle.getMessagesForGuild(guild.getIdLong());
				List<List<String>> tableInfo = new ArrayList<>();

				for (ORaffle raffle : raffles) {
					if (raffle != null) {
						Member owner = guild.getMemberById(raffle.ownerId);
						tableInfo.add(Arrays.asList(raffle.id + "", (owner == null ? "" : owner.getEffectiveName()) + "", raffle.prize))
						;//NullPointerException
					}
				}

				return Misc.makeAsciiTable(Arrays.asList("ID", "Owner", "Prize"), tableInfo, null);
			} else if (args[0].equalsIgnoreCase("new") || args[0].equalsIgnoreCase("n")) { // Create new raffle
				// Break apart command and read in appropriate info
				TextChannel startChan = null;
				r.guildId = guild.getIdLong();
				if (args.length > argStart && DisUtil.isChannelMention(args[argStart])) {
					startChan = inputMessage.getMentionedChannels().get(0);
					r.channelId = startChan.getIdLong();
					r.deleteOnEnd = true;
					r.duration = 1;
					argStart++;
				}
				if (args.length > argStart) { // && DisUtil.isUserMention(args[argStart])) {
//					Member owner = inputMessage.getMentionedMembers().get(0);
//					r.ownerId = owner.getUser().getIdLong();
//					argStart++;
					User owner = DisUtil.findUser(chan, args[argStart]);
					if (owner != null) {
						r.ownerId = owner.getIdLong();
						argStart++;
					}
				} else {
					r.ownerId = author.getIdLong();
				}
				if (args.length > argStart) {
					r.prize = Joiner.on(" ").join(Arrays.copyOfRange(args, argStart, args.length));
					if (r.prize.length() > CRaffle.PRIZE_LENGTH) {
						return "Max `" + CRaffle.PRIZE_LENGTH + "` characters for prize name, yours was `" + r.prize.length() + "` characters long.";
					}
				} else {
					r.prize = "Mystery Prize";
				}

				// Insert raffle to database
				r = CRaffle.insert(r);

				// Inform user of actions
				StringBuilder ret = new StringBuilder();
				// Start raffle if necessary
				if (startChan != null) {
					bot.raffleHandler.startRaffle(r, guild);
					ret.append("Created and started (in channel ");
					ret.append(startChan.getAsMention());
					ret.append(") raffle for *");
				} else {
					ret.append("Created raffle for **");
				}
				ret.append(r.prize);
				ret.append("** with ID `");
				ret.append(r.id);
				ret.append("`, beloning to ");
				Member owner = guild.getMemberById(r.ownerId);
				if (owner != null)
					ret.append(owner.getAsMention());//NullPointerException
				return ret.toString();
			} else if (args[0].equalsIgnoreCase("blacklist") || args[0].equalsIgnoreCase("bl")) { // Blacklist user from all or particular raffles
				if (args.length > argStart && DisUtil.isUserMention(args[argStart])) {
					ORaffleBlacklist bl = new ORaffleBlacklist();
					// Only looking for mentions of users to prevent accidental banning
//					User user = inputMessage.getMentionedUsers().get(0);
//					argStart++;

					User user = DisUtil.findUser(chan, args[argStart]);
					if (user == null) {
						return "Can't find user `" + args[argStart] + "`. Only one word is searched, the rest is assumed to be subsequent arguments.";
					} else {
						argStart++;
					}

					bl.guildId = guild.getIdLong();
					bl.userId = user.getIdLong();
					if (args.length > argStart) {
						try {
							bl.raffleId = Integer.parseInt(args[argStart]);
							argStart++;
						} catch (NumberFormatException ex) {
						}

						if (args.length > argStart && Misc.isFuzzyFalse(args[argStart])) {
							bl.currently = false;
						}
					}

					CRaffleBlacklist.safeUpdate(bl);

					if (bl.currently) {
						return "Blacklisted " + user.getAsMention() + " for raffle " + bl.raffleId;
					} else {
						return "Unblacklisted " + user.getAsMention() + " for raffle " + bl.raffleId;
					}
				} else {
					List<ORaffleBlacklist> bls = CRaffleBlacklist.getForGuild(guild.getIdLong());
					List<List<String>> tableInfo = new ArrayList<>();

					for (ORaffleBlacklist bl : bls) {
						tableInfo.add(Arrays.asList(guild.getMemberById(bl.userId).getEffectiveName() + "", bl.raffleId + "", bl.currently + ""));
					}

					return Misc.makeAsciiTable(Arrays.asList("User", "Raffle ID", "Blacklisted"), tableInfo, null)
							+ "Raffle ID 0 applies to all raffles, but is overridden by individual raffles.";
				}
			}

			// There should be an ID after these sub-commands
			if (args.length > 1) {
				try {
					r = CRaffle.findBy(guild.getIdLong(), Integer.parseInt(args[argStart]));
					argStart++;
				} catch (NumberFormatException ex) {
					return Templates.invalid_use.formatGuild(guild.getIdLong()) + " Remember the raffle ID.";
				}

				if (r.id == 0) {
					return "No raffles with this ID found";
				}

				// Figure out which sub-command was issued
				switch (args[0].toLowerCase()) {
					case "s":
					case "start": // raf start <id> [chan] [del]
						if (r.messageId != 0L) {
							return "This raffle has already been started.";
						}
						TextChannel startChan = null;
						r.guildId = guild.getIdLong();
						// Channel
						if (args.length > argStart && DisUtil.isChannelMention(args[argStart])) {
							startChan = inputMessage.getMentionedChannels().get(0);
							argStart++;
						} else {
							startChan = chan;
						}
						r.channelId = startChan.getIdLong();
						// Delete on end
						if (args.length > argStart && (Misc.isFuzzyTrue(args[argStart]) || args[argStart].equalsIgnoreCase("d")
								|| args[argStart].equalsIgnoreCase("del") || args[argStart].equalsIgnoreCase("delete"))) {
							r.deleteOnEnd = true;
						}
						// Start raffle
						bot.raffleHandler.startRaffle(r, guild);
						if (r.messageId == 0L) {
							return "Failed to start raffle `" + r.id + "`.";
						} else {
							return "Started raffle `" + r.id + "` in channel " + startChan.getAsMention();
						}
					case "e":
					case "end": // raf end <id>
						if (r.messageId == 0) {
							return "This raffle has not been started.";
						}
						bot.raffleHandler.endRaffle(r, guild);
						return "Raffle `" + r.id + "` ended";
					case "c":
					case "cancel": // raf cancel <id>
						if (r.messageId == 0) {
							return "This raffle has not been started.";
						}
						bot.raffleHandler.cancelRaffle(r, guild);
						return "Raffle `" + r.id + "` cancelled";
					case "d":
					case "del":
					case "delete": // raf delete <id>
						if (r.messageId != 0L) {
							return "This raffle is currently in progress. First `end` or `cancel` it.";
						}
						CRaffle.delete(r);
						return "Raffle `" + r.id + "` deleted";
					case "p":
					case "pre":
					case "preview": // raf preview <id>
						r.channelId = chan.getIdLong();
						bot.raffleHandler.displayRaffle(r, guild);
						return "";
					case "o":
					case "own":
					case "owner": // raf owner <id> [owner]
//					User owner = author;
//					if (args.length > argStart && DisUtil.isUserMention(args[argStart])) {
//						owner = inputMessage.getMentionedUsers().get(0);
//					}
						User owner = DisUtil.findUser(chan, Joiner.on(" ").join(Arrays.copyOfRange(args, argStart, args.length)));
						if (owner == null) {
							owner = author;
						}
						r.ownerId = owner.getIdLong();
						CRaffle.update(r);
						return "Set owner of raffle `" + r.id + "` to " + owner.getAsMention();
					case "pri":
					case "prize": // raf prize <id> [prize]
						String prize = "Mystery Prize";
						if (args.length > argStart) {
							prize = Joiner.on(" ").join(Arrays.copyOfRange(args, argStart, args.length));
//						if (debug) {
//							channel.sendMessage("[DEBUG] Joiner output: " + prize).queue();
//						}
						}
						if (prize.length() > CRaffle.PRIZE_LENGTH) {
							return "Max `" + CRaffle.PRIZE_LENGTH + "` characters for prize name, yours was `" + prize.length() + "` characters long.";
						}
						r.prize = prize;
						CRaffle.update(r);
						return "Set prize of raffle `" + r.id + "` to **" + prize + "**";
					case "des":
					case "desc":
					case "description": // raf description <id> [description]
						String desc = "";
						if (args.length > argStart) {
							desc = Joiner.on(" ").join(Arrays.copyOfRange(args, argStart, args.length));
						}
						if (desc.length() > CRaffle.DESC_LENGTH) {
							return "Max `" + CRaffle.DESC_LENGTH + "` characters for description, yours was `" + desc.length() + "` characters long.";
						}
						r.description = desc;
						CRaffle.update(r);
						if (desc.length() == 0) {
							return "Removed description from raffle `" + r.id + "`";
						} else {
							return "Set description of raffle `" + r.id + "` to *" + desc + "*";
						}
					case "time":
					case "dur":
					case "duration": // raf duration <id> [<m/h/d> <time>]
						int duration = 0;
						TimeUnit unit = TimeUnit.DAYS;
						if (args.length > argStart) {
							if (args.length > argStart + 1) {
								switch (args[argStart].toLowerCase()) {
									case "m":
										unit = TimeUnit.MINUTES;
										break;
									case "h":
										unit = TimeUnit.HOURS;
										break;
									case "d":
										unit = TimeUnit.DAYS;
										break;
									default:
										return Templates.invalid_use.formatGuild(guild.getIdLong());
								}
								try {
									duration = Integer.parseInt(args[argStart + 1]);
									if (unit == TimeUnit.MINUTES && duration % 60 == 0) {
										unit = TimeUnit.HOURS;
										duration /= 60;
									}
									if (unit == TimeUnit.HOURS && duration % 24 == 0) {
										unit = TimeUnit.DAYS;
										duration /= 24;
									}
									if (duration < 0) {
										return "Duration may not be less than 0";
									}
									if (unit.toDays(duration) > 30) {
										return "Duration may not be more than 30 days";
									}
								} catch (NumberFormatException e) {
									return Templates.invalid_use.formatGuild(guild.getIdLong());
								}
							} else {
								return Templates.invalid_use.formatGuild(guild.getIdLong());
							}
						}
						r.durationUnit = unit;
						r.duration = duration;
						CRaffle.update(r);
						if (duration == 0) {
							return "Removed duration from raffle `" + r.id + "`";
						} else {
							return "Set duration of raffle `" + r.id + "` to " + duration + " " + unit.toString().toLowerCase();
						}
					case "ent":
					case "entries":
					case "entrants": // raf entrants <id> [entrants]
						int entrants = RaffleHandler.MAX_ENTRIES;
						if (args.length > argStart) {
							try {
								entrants = Integer.parseInt(args[argStart]);
							} catch (NumberFormatException e) {
								return Templates.invalid_use.formatGuild(guild.getIdLong());
							}
						}
						if (entrants < r.winners) {
							return "You can't have less entrants than winners!";
						}
						r.entrants = entrants;
						CRaffle.update(r);
						return "Set max entrants of raffle `" + r.id + "` to " + entrants;
					case "win":
					case "wins":
					case "winners": // raf winners <id> [winners]
						int winners = 1;
						if (args.length > argStart) {
							try {
								winners = Integer.parseInt(args[argStart]);
							} catch (NumberFormatException e) {
								return Templates.invalid_use.formatGuild(guild.getIdLong());
							}
						}
						if (winners > r.entrants) {
							return "You can't have more winners than entrants!";
						}
						r.winners = winners;
						CRaffle.update(r);
						return "Set max winners of raffle `" + r.id + "` to " + winners;
					case "th":
					case "thm":
					case "thumb": // raf thumb <id>
						String thumb = "";
						List<Attachment> attsT = inputMessage.getAttachments();
						if (attsT.size() != 0 && attsT.get(0).isImage()) {
							thumb = attsT.get(0).getUrl();
						}
						if (thumb.length() > CRaffle.IMAGE_LENGTH) {
							return "Max `" + CRaffle.IMAGE_LENGTH + "` characters for image URL, yours was `" + thumb.length() + "` characters long.";
						}
						r.thumb = thumb;
						CRaffle.update(r);
						if (thumb.length() == 0) {
							return "Removed thumbnail from raffle `" + r.id + "`";
						} else {
							return "Set thumbnail of raffle `" + r.id + "` to " + thumb;
						}
					case "im":
					case "img":
					case "image": // raf image <id>
						String image = "";
						List<Attachment> attsI = inputMessage.getAttachments();
						if (attsI.size() != 0 && attsI.get(0).isImage()) {
							image = attsI.get(0).getUrl();
						}
						if (image.length() > CRaffle.IMAGE_LENGTH) {
							return "Max `" + CRaffle.IMAGE_LENGTH + "` characters for image URL, yours was `" + image.length() + "` characters long.";
						}
						r.image = image;
						CRaffle.update(r);
						if (image.length() == 0) {
							return "Removed image from raffle `" + r.id + "`";
						} else {
							return "Set image of raffle `" + r.id + "` to " + image;
						}
					default:
						return Templates.invalid_use.format(guild.getIdLong());
				}
			}
		}
		return Templates.invalid_use.formatGuild(guild.getIdLong()) + " Remember the raffle ID.";
	}
}