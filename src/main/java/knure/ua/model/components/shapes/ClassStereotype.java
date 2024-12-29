package knure.ua.model.components.shapes;

import lombok.Getter;

public enum ClassStereotype {
	None(""),
	Interface("<<Interface>>"),
	Entity("<<Entity>>"),
	Controller("<<Controller>>"),
	Boundary("<<Boundary>>"),
	View("<<View>>"),
	Service("<<Service>>");

	@Getter
	private String stereotype;

	ClassStereotype(String stereotype) {
		this.stereotype = stereotype;
	}
}
