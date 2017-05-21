package net.tetrakoopa.mdu4j.front.servlet;

import net.tetrakoopa.mdu4j.util.StringUtil;

import javax.servlet.http.HttpServlet;

public abstract class AbstractServlet extends HttpServlet {

	protected final String escape(String string) {
		return escape(string, false);
	}

	/**
	 * @param string, can be null
	 * @param alsoCr si <code>true</code> les retour chariot sont aussi convertis en leur equilvalent HTML
	 * @return <ul><li>null si l'entrée est null</li><li>la chaine échappée en HTML ( à minima &amp;, &gt; et &lt; )</li></ul>
	 */
	protected final String escape(String string, boolean alsoCr) {
		if (string==null) {
			return null;
		}
		return StringUtil.escapeHtml(string, alsoCr);
	}

}
