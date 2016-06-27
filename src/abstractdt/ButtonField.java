package abstractdt;

import org.jsoup.nodes.Element;

import utils.UtilsStaticAnalyzer;

public class ButtonField extends InputField {

	public ButtonField(Element singleElement) {
		super(singleElement);
		this.type = "button";
		this.defaultAction = "click";
		this.isMethod = true;
	}

}
