package com.matpil.farmacia.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;

public class AggiornamentoFileDaRemoto {

	private static String urlElencoFarmacie = "https://www.dropbox.com/s/bm4ucj05c2ovh73/ElencoFarmacieMantova.csv?dl=1";
	private static String urlTurniFarmacie = "https://www.dropbox.com/s/yzg9tifisb9wyeu/TurniFarmacieMantova.csv?dl=1";

	public static boolean downloadAndCopyFiles(Context context) {

		try {
			downloadAndStore(context, new URL(urlElencoFarmacie),
					ImportaFarmacieDaFile.NOME_FILE_ELENCO_FARMACIE);
			downloadAndStore(context, new URL(urlTurniFarmacie),
					ImportaFarmacieDaFile.NOME_FILE_TURNI);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void downloadAndStore(Context context, URL url, String path) throws Exception {

		try {
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) url.openConnection();

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Server returned HTTP "
						+ connection.getResponseCode() + " "
						+ connection.getResponseMessage());
				throw new IOException();
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			// int fileLength = connection.getContentLength();

			// download the file
			input = connection.getInputStream();

			// and connect!

			checkDir(context);
			File downloaded = new File(String.format("%s/%s/%s",
					Environment.getExternalStorageDirectory(),
					Environment.DIRECTORY_DOWNLOADS, path));

			// this will be used to write the downloaded data into the file we
			// created
			output = new FileOutputStream(downloaded);

			byte data[] = new byte[4096];
//			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			
			File toImport = new File(String.format("%s/%s",
					ImportaFarmacieDaFile.internalMemoryPath, path));
			System.out.println("toImport -> " + toImport.getAbsolutePath());
			AggiornamentoFileDaSdCard.copyFile(downloaded, toImport);
			downloaded.delete();

			// catch some possible errors...
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

	}

	private static void checkDir(Context context) {
		if (ImportaFarmacieDaFile.internalMemoryPath == null)
			ImportaFarmacieDaFile.internalMemoryPath = context.getFilesDir()
					.getAbsolutePath();
		File dirPath = new File(ImportaFarmacieDaFile.internalMemoryPath);
		if (!dirPath.exists()) {
			dirPath.mkdir();
		}
	}

}
