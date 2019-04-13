package takeshi.games.uno;

/**
 * The type Uno card.
 */
public class UnoCard {
	/**
	 * The Color.
	 */
	final Color color;
	/**
	 * The Value.
	 */
	final Value value;
	/**
	 * The Sel col.
	 */
	Color selCol = null; //Will temporarily override the display of black cards whose colors have been selected

	/**
	 * Instantiates a new Uno card.
	 *
	 * @param color the color
	 * @param value the value
	 */
// Create the UnoCard object
	UnoCard(Color color, Value value) {
		this.color = color;
		this.value = value;
	}

	/**
	 * Gets current color.
	 *
	 * @return the current color
	 */
	Color getCurrentColor() {
		return selCol == null ? color : selCol;
	}

	@Override
	public String toString() {
		return value.toString() + "\n" + getCurrentColor().toString();
	}
}
