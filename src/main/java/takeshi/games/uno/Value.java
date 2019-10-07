package takeshi.games.uno;

/**
 * The enum Value.
 */
public enum Value {

	// Enum to handle the values of the cards

	/**
	 * Zero value.
	 */
	ZERO("\u0030\u20E3"),
	/**
	 * One value.
	 */
	ONE("\u0031\u20E3"),
	/**
	 * Two value.
	 */
	TWO("\u0032\u20E3"),
	/**
	 * Three value.
	 */
	THREE("\u0033\u20E3"),
	/**
	 * Four value.
	 */
	FOUR("\u0034\u20E3"),
	/**
	 * Five value.
	 */
	FIVE("\u0035\u20E3"),
	/**
	 * Six value.
	 */
	SIX("\u0036\u20E3"),
	/**
	 * Seven value.
	 */
	SEVEN("\u0037\u20E3"),
	/**
	 * Eight value.
	 */
	EIGHT("\u0038\u20E3"),
	/**
	 * Nine value.
	 */
	NINE("\u0039\u20E3"),
	/**
	 * Reverse value.
	 */
	REVERSE("♻"),
	/**
	 * Skip value.
	 */
	SKIP("🚫"),
	/**
	 * Draw value.
	 */
	DRAW("➕"),
	/**
	 * Wild value.
	 */
	WILD("🎨");

	private String unicode;

	private Value(String unicode) {
		this.unicode = unicode;
	}

	@Override
	public String toString() {
		return this.unicode;
	}
}
