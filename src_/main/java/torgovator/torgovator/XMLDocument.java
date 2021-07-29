package torgovator.torgovator;

import java.util.logging.Logger;

public abstract class XMLDocument {
	public static Logger log = Logger.getLogger(XMLDocument.class.getName());
	private Object DocToInsert;
	/**
	 * @return the docToInsert
	 */
	public Object getDocToInsert() {
		return DocToInsert;
	}
	/**
	 * @param docToInsert the docToInsert to set
	 */
	public void setDocToInsert(Object docToInsert) {
		DocToInsert = docToInsert;
	}
}
