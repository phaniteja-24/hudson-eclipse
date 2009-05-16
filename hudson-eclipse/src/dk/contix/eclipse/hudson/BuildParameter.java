package dk.contix.eclipse.hudson;

public class BuildParameter {
	private String name;
	private String value;

	public BuildParameter(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public BuildParameter() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
