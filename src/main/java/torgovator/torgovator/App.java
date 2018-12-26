package torgovator.torgovator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.ini4j.InvalidFileFormatException;
import org.xml.sax.SAXException;

import torgovator.utils.Downloader;
import torgovator.utils.EMailService;
import torgovator.utils.ExceptionUtils;
import torgovator.utils.FileUtils;
import torgovator.utils.Zipper;

/** Downloader! */

public class App {

	private static Logger log = Logger.getLogger(App.class.getName());

	public static void main(String[] args) throws InvalidFileFormatException, IOException, SQLException {
		init();

		// Обрабатываю закачанные файлы и хреначу их в базу

		File DirTmp = new File(Params.getWorkDirTorgi().getWorkDirTmp());
		if (!DirTmp.exists()) {
			log.warning(
					"Временная папка отсутствует, делать нечего, уходим " + Params.getWorkDirTorgi().getWorkDirTmp());
			System.exit(2);
		}
		;
		String[] list = DirTmp.list();
		Database data = new Database(Params.getDatabaseFDB());

		for (String dirItem : list) {
			if (!dirItem.contains("nsiOrganization")) {
				try {
					XmlDocumentZakupka Doc = new XmlDocumentZakupka(
							Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
					Doc.zakupka.setFileName(dirItem);
					data.InsertZakupki(Doc.zakupka);

				} catch (ParserConfigurationException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				} catch (SAXException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				}
			} else {
				// Закачиваем организации				
				System.out.println("Закачиваем организации = " + dirItem);
				try {
					XMLnsiOrganizationList OrganizationList = new XMLnsiOrganizationList(
							Params.getWorkDirTorgi().getWorkDirTmp() + "/" + dirItem);
					data.InsertOrg(OrganizationList.OrgList, dirItem);
				} catch (ParserConfigurationException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				} catch (SAXException e) {
					log.warning(ExceptionUtils.ExceptionStackToString(e));
				}

			}

		}

		FileUtils.RemoveAllFileinDirTo(Params.getWorkDirTorgi());

		// Очищаю папку 
		FileUtils.clear_directory(Params.getWorkDirTorgi());

		// Вот тут приделать отправку писем			
		String Mailhost = Params.getMailhost();
		String Mailpasswd = Params.getMailpasswd();
		String Mailfrom = Params.getMailfrom();
		String Mailto = Params.getMailto();
		String MailUsername = Params.getMailUsername();
		String MailMsg = "";
		String MailSubject = "Вероятно наши торги. Уведомление о событиях в закупках";

		MailMsg = getEmailString(data);
		if (MailMsg == "") {
			MailMsg = "Нет торгов по нашим клиентам и по нашим услугам";
		}
		;
		MailMsg = MailMsg + "\r\n Ваш любимый торговый робот";
		if (Params.isSendemail()) {
			EMailService.SendEMail(Mailhost, MailUsername, Mailpasswd, Mailfrom, Mailto, MailMsg, MailSubject);
		}

	};

	private static String getEmailString(Database data) {
		// TODO Хранить в файлах всю эту шнягу		
		String head = "<head>\r\n" + "<style type=\"text/css\" media=\"screen\">\r\n" + "            \r\n"
				+ "            body {\r\n" + "    padding: 0;\r\n" + "    margin: 0;\r\n"
				+ "    background: #f4f4f4;\r\n" + "}\r\n" + "#wrapper {\r\n" + "    background: #f4f4f4;\r\n"
				+ "    color: #222;\r\n" + "    font-family: Roboto, Helvetica, Arial, sans-serif;\r\n"
				+ "    font-size: 15px;\r\n" + "    line-height: 1.5;\r\n" + "    max-width: 600px;\r\n"
				+ "    margin: 0 auto;\r\n" + "}\r\n" + "#wrapper.full {\r\n" + "    max-width: none;\r\n"
				+ "    width: 100%;\r\n" + "}\r\n" + "#wrapper-inner {\r\n" + "    padding: 35px;\r\n" + "}\r\n"
				+ "\r\n" + "#content {\r\n" + "    background: #fff;\r\n" + "    padding: 35px;\r\n" + "}\r\n"
				+ "#after-content {\r\n" + "    background: #f4f4f4;\r\n" + "    font-size: 13px;\r\n"
				+ "    color: #777;\r\n" + "    margin-top: 35px;\r\n" + "}\r\n" + "\r\n" + "#footer {\r\n"
				+ "    color: #777777;\r\n" + "    text-align: center;\r\n" + "    font-size: 13px;\r\n" + "}\r\n"
				+ "#footer .logo img {\r\n" + "    display: block;\r\n" + "}\r\n" + "#footer .logo {\r\n"
				+ "    padding: 0 35px 35px;\r\n" + "}\r\n" + "#footer .copyright {\r\n"
				+ "    border-top: 1px solid #309a44;\r\n" + "    padding: 35px;\r\n" + "}\r\n" + "\r\n"
				+ "a, a:hover {\r\n" + "    text-decoration: underline;\r\n" + "    color: #309a44;\r\n" + "}\r\n"
				+ "\r\n" + "td, tr, th, table {\r\n" + "    padding: 0;\r\n" + "    margin: 0;\r\n" + "}\r\n" + "\r\n"
				+ "b {\r\n" + "    font-weight: bold;\r\n" + "}\r\n" + "\r\n" + "p {\r\n" + "    margin: 15px 0;\r\n"
				+ "}\r\n" + "p:first-child {\r\n" + "    margin-top: 0;\r\n" + "}\r\n" + "p:last-child {\r\n"
				+ "    margin-bottom: 0;\r\n" + "}\r\n" + "\r\n" + "ul li {\r\n" + "    margin-bottom: 15px;\r\n"
				+ "}\r\n" + "\r\n" + "table {\r\n" + "    border-collapse: collapse;\r\n" + "    width: 100%;\r\n"
				+ "    margin-bottom: 15px;\r\n" + "}\r\n" + "tr, td, th {\r\n" + "    border: 1px solid #ddd;\r\n"
				+ "}\r\n" + "td, th {\r\n" + "    padding: 8px 11px;\r\n" + "    vertical-align: top;\r\n" + "}\r\n"
				+ "th {\r\n" + "    font-weight: bold;\r\n" + "}\r\n" + "\r\n" + "h2 {\r\n"
				+ "    margin: 25px 0 15px;\r\n" + "    font-size: 20px;\r\n" + "    font-weight: normal;\r\n" + "}\r\n"
				+ "h2:first-child {\r\n" + "    margin-top: 0;\r\n" + "}\r\n" + "h2:last-child {\r\n"
				+ "    margin-bottom: 0;\r\n" + "}\r\n" + "\r\n" + ".button, .button:hover {\r\n"
				+ "    line-height: 47px;\r\n" + "    padding: 0 25px;\r\n" + "    color: #ffffff;\r\n"
				+ "    text-decoration: none;\r\n" + "    background: #309a44;\r\n" + "    display: inline-block;\r\n"
				+ "    border-radius: 2px;\r\n" + "}\r\n" + "\r\n" + ".table-title {\r\n" + "    color: #ffffff;\r\n"
				+ "    background: #309a44;\r\n" + "    padding: 8px 11px;\r\n" + "    font-weight: bold;\r\n" + "}\r\n"
				+ "\r\n" + "table.vam td {\r\n" + "    vertical-align: middle;\r\n" + "}\r\n" + "\r\n"
				+ "            .notice-tender-link {\r\n" + "    color: #888;\r\n" + "    text-decoration: none;\r\n"
				+ "}\r\n" + "\r\n" + ".rainlab-user-wellcome .main-title {\r\n" + "    font-size: 30px;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .demo {\r\n" + "    background: #309a44;\r\n" + "    padding: 30px 35px;\r\n"
				+ "    color: #ffffff;\r\n" + "    margin: 30px 0;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .demo .datetime {\r\n" + "    font-size: 40px;\r\n"
				+ "    line-height: 1;\r\n" + "    margin-top: 10px;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .demo .datetime .time {\r\n" + "    font-size: 20px;\r\n"
				+ "    margin-left: 3px;\r\n" + "}\r\n" + ".rainlab-user-wellcome .start-search {\r\n"
				+ "    margin: 30px 0;\r\n" + "    text-align: center;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages {\r\n" + "    padding: 25px 0;\r\n"
				+ "    margin: 30px -35px -35px;\r\n" + "    background: #f4f4f4;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .title {\r\n" + "    font-size: 25px;\r\n"
				+ "    text-align: center;\r\n" + "    margin-bottom: 10px;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item {\r\n" + "    margin-bottom: 25px;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item:last-child {\r\n" + "    margin-bottom: 0;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item:after {\r\n" + "    content: '';\r\n"
				+ "    clear: both;\r\n" + "    display: table;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item .img {\r\n" + "    width: 80px;\r\n"
				+ "    height: 80px;\r\n" + "    float: left;\r\n" + "    overflow: hidden;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item .img img {\r\n" + "    height: 80px;\r\n"
				+ "    width: auto;\r\n" + "    display: block;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item .info {\r\n" + "    margin-left: 100px;\r\n"
				+ "    padding-top: 22px;\r\n" + "}\r\n" + ".rainlab-user-wellcome .advantages .item .name {\r\n"
				+ "    font-size: 22px;\r\n" + "    font-weight: 500;\r\n" + "    margin-bottom: 5px;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item .text {\r\n" + "    font-size: 15px;\r\n" + "}\r\n"
				+ ".rainlab-user-wellcome .advantages .item .text ul,\r\n"
				+ ".rainlab-user-wellcome .advantages .item .text li {\r\n" + "    padding: 0;\r\n"
				+ "    margin: 0;\r\n" + "}\r\n" + ".rainlab-user-wellcome .advantages .item .text ul {\r\n"
				+ "    margin-left: 20px;\r\n" + "}\r\n" + ".promocode-box {\r\n" + "    margin: 25px 0;\r\n" + "}\r\n"
				+ ".promocode-box .text {\r\n" + "    padding-left: 120px;\r\n" + "}\r\n"
				+ ".promocode-box:first-child {\r\n" + "    margin-top: 0;\r\n" + "}\r\n"
				+ ".promocode-box:last-child {\r\n" + "    margin-bottom: 0;\r\n" + "}\r\n" + ".promocode-box img {\r\n"
				+ "    float: left;\r\n" + "}\r\n" + "\r\n" + ".date-box {\r\n" + "    background: #309a44;\r\n"
				+ "    color: #ffffff;\r\n" + "    padding: 18px 25px 16px;\r\n" + "    font-size: 18px;\r\n"
				+ "    margin: 25px 0;\r\n" + "}\r\n" + ".date-box:first-child {\r\n" + "    margin-top: 0;\r\n"
				+ "}\r\n" + ".date-box:last-child {\r\n" + "    margin-bottom: 0;\r\n" + "}\r\n" + ".date-box p {\r\n"
				+ "    margin-bottom: 5px;\r\n" + "}\r\n" + ".date-box .value {\r\n" + "    font-size: 38px;\r\n"
				+ "    line-height: 1;\r\n" + "    margin-right: 10px;\r\n" + "}\r\n" + "\r\n" + "\r\n"
				+ "        </style>\r\n"
				+ "<link href=\"https://fonts.googleapis.com/css?family=Roboto:300,300i,400,400i,500,500i,700,700i&amp;subset=cyrillic\" rel=\"stylesheet\">\r\n"
				+ "</head>\r\n" + "";

		String msg = data.getDatasetString("select distinct " + "zk.placingway AS \"Способ размещения\",\r\n"
				+ "cust.purchasecode AS \"Идентификационный код закупки (ИКЗ)\",\r\n"
				+ "zk.href AS \"Ссылка на закупку\",\r\n"
				+ "zk.purchaseobjectinfo AS \"Наименование объекта закупки\",\r\n"
				+ "zk.startdate AS \"Дата и время начала подачи заявок\",\r\n"
				+ "zk.enddate AS \"Дата и время окончания подачи заявок\",\r\n"
				+ "zk.maxprice AS \"Начальная (максимальная) цена контракта\",\r\n"
				+ "zk.responsibleorg_fullname  AS \"Размещение осуществляет\",\r\n"
				+ "SUBSTRING (cust.purchasecode from 4 for 10) \"ИНН Заказчика\", \r\n"
				+ "org.fullname  AS \"Полное наименование Заказчика\",\r\n" + "org.phone  AS \"Телефон\"\r\n"
				+ "/*,cast (SUBSTRING (zk.purchasecode from 4 for 10) as NUMERIC)*/\r\n" + "from\r\n" + "zakupki zk\r\n"
				+ "inner join customerrequirements cust on (zk.id=cust.recordindex)\r\n"
				+ "inner join purchaseobjects po on (zk.id=po.recordindex) left join organization org on (SUBSTRING (cust.purchasecode from 4 for 10)=org.inn and org.actual=1 and org.phone is not null)\r\n"
				+ "\r\n" + "where\r\n" + "(SUBSTRING (cust.purchasecode from 4 for 10) in (\r\n" + "select mo.inn\r\n"
				+ "from my_org mo\r\n" + ") or (zk.inn in\r\n" + "(select mo.inn from my_org mo) ))\r\n" + "\r\n"
				+ "and zk.enddate>=current_timestamp\r\n" + "and (zk.startdate>current_timestamp-1.5)" + "       \r\n"
				+ "and\r\n" + "(\r\n" + "(po.okpd2_code like '58%')\r\n" + "or\r\n" + "(po.okpd2_code like '62%')\r\n"
				+ "or\r\n" + "(po.okpd2_code like '63%')\r\n" + ")\r\n" /*
																		 * + "and\r\n" + "zk.id in\r\n" +
																		 * "(select max(zz.id) from zakupki zz group by zz.purchasenumber)\r\n"
																		 * + "\r\n" + "\r\n" + "order by zk.startdate"
																		 */);
		msg = "<p style=\"margin: 15px 0;\">Здравствуйте! <br>"
				+ "В системе поиска закупок и тендеров произошли новые события. <br>Пожалуйста, ознакомьтесь с изменениями в закупках. </p>"
				+ msg;
		msg = "<a href=\"https://krista-it.ru\" style=\"text-decoration: underline; color: #309a44;\">\r\n"
				+ "        <img src=\"https://krista-it.ru/images/logo_white.png\" alt=\"Криста-Иркутск\" style=\"display: block; margin: 0 auto;\"></a>"
				+ msg;

		return "<html>" + head + "<body>" + msg + "</body>" + "</html>";
	}

	public static void init() {
		try {
			LogManager.getLogManager().readConfiguration(App.class.getResourceAsStream("/logging.properties"));
		} catch (IOException e) {
			log.info(("Could not setup logger configuration: " + e.toString()));
		} catch (NullPointerException e) {
			log.info(("Could not setup logger configuration: " + e.toString()));
		}
		new Params();

		// Скачиваем Торги если не отладка
		if (!Params.isDebug()) {
			Downloader.Download(Params.getWorkDirTorgi(), Params.getDirToDownload(), Params.getFtpUrl(),
					Params.getFtpUserName(), Params.getFtpUserPasswd());
		}
		// Распаковываем
		Zipper.unpackZipInDir(Params.getWorkDirTorgi(), Params.getPatternToExtractRegexp());

	}

}