package takeshi.command.meta;

import java.util.ArrayList;

/**
 * The enum Reaction type.
 */
public enum ReactionType {
	/**
	 * The User input.
	 */
	USER_INPUT("User reaction to messages", "Users reacting to messages"),
	/**
	 * The Music.
	 */
	MUSIC("Music reactions", "These reactions get placed under the now playing message");

    private final String title;
    private final String description;

    ReactionType(String title, String description) {

        this.title = title;
        this.description = description;
    }

	/**
	 * Gets reactions.
	 *
	 * @return the reactions
	 */
	public ArrayList<Reactions> getReactions() {
        ArrayList<Reactions> r = new ArrayList<>();
        for (Reactions reactions : Reactions.values()) {
            if (reactions.getReactionType() == this) {
                r.add(reactions);
            }
        }
        return r;
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
	 * Gets title.
	 *
	 * @return the title
	 */
	public String getTitle() {
        return title;
    }
}
