package abstractdt;

import org.jsoup.nodes.Element;

import utils.UtilsStaticAnalyzer;

public class SubmitField extends InputField {

	public SubmitField(Element singleElement) {
		super(singleElement);
		this.type = "submit";
		this.defaultAction = "click";
		this.isMethod = true;
	}

}
