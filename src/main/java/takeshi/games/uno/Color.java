package takeshi.games.uno;

public enum Color {

	// Enum to handle the colors of the cards

	RED("❤"), YELLOW("💛"), GREEN("💚"), BLUE("💙"), BLACK("🖤");

	private String unicode;

	@Override
	public String toString() {
		return this.unicode;
	}

	private Color(String unicode) {
		this.unicode = unicode;
	}
}
