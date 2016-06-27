package abstractdt;

import java.io.IOException;
import java.util.LinkedList;

import org.w3c.dom.Document;

import com.crawljax.core.CandidateElement;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateVertex;
import com.crawljax.core.state.Eventable.EventType;
import com.crawljax.util.DomUtils;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class MyStateVertexImpl implements StateVertex {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
	private final String dom;
	private final String strippedDom;
	private final String url;
	private String name;
	
	private ImmutableList<CandidateElement> candidateElements;
	
	public MyStateVertexImpl(int id, String name, String dom) {
		this(id, null, name, dom, dom);
	}

	/**
	 * Defines a State.
	 * 
	 * @param url
	 *            the current url of the state
	 * @param name
	 *            the name of the state
	 * @param dom
	 *            the current DOM tree of the browser
	 * @param strippedDom
	 *            the stripped dom by the OracleComparators
	 */
	public MyStateVertexImpl(int id, String url, String name, String dom, String strippedDom) {
		this.id = id;
		this.url = url;
		this.name = name;
		this.dom = dom;
		this.strippedDom = strippedDom;
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
	public int hashCode() {
		return Objects.hashCode(strippedDom);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof StateVertex) {
			StateVertex that = (StateVertex) object;
			return Objects.equal(this.strippedDom, that.getStrippedDom());
		}
		return false;
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
