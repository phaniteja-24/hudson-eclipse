package dk.contix.eclipse.hudson;

import org.eclipse.swt.graphics.Image;

public class BuildHealth implements Comparable<BuildHealth> {

	private final int health;

	public BuildHealth(int health) {
		this.health = health - (health % 20);
	}
	
	public Image getImage() {
		String imagePath = "icons/health_" + (health == 100 ? 80 : health) + ".png";

		return Activator.getImage(imagePath);
	}
	
	public int hashCode() {
		return 13 * health;
	}
	
	public boolean equals(Object obj) {
		BuildHealth other = (BuildHealth) obj;
		return health == other.health;
	}

	public int compareTo(BuildHealth o) {
		return health - o.health;
	}
	
	public String toString() {
		return "Health: " + health;
	}
}
