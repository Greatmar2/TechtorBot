package takeshi.games.uno;

/**
 * The enum Color.
 */
public enum Color {

	// Enum to handle the colors of the cards

	/**
	 * Red color.
	 */
	RED("❤"),
	/**
	 * Yellow color.
	 */
	YELLOW("💛"),
	/**
	 * Green color.
	 */
	GREEN("💚"),
	/**
	 * Blue color.
	 */
	BLUE("💙"),
	/**
	 * Black color.
	 */
	BLACK("🖤");

	private String unicode;

	@Override
	public String toString() {
		return this.unicode;
	}

	private Color(String unicode) {
		this.unicode = unicode;
	}
}
