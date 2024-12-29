package knure.ua.model.components.shapes;

import lombok.Getter;

public enum AccessLevel {
	Private("-"),
	Protected("#"),
	Public("+");

	@Getter
	private String accessLevel;

	AccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
}
