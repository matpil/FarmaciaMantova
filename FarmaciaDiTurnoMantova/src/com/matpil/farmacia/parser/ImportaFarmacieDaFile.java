package com.matpil.farmacia.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.content.Context;

import com.matpil.farmacia.model.Farmacia;
import com.matpil.farmacia.model.InfoFarmacie;

public class ImportaFarmacieDaFile {

	protected final static String NOME_FILE_TURNI = "TurniFarmacieMantova.csv";
	protected final static String NOME_FILE_ELENCO_FARMACIE = "ElencoFarmacieMantova.csv";
	protected static String sdCardPath = null;
	protected static String internalMemoryPath = null;

	static {
		String[] directories = GetRemovableDevice.getDirectories();
		for (int i = 0; i < directories.length; i++) {
//			System.out.println(String.format("directory %s -> %s", i, directories[i]));

			sdCardPath = directories[i]; //Environment.getExternalStorageDirectory().getAbsolutePath();
			File elencoFarmacie = new File(String.format("%s/%s", sdCardPath, NOME_FILE_ELENCO_FARMACIE));
			File fileTurni = new File(String.format("%s/%s", sdCardPath, NOME_FILE_TURNI));
//			System.out.println("elencoFarmacie: " + elencoFarmacie.getAbsolutePath());
//			System.out.println("fileTurni: " + fileTurni.getAbsolutePath());
			if (elencoFarmacie.exists() && fileTurni.exists())
				break;
		}
	}

	public static Map<String, Farmacia> readTextFile(Context context) throws IOException {
		checkDir(context);
		BufferedReader in = null;
		Map<String, Farmacia> mapFarmacie = new HashMap<String, Farmacia>();
		try {
			File fileElencoFarmacie = new File(String.format("%s/%s", sdCardPath, NOME_FILE_ELENCO_FARMACIE));
			InputStream input = new FileInputStream(fileElencoFarmacie);
			in = new BufferedReader(new InputStreamReader(input, Charset.forName("ISO-8859-1")));
			String line;
			while ((line = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Farmacia pharm = new Farmacia();
					String codice = st.nextToken();
					String farmacia = st.nextToken();
					String localitÓ = st.nextToken();
					String indirizzo = st.nextToken();
					String telefono = st.nextToken();
					pharm.setCodice(codice);
					pharm.setNome(farmacia);
					pharm.setLocalitÓ(localitÓ);
					pharm.setIndirizzo(indirizzo);
					pharm.setTelefono(telefono);
					// System.out.println(pharm);
					mapFarmacie.put(codice, pharm);
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
		return mapFarmacie;
	}

	private static void checkDir(Context context) {
		if (internalMemoryPath == null)
			internalMemoryPath = context.getFilesDir().getAbsolutePath();
		File dirPath = new File(internalMemoryPath);
		if (!dirPath.exists()) {
			dirPath.mkdir();
		}
	}

	public static Map<String, InfoFarmacie> readTurniFile(Context context, Map<String, Farmacia> pharmMap) {
		checkDir(context);
		BufferedReader in = null;
		Map<String, InfoFarmacie> mapTurniFarmacie = new HashMap<String, InfoFarmacie>();
		try {
			File fileTurni = new File(String.format("%s/%s", sdCardPath, NOME_FILE_TURNI));
			String pathFile = fileTurni.getPath();
			InputStream input = new FileInputStream(pathFile);
			in = new BufferedReader(new InputStreamReader(input, Charset.forName("ISO-8859-1")));
			String line;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				StringTokenizer st = new StringTokenizer(line, ";", true);
				List<Farmacia> farmList = new ArrayList<Farmacia>();
				while (st.hasMoreTokens()) {
					String data = checkToken(st);
					String ora = checkToken(st);
					String farm1 = checkToken(st);
					String note1 = checkToken(st);
					String farm2 = checkToken(st);
					String note2 = checkToken(st);
					String farm3 = checkToken(st);
					String note3 = checkToken(st);
					String farm4 = checkToken(st);
					String note4 = checkToken(st);
					String farm5 = checkToken(st);
					String note5 = checkToken(st);
					String farm6 = checkToken(st);
					String note6 = checkToken(st);
					String farm7 = checkToken(st);
					String note7 = checkToken(st);
					String farm8 = checkToken(st);
					String note8 = checkToken(st);
					String farm9 = checkToken(st);
					String note9 = checkToken(st);
					String farm10 = checkToken(st);
					String note10 = checkToken(st);
					String farm11 = checkToken(st);
					String note11 = checkToken(st);
					farmList.add(addPharm(pharmMap, farm1, note1));
					farmList.add(addPharm(pharmMap, farm3, note3));
					farmList.add(addPharm(pharmMap, farm4, note4));
					farmList.add(addPharm(pharmMap, farm5, note5));
					farmList.add(addPharm(pharmMap, farm2, note2));
					farmList.add(addPharm(pharmMap, farm6, note6));
					farmList.add(addPharm(pharmMap, farm7, note7));
					farmList.add(addPharm(pharmMap, farm8, note8));
					farmList.add(specialFarm());
					farmList.add(addPharm(pharmMap, farm9, note9));
					farmList.add(addPharm(pharmMap, farm10, note10));
					farmList.add(addPharm(pharmMap, farm11, note11));
					InfoFarmacie info = new InfoFarmacie();
					info.setListPharm(farmList);
					info.setTimeUpdate(ora);
					mapTurniFarmacie.put(data, info);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
		return mapTurniFarmacie;
	}

	private static Farmacia addPharm(Map<String, Farmacia> pharmMap, String farm, String note) {
		Farmacia clone = pharmMap.get(farm);
		Farmacia toAdd = null;
		if (clone != null) {
			toAdd = new Farmacia(clone);
			toAdd.setNote(note);
		}
		return toAdd;
	}

	private static Farmacia specialFarm() {
		Farmacia farmacia = new Farmacia();
		farmacia.setLocalitÓ("INFO FARMACIE DI");
		farmacia.setNome("TURNO N. VERDE");
		farmacia.setIndirizzo("800228521");
		farmacia.setTelefono("");
		return farmacia;
	}

	private static String checkToken(StringTokenizer st) {
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (";".equals(token)) {
				return "";
			} else {
				if (st.hasMoreTokens())
					st.nextToken();
				return token;
			}
		}
		return null;
	}

	public static boolean existPharmListFile(Context context) {
		checkDir(context);
		File elencoFarmacie = new File(String.format("%s/%s", internalMemoryPath, NOME_FILE_ELENCO_FARMACIE));
//		System.out.println("PATH -> " + elencoFarmacie.getPath());
		return elencoFarmacie.exists();
	}

	public static boolean existScheduleFile(Context context) {
		checkDir(context);
		File fileTurni = new File(String.format("%s/%s", internalMemoryPath, NOME_FILE_TURNI));
//		System.out.println("PATH -> " + fileTurni.getPath());
		return fileTurni.exists();
	}

}
