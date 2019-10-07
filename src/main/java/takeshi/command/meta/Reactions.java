package takeshi.command.meta;

import takeshi.util.Emojibet;

/**
 * The enum Reactions.
 */
public enum Reactions {
	/**
	 * The Star.
	 */
	STAR(ReactionType.USER_INPUT, Emojibet.STAR, "Starboard, See the starboard command for more info"),
	/**
	 * The Skip track.
	 */
	SKIP_TRACK(ReactionType.MUSIC, Emojibet.NEXT_TRACK, "Vote to skip the now playing track"),
    ;

    private final ReactionType reactionType;
    private final String emote;
    private final String description;

    Reactions(ReactionType reactionType, String emote, String description) {

        this.reactionType = reactionType;
        this.emote = emote;
        this.description = description;
    }

	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
        return description;
    }

	/**
	 * Gets emote.
	 *
	 * @return the emote
	 */
	public String getEmote() {
        return emote;
    }

	/**
	 * Gets reaction type.
	 *
	 * @return the reaction type
	 */
	public ReactionType getReactionType() {
        return reactionType;
    }
}
