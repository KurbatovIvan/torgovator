package torgovator.torgovator;

import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import torgovator.utils.ExceptionUtils;

public class Database {
	private static Logger log = Logger.getLogger(App.class.getName());

	protected Connection conn = null;

	private boolean OrgExist(String regnumber) {
		if (regnumber.isEmpty()) {
			return true;
		}
		;
		String strSQL = "select count(*) as countorg from ORGANIZATION org where org.regnumber='";
		strSQL = strSQL + regnumber + "';";
		ResultSet rs = getDataset(strSQL);
		try {
			int countorg = 0;
			while (rs.next()) {
				countorg = rs.getInt(1);
			}
			if (countorg >= 1) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			log.warning(e.getLocalizedMessage() + "\r\n" + strSQL);
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}

		return false;
	}

	public void InsertOrg(List<Organization> OrgList, String filename) throws SQLException {
		log.info("Вставляем файл в БД: " + filename);
		// Тут будем использовать пакетную вставку
		//		String SQL_INSERT = "INSERT INTO ORGANIZATION (ID, OGRN, KPP, OKTMO, SHORTNAME, FULLNAME, ZIP, URL, POSTALADDRESS, TIMEZONEUTCOFFSET, ORGANIZATIONTYPECODE, ACTUAL, PHONE, EMAIL,FILENAME,REGNUMBER,CONTACTPERSON) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String SQL_INSERT = "EXECUTE PROCEDURE orginsert (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement statement = conn.prepareStatement(SQL_INSERT);
			long start = System.currentTimeMillis();

			for (int index = 0; index <= OrgList.size() - 1; index++) {
				// Проверяем нужно ли вставлять
				//			
				//				if (!OrgExist(OrgList.get(index).getRegNumber()))
				{
					if ((OrgList.get(index).getOrganizationTypeCode() == null)
							|| (OrgList.get(index).getRegNumber() == null)) {
						log.warning("Какое то поле пустое");
					}

					if (OrgList.get(index).getOGRN().length() > 20) {
						log.warning("Поле ОГРН больше 20 знаков");
					}
					if (OrgList.get(index).getKPP().length() > 9) {
						log.warning("Поле КПП больше 9 знаков");
					}
					if (OrgList.get(index).getShortName().length() > 500) {
						log.warning("Поле краткое имя больше 500 знаков и имеет размер="
								+ OrgList.get(index).getShortName().length());
						log.warning(OrgList.get(index).getShortName());
					}
					if (OrgList.get(index).getFullName().length() > 1000) {
						log.warning("Поле полное имя больше 1000 знаков");
					}

					if (OrgList.get(index).getOKTMO().length() > 20) {
						log.warning("Поле ОКТМО больше 20 знаков");
					}
					if (OrgList.get(index).getZip().length() > 6) {
						log.warning("Поле ZIP больше 6 знаков");
					}

					if (OrgList.get(index).getContactPerson().length() > 150) {
						log.warning("Поле ContactPerson больше 150 знаков");
					}
					if (OrgList.get(index).getRegNumber().length() > 11) {
						log.warning("Поле RegNumber больше 11 знаков");
					}
					if (OrgList.get(index).getEmail().length() > 100) {
						log.warning("Поле Email больше 11 знаков");
					}
					if (OrgList.get(index).getPhone().length() > 150) {
						log.warning("Поле Phone больше 11 знаков");
					}
					if (OrgList.get(index).getPostalAddress().length() > 255) {
						log.warning("Поле PostalAddress больше 255 знаков");
					}
					statement.setLong(1, OrgList.get(index).getId());
					statement.setString(2, OrgList.get(index).getOGRN());
					statement.setString(3, OrgList.get(index).getKPP());
					statement.setString(4, OrgList.get(index).getOKTMO());
					statement.setString(5, OrgList.get(index).getShortName());
					statement.setString(6, OrgList.get(index).getFullName());
					statement.setString(7, OrgList.get(index).getZip());
					statement.setString(8, "");
					statement.setString(9, OrgList.get(index).getPostalAddress());
					statement.setString(10, "");
					statement.setInt(11, OrgList.get(index).getOrganizationTypeCode()); // 
					statement.setInt(12, OrgList.get(index).isActual());
					statement.setString(13, OrgList.get(index).getPhone());
					statement.setString(14, OrgList.get(index).getEmail());
					statement.setString(15, filename);
					statement.setString(16, OrgList.get(index).getRegNumber());
					statement.setString(17, OrgList.get(index).getContactPerson());
					statement.addBatch();

				}

			}
			statement.executeBatch();
			long end = System.currentTimeMillis();

			log.info("Total time taken to insert the batch = " + (end - start) + " ms");
			if (OrgList.size() != 0) {
				log.info("total time taken = " + (end - start) / OrgList.size() + " ms");
			}

			statement.close();

		}

