package abstractdt;

import java.io.IOException;
import java.util.LinkedList;

import org.w3c.dom.Document;

import utils.UtilsClustering;

import com.crawljax.condition.eventablecondition.EventableCondition;
import com.crawljax.core.CandidateElement;
import com.crawljax.core.state.StateVertex;
import com.crawljax.util.DomUtils;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class StateVertexLevensteinEquals implements StateVertex {

	private static final long serialVersionUID = 1L;
	private int id;
	private String dom;
	private String strippedDom;
	private String url;
	private String name;
	private ImmutableList<CandidateElement> candidateElements;
	
	public StateVertexLevensteinEquals(int id, String url, String name, String dom, String strippedDom) {
		this.id = id;
		this.url = url;
		this.name = name;
		this.dom = dom;
		this.strippedDom = strippedDom;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(strippedDom);
		//return 1;
	}

	@Override
	public boolean equals(Object object) {
		//return false;
		
		if (object instanceof StateVertex) {
			StateVertex that = (StateVertex) object;	
			
			if(this.getDom().equals(that.getDom())){
				return true;
			}
			else if(this.getUrl().equals(that.getUrl())){
				return UtilsClustering.levenshteinEquals(1.00, this.getDom(), that.getDom());
			}
		}
		return false;
		
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDom() {
		return dom;
	}

	@Override
	public String getStrippedDom() {
		return strippedDom;
	}

	@Override
	public String getUrl() {
		return url;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public Document getDocument() throws IOException {
		return DomUtils.asDocument(this.dom);
	}

	@Override
	public void setElementsFound(LinkedList<CandidateElement> elements) {
		this.candidateElements = ImmutableList.copyOf(elements);
	}

	@Override
	public ImmutableList<CandidateElement> getCandidateElements() {
		return candidateElements;
	}

}
