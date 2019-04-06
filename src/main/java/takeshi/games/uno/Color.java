package takeshi.games.uno;

public enum Color {

	// Enum to handle the colors of the cards

	RED("❤"), YELLOW("💛"), GREEN("💚"), BLUE("💙"), BLACK("🖤");

	private String unicode;

	public String getUnicode() {
		return this.unicode;
	}

	private Color(String unicode) {
		this.unicode = unicode;
	}
}