		catch (DataTruncation e) {
			log.warning("DataTruncation DataSize" + e.getDataSize() + "\r\n " + "DataTruncation TransferSize"
					+ e.getTransferSize() + "\r\n " + "DataTruncation LocalizedMessage" + e.getLocalizedMessage());
		}

		catch (SQLException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			log.warning(e.getLocalizedMessage());
			log.warning("SQLState " + e.getSQLState());
			log.warning(ExceptionUtils.ExceptionStackToString(e));

		}

	}

	public void InsertZakupki(Zakupka zakupka) throws SQLException {
		String SQL_INSERT = "INSERT INTO ZAKUPKI (FILENAME,purchaseNumber,OKDP2_CODE,OKPD2_NAME,purchaseObjectInfo, INN, Regnum,HREF,startDate,EndDate,MAXPRICE,PLACINGWAY,responsibleOrg_fullName,PUBLISHDATE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);) {
			try {
				statement.setString(1, zakupka.getFileName());
				statement.setString(2, zakupka.getPurchaseNumber());
				statement.setString(3, "");
				statement.setString(4, "");
				statement.setString(5, zakupka.getPurchaseObjectInfo());
				statement.setString(6, zakupka.getINN());
				statement.setString(7, zakupka.getRegnum());
				statement.setString(8, zakupka.getHref());
				//			statement.setDate(9, (java.sql.Date) zakupka.getStartDate());
				statement.setTimestamp(9, (java.sql.Timestamp) zakupka.getStartDate());
				statement.setTimestamp(10, (java.sql.Timestamp) zakupka.getEndDate());
				statement.setDouble(11, zakupka.getMaxPrice());
				statement.setString(12, zakupka.getPlacingWay());
				statement.setString(13, zakupka.getResponsibleOrg_fullName());
				statement.setTimestamp(14, (java.sql.Timestamp) zakupka.getPublishDate());

				int affectedRows = statement.executeUpdate();

				if (affectedRows == 0) {
					throw new SQLException("Insert Zakupka failed, no rows affected.");
				}
			} catch (SQLException e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
				log.warning(e.getLocalizedMessage() + "\r\n" + SQL_INSERT);
				log.warning(ExceptionUtils.ExceptionStackToString(e));

			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					zakupka.setId((generatedKeys.getLong(1)));
					log.info("KEY=" + zakupka.getId());
					// Вставляем CUSTOMERREQUIREMENTS
					InsertCUSTOMERREQUIREMENTS(zakupka);
					InsertPURCHASEOBJECTS(zakupka);
				} else {
					throw new SQLException("Insert Zakupka failed, no ID obtained.");
				}
			}
		}
	}

	private void InsertPURCHASEOBJECTS(Zakupka zakupka) throws SQLException {
		String SQL_INSERT = "INSERT INTO PURCHASEOBJECTS (RECORDINDEX,OKPD2_CODE) VALUES (?,?)";
		try (PreparedStatement statement = conn.prepareStatement(SQL_INSERT, Statement.NO_GENERATED_KEYS);) {

			List<String> OKPD2 = zakupka.getOKPD2();
			for (int i = 0; i < OKPD2.size(); i++) {
				String OKPD2Str = OKPD2.get(i);
				statement.setLong(1, zakupka.getId());
				if (OKPD2Str.length() > 12) {
					OKPD2Str = OKPD2Str.substring(1, 12);
				}
				statement.setString(2, OKPD2Str);

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Insert PURCHASEOBJECTS failed, no rows affected.");
				}
			}
		}
	}

	private void InsertCUSTOMERREQUIREMENTS(Zakupka zakupka) throws SQLException {
		String SQL_INSERT = "INSERT INTO CUSTOMERREQUIREMENTS (RECORDINDEX,PURCHASECODE) VALUES (?,?)";
		try (PreparedStatement statement = conn.prepareStatement(SQL_INSERT, Statement.NO_GENERATED_KEYS);) {

			List<String> purchasecode1 = zakupka.getPURCHASECODE1();
			for (int i = 0; i < purchasecode1.size(); i++) {
				String PURCHASECODE = purchasecode1.get(i);
				statement.setLong(1, zakupka.getId());
				statement.setString(2, PURCHASECODE);

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Insert CUSTOMERREQUIREMENTS failed, no rows affected.");
				}
			}
		}
	}

	public boolean InsertToDataset(String strSQL) {
		try {
			Statement st = conn.createStatement();
			st.executeUpdate(strSQL);
			st.close();
		} catch (Exception e) {
			log.warning(ExceptionUtils.ExceptionStackToString(e));
			log.warning(strSQL);
		}
		return true;
	}

	/** @param strSQL
	 *            - SQL Запрос
	 * @return - возвращает строки со столбцами из СтатикСета
	 * @throws SQLException
	 */
	public String getDatasetString(String strSQL) throws SQLException {
		//        Statement.KEEP_CURRENT_RESULT;
		String messageResult = "";
		ResultSet rs = getDataset(strSQL);
		System.out.println("Дата сет закрыт1: " + rs.isClosed());
		ResultSetMetaData rsmd = rs.getMetaData();
		int nColumnsCount;

		try {
			nColumnsCount = rs.getMetaData().getColumnCount();
			while (rs.next()) {

				for (int n = 1; n < nColumnsCount + 1; n++) {
					Object obj = rs.getObject(n);
					if (obj != null) {
						String ColumnName = rsmd.getColumnLabel(n);
						obj = HtmlURL(obj.toString());
						messageResult = messageResult + (ColumnName + ": " + obj + "\r\n <br>");
					}
				}

				messageResult = messageResult + "\r\n <br>";
				messageResult = messageResult + "---------------------------\r\n <br>";
			}
		} catch (SQLException e) {
			log.warning(ExceptionUtils.ExceptionStackToString(e));
			log.warning(strSQL);
		}

		return messageResult;
	}

	private static String HtmlURL(String url) {
		if (url.isEmpty())
			return url;

		if (url.substring(0, 4).equals("http")) {
			return "<a href=" + url + ">" + url + "</a>";
		}
		if (url.contains("Бюджет")) {
			return "<b>" + url + "</b>";
		}
		if (url.contains("Смета")) {
			return "<b>" + url + "</b>";
		}
		if (url.contains("УРМ")) {
			return "<b>" + url + "</b>";
		} else {
			return url;
		}
	}

	/** @param SQL
	 *            Запрос по которому нужно вернуть статиксет
	 * @return ResultSet */
	public ResultSet getDataset(String strSQL) {
		/*
		 * By default, only one ResultSet object per Statement object can be open at the
		 * same time. Therefore, if the reading of one ResultSet object is interleaved
		 * with the reading of another, each must have been generated by different
		 * Statement objects. All execution methods in the Statement interface
		 * implicitly close a statment's current ResultSet object if an open one exists.
		 */
		ResultSet rs = null;
		// Создаём класс, с помощью которого будут выполняться
		// SQL запросы.
		try {
			Statement stmt = conn.createStatement();
			System.out.println("База данных открыта: " + conn.isClosed());
			// Выполняем SQL запрос.
			rs = stmt.executeQuery(strSQL);

		} catch (SQLException e) {
			log.warning("SQL: " + strSQL);
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}
		return rs;
	}

	public ResultSet getDatasetDetail(String strSQL) {

		ResultSet rsDetail = null;
		// Создаём класс, с помощью которого будут выполняться
		// SQL запросы.
		try {
			Statement stmtDetail = conn.createStatement();
			System.out.println("База данных открыта: " + conn.isClosed());
			// Выполняем SQL запрос.
			rsDetail = stmtDetail.executeQuery(strSQL);

		} catch (SQLException e) {
			log.warning("SQL: " + strSQL);
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}
		return rsDetail;
	}

	public Database() {

		//		String strDatabasePath = "D:\\!Work\\torgovator\\Database\\TORGOVATOR.FDB";
		String strDatabasePath = Params.getDatabaseFDB();
		log.info("Path to Database " + strDatabasePath);
		Properties props = new Properties();

		props.setProperty("user", Params.getDatabaseUser());
		props.setProperty("password", Params.getDatabasePasswd());
		props.setProperty("encoding", Params.getDatabaseencoding());

		// По этому URL будет происходить подключение к базе данных.
		// Обратите внимание: URL содержит путь к базе данных.
		// URL действителен для firebird. Для других СУБД он будет другим.
		String strURL = "jdbc:firebirdsql:" + strDatabasePath;

		/*
		 * try { // Инициализируемя Firebird JDBC driver. // Эта строка действительна //
		 * // только для Firebird. // Для других СУБД она будет немного видоизменена.
		 * Class.forName("org.firebirdsql.jdbc.FBDriverd").newInstance(); } catch
		 * (IllegalAccessException ex) { ex.printStackTrace(); } catch
		 * (InstantiationException ex) { ex.printStackTrace(); } catch
		 * (ClassNotFoundException ex) { ex.printStackTrace(); }
		 */

		try {
			// Создаём подключение к базе данных
			// conn = DriverManager.getConnection(strURL, strUser, strPassword);
			conn = DriverManager.getConnection(strURL, props);

			if (conn == null) {
				System.err.println("Could not connect to database");
				log.warning("Could not connect to database");
			}
		} catch (SQLException e) {
			log.warning("Не смог подключится к базе данных !");
			log.warning(ExceptionUtils.ExceptionStackToString(e));

		}

	}

}
