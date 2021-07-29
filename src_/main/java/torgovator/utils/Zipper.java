package torgovator.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import torgovator.torgovator.App;
import torgovator.torgovator.WorkDir;

/*
 *Распаковываем все файлы из одной папки и складываем в другую папку 
 *
 *
 */
public class Zipper {
	// Подавляет появление конструктора по умолчанию, а заодно и создание экземпляра класса Дж.Блох Статья 5.
	private Zipper() {
	}

	private static Logger log = Logger.getLogger(App.class.getName());

	private static String PatternToExtractRegexp;

	public static void SetPatternToExtractRegexp(String PatternToExtract) {
		PatternToExtractRegexp = PatternToExtract;
	}

	public static boolean unpackZipInDir(WorkDir workdir, String pattern) {
		if ((workdir == null) || (pattern == null)) {
			log.warning("Не заполнен один или несколько обязательных параметров для распаковки");
			return false;
		}

		String sourceDir = workdir.getWorkDirIn();
		String destDir = workdir.getWorkDirTmp();
		String errorDir = workdir.getWorkDirError();

		return unpackZipInDir(sourceDir, destDir, errorDir, pattern);

	}

	/** @param sourceDir
	 *            Папка из которой будем распаковывать
	 * @param destDir
	 *            Папка в которую будем распаковывать
	 * @param errorDir
	 *            Папка с файлами которые не смогли распаковать
	 * @param pattern
	 *            Паттерн(маска) файлов которые нужно извлекать
	 * @return */
	public static boolean unpackZipInDir(String sourceDir, String destDir, String errorDir, String pattern) {
		boolean result = false;

		SetPatternToExtractRegexp(pattern);
		log.info("Разархивирую файлы ");
		File DirIn = new File(sourceDir);

		if (!DirIn.exists()) {
			log.warning("Папка с исходными документами не существует " + sourceDir);
			return result;
		}
		;
		String[] list = DirIn.list(); // Список файлов
		for (String dirItem : list) {
			log.info("Unpack archive " + dirItem);
			try {
				result = Zipper.unpackZip(destDir + "/", sourceDir + "/" + dirItem);
			} catch (IOException e) {
				log.warning(ExceptionUtils.ExceptionStackToString(e));
			}
			;
			if (!result) {
				log.warning("Перемещаю кривой архив в папку ERROR");
				FileUtils.RemoveFileTo(errorDir, sourceDir + "/" + dirItem);
			}
		}

		return result;
	}

	public static boolean unpackZip(String path, String zipname) throws IOException {
		InputStream is;
		ZipInputStream zis;
		is = new FileInputStream(zipname);
		zis = new ZipInputStream(new BufferedInputStream(is));

		try {
			// is = new FileInputStream(path + zipname);
			ZipEntry ze;

			while ((ze = zis.getNextEntry()) != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count;

				String filename = ze.getName();
				if (((PatternToExtractRegexp != "") && (ze.getName().matches(PatternToExtractRegexp)))
						|| (PatternToExtractRegexp == "")) {
					FileOutputStream fout = new FileOutputStream(path + filename);

					// reading and writing
					while ((count = zis.read(buffer)) != -1) {
						baos.write(buffer, 0, count);
						byte[] bytes = baos.toByteArray();
						fout.write(bytes);
						baos.reset();
					}

					fout.close();
					zis.closeEntry();
				}
			}

			zis.close();
		} catch (IOException e) {
			log.warning("Не удалось распаковать архив " + zipname);
			log.warning(e.getLocalizedMessage());

			e.printStackTrace();
			return false;
		} finally {
			zis.close();
		}

		return true;
	}
}
