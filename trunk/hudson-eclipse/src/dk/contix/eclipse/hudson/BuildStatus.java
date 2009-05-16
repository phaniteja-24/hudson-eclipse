package dk.contix.eclipse.hudson;

import org.eclipse.swt.graphics.Image;


public enum BuildStatus {
	
	SUCCESS("blue"),
	FAIL("red"),
	TEST_FAIL("yellow"),
	NO_BUILD("grey");
	
	private final String code;
	
	private BuildStatus(String code) {
		this.code = code;
	}

	public static BuildStatus getStatus(String code) {
		code = code.toLowerCase();
		if (code.endsWith("_anime")) {
			code = code.substring(0, code.indexOf("_anime"));
		}
		for (BuildStatus b : values()) {
			if (b.code.equals(code)) {
				return b;
			}
		}
		throw new IllegalArgumentException("No status constant for value " + code);
	}
	
	public Image getImage() {
		return Activator.getImage("icons/" + code + ".png");
	}
}
