package torgovator.torgovator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import torgovator.utils.ExceptionUtils;

public abstract class Document {
	public static Logger log = Logger.getLogger(App.class.getName());

	private long Id;
	private String FileName = "";

	/** @return the id */
	public long getId() {
		return Id;
	}

	/** @param id
	 *            the id to set */
	public void setId(long id) {
		Id = id;
	}

	/** @return the fileName */
	public String getFileName() {
		return FileName;
	}

	/** @param fileName
	 *            the fileName to set */
	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public java.sql.Timestamp convertToSqlDateTime(Date utilDate) {
		return new java.sql.Timestamp(utilDate.getTime());
	}

	public java.sql.Timestamp StrToDateSql(String returnValues) {
		String format = "yyyy-MM-dd'T'HH:mm:ssXXX";
		if (returnValues.length() == 29) {
			// Если формат с милисекундами			
			format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
		}

		DateFormat formats = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = formats.parse(returnValues);
		} catch (ParseException e) {
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}
		return convertToSqlDateTime(date);
	}
}
