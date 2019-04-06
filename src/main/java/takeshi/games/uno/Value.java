package takeshi.games.uno;

public enum Value {

	// Enum to handle the values of the cards

	ZERO("\u0030\u20E3"), ONE("\u0031\u20E3"), TWO("\u0032\u20E3"), THREE("\u0033\u20E3"), FOUR("\u0034\u20E3"), FIVE("\u0035\u20E3"), SIX("\u0036\u20E3"),
	SEVEN("\u0037\u20E3"), EIGHT("\u0038\u20E3"), NINE("\u0039\u20E3"), REVERSE("♻"), SKIP("🚫"), DRAW("➕"), WILD("🎨");

	private String unicode;

	public String getUnicode() {
		return this.unicode;
	}

	private Value(String unicode) {
		this.unicode = unicode;
	}
}
