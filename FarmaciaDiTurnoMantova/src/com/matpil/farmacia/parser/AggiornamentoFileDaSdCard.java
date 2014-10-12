package com.matpil.farmacia.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.widget.Toast;

public class AggiornamentoFileDaSdCard {
	
	protected static String sdCardPath = null;
	
	static {
		String[] directories = GetRemovableDevice.getDirectories();
		for (int i = 0; i < directories.length; i++) {
//			System.out.println(String.format("directory %s -> %s", i, directories[i]));

			sdCardPath = directories[i]; //Environment.getExternalStorageDirectory().getAbsolutePath();
			File elencoFarmacie = new File(String.format("%s/%s", sdCardPath, ImportaFarmacieDaFile.NOME_FILE_ELENCO_FARMACIE));
			File fileTurni = new File(String.format("%s/%s", sdCardPath, ImportaFarmacieDaFile.NOME_FILE_TURNI));
//			System.out.println("elencoFarmacie: " + elencoFarmacie.getAbsolutePath());
//			System.out.println("fileTurni: " + fileTurni.getAbsolutePath());
			if (elencoFarmacie.exists() && fileTurni.exists())
				break;
		}
	}	
	
	public static boolean copyFiles(Context context) {
		boolean copyOk = false;
		File turniSdCard = new File(String.format("%s/%s", sdCardPath, ImportaFarmacieDaFile.NOME_FILE_TURNI));
		File elencoFarmacieSdCard = new File(String.format("%s/%s", sdCardPath, ImportaFarmacieDaFile.NOME_FILE_ELENCO_FARMACIE));
		try {
//			System.out.println("CHECK FILE -> " + (turniSdCard.exists() && elencoFarmacieSdCard.exists()));
			if (turniSdCard.exists() && elencoFarmacieSdCard.exists()) {
				copyFile(turniSdCard, new File(String.format("%s/%s", ImportaFarmacieDaFile.internalMemoryPath, ImportaFarmacieDaFile.NOME_FILE_TURNI)));
				copyFile(elencoFarmacieSdCard, new File(String.format("%s/%s", ImportaFarmacieDaFile.internalMemoryPath, ImportaFarmacieDaFile.NOME_FILE_ELENCO_FARMACIE)));
				copyOk = true;
			} else {
				Toast.makeText(context, "Mancano uno o più file dati", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "ERRORE -> " + e, Toast.LENGTH_LONG).show();
			copyOk = false;
		}
		return copyOk;
	}

	protected static void copyFile(File src, File dst) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(src);
		FileChannel inChannel = fileInputStream.getChannel();
		FileOutputStream fileOutputStream = new FileOutputStream(dst);
		FileChannel outChannel = fileOutputStream.getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
			if (fileInputStream != null)
				fileInputStream.close();
			if (fileOutputStream != null)
				fileOutputStream.close();
		}
	}

}
