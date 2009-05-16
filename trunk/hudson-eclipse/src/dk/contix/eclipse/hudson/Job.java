package dk.contix.eclipse.hudson;

/**
 * Representation of a Hudson job.
 * 
 * @author Joakim Recht
 *
 */
public class Job {
	
	private String name;
	private String url;
	private String lastBuild;
	private BuildStatus status;
	private final BuildHealth health;
	
	public Job(String name, String url, String lastBuild, BuildStatus status, BuildHealth health) {
		super();
		this.name = name;
		this.url = url;
		this.lastBuild = lastBuild;
		this.status = status;
		this.health = health;
	}
	public BuildStatus getStatus() {
		return status;
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
	public BuildHealth getHealth() {
		return health;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((status == null) ? 0 : status.hashCode());
		result = PRIME * result + ((lastBuild == null) ? 0 : lastBuild.hashCode());
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((url == null) ? 0 : url.hashCode());
		result = PRIME * result + ((health == null) ? 0 : health.hashCode());
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
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (status != other.status)
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
		if (health == null) {
			if (other.health != null)
				return false;
		} else if (!health.equals(other.health))
			return false;
		return true;
	}
	
	
}
