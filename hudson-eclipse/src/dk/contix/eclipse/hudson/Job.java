package dk.contix.eclipse.hudson;

/**
 * Representation of a Hudson job.
 * 
 * @author Joakim Recht
 *
 */
public class Job {
	public static final String BUILD_SUCCESS = "blue";
	public static final String BUILD_FAIL = "red";
	public static final String BUILD_TEST_FAIL = "yellow";
	public static final String BUILD_NO_BUILD = "grey";
	
	private String name;
	private String url;
	private String color;
	private String lastBuild;
	
	public Job(String name, String url, String color, String lastBuild) {
		super();
		this.name = name;
		this.url = url;
		this.color = color;
		this.lastBuild = lastBuild;
	}
	public String getColor() {
		return color;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public String getLastBuild() {
		return lastBuild;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((color == null) ? 0 : color.hashCode());
		result = PRIME * result + ((lastBuild == null) ? 0 : lastBuild.hashCode());
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Job other = (Job) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (lastBuild == null) {
			if (other.lastBuild != null)
				return false;
		} else if (!lastBuild.equals(other.lastBuild))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	
}
