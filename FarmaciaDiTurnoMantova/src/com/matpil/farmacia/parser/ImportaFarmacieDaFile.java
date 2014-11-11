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
import android.content.SharedPreferences;

import com.matpil.farmacia.model.Farmacia;
import com.matpil.farmacia.model.InfoFarmacie;

public class ImportaFarmacieDaFile {

	protected static String internalMemoryPath = null;
	public final static String POST_IT_SPECIAL = "guardiaMedica";
	public final static String POST_IT_SPECIAL_ROW_1 = "row1";
	public final static String POST_IT_SPECIAL_ROW_2 = "row2";
	public final static String POST_IT_SPECIAL_ROW_3 = "row3";
	public final static String POST_IT_SPECIAL_ROW_4 = "row4";
	public final static String POST_IT_SPECIAL_ROW_5 = "row5";
	protected final static String NOME_FILE_TURNI = "TurniFarmacieMantova.csv";
	protected final static String NOME_FILE_ELENCO_FARMACIE = "ElencoFarmacieMantova.csv";	

	public static Map<String, Farmacia> readPharmFile(Context context) throws IOException {
		checkDir(context);
		BufferedReader in = null;
		Map<String, Farmacia> mapFarmacie = new HashMap<String, Farmacia>();
		try {
			File fileElencoFarmacie = new File(String.format("%s/%s", internalMemoryPath, NOME_FILE_ELENCO_FARMACIE));
			InputStream input = new FileInputStream(fileElencoFarmacie);
			in = new BufferedReader(new InputStreamReader(input, Charset.forName("ISO-8859-1")));
			String line;
			while ((line = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Farmacia pharm = new Farmacia();
					String codice = st.nextToken();
					String farmacia = st.nextToken();
					String località = st.nextToken();
					String indirizzo = st.nextToken();
					String telefono = st.nextToken();
					pharm.setCodice(codice);
					pharm.setNome(farmacia);
					pharm.setLocalità(località);
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
			File fileTurni = new File(String.format("%s/%s", internalMemoryPath, NOME_FILE_TURNI));
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
					farmList.add(specialFarm(context));
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

	private static Farmacia specialFarm(Context context) {
		Farmacia farmacia = new Farmacia();
		final SharedPreferences prefs = context.getSharedPreferences(ImportaFarmacieDaFile.POST_IT_SPECIAL, Context.MODE_PRIVATE);
		String row1 = prefs.getString(ImportaFarmacieDaFile.POST_IT_SPECIAL_ROW_1, "INFO FARMACIE DI TURNO");
		String row2 = prefs.getString(ImportaFarmacieDaFile.POST_IT_SPECIAL_ROW_2, "800 22 85 21");
		String row3 = prefs.getString(ImportaFarmacieDaFile.POST_IT_SPECIAL_ROW_3, "");
		String row4 = prefs.getString(ImportaFarmacieDaFile.POST_IT_SPECIAL_ROW_4, "");
		String row5 = prefs.getString(ImportaFarmacieDaFile.POST_IT_SPECIAL_ROW_5, "");				
		farmacia.setLocalità(row1);
		farmacia.setNome(row2);
		farmacia.setIndirizzo(row3);		
		farmacia.setTelefono(row4);
		farmacia.setNote(row5);
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
