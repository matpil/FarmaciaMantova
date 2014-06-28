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
import android.os.Environment;
import android.widget.Toast;

import com.matpil.farmacia.model.Farmacia;

public class ImportaFarmacieDaFile {

	private final static String NOME_FILE_TURNI = "TurniFarmacieMantova.csv";
	private final static String NOME_FILE_ELENCO_FARMACIE = "ElencoFarmacieMantova.csv";
	private final static File sdcard = Environment.getExternalStorageDirectory();
	private final static String sdCardPath = String.format("%s/FARMACIAPP", Environment.getExternalStorageDirectory());

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
		// System.out.println("---------- CARICATE " + mapFarmacie.size() +
		// " FARMACIE ---------");
		return mapFarmacie;
	}

	private static void checkDir(Context context) {
		File dirPath = new File(sdCardPath);
		if (!dirPath.exists()) {
			boolean mkdir = dirPath.mkdir();
			if (!mkdir)
				Toast.makeText(context, "CARTELLA "+sdCardPath+" NON CREATA", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(context, "DIRECTORY CREATA, path -> " + sdCardPath, Toast.LENGTH_LONG).show();
		}
	}

	public static Map<String, List<Farmacia>> readTurniFile(Context context, Map<String, Farmacia> pharmMap) {
		checkDir(context);
		BufferedReader in = null;
		Map<String, List<Farmacia>> mapTurniFarmacie = new HashMap<String, List<Farmacia>>();
		try {
			// System.out.println("FILE_NAME -> " + NOME_FILE_TURNI);
			File fileTurni = new File(String.format("%s/%s", sdCardPath, NOME_FILE_TURNI));
			String pathFile = fileTurni.getPath();
			InputStream input = new FileInputStream(pathFile);

			in = new BufferedReader(new InputStreamReader(input, Charset.forName("ISO-8859-1")));
			String line;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				StringTokenizer st = new StringTokenizer(line, ";", true);
				List<Farmacia> farmList = new ArrayList<Farmacia>();
				if (st.hasMoreTokens()) {
					String data = checkToken(st);
					checkToken(st); // ora
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

//					if (data.equals("22/06/2014")) {
//					System.out.println(String.format("<%s>", data));
//					System.out.println(String.format("\t<%s> note: <%s>", farm1, note1));
//					System.out.println(String.format("\t<%s> note: <%s>", farm2, note2));
//					System.out.println(String.format("\t<%s> note: <%s>", farm3, note3));
//					System.out.println(String.format("\t<%s> note: <%s>", farm4, note4));
//					System.out.println(String.format("\t<%s> note: <%s>", farm5, note5));
//					System.out.println(String.format("\t<%s> note: <%s>", farm6, note6));
//					System.out.println(String.format("\t<%s> note: <%s>", farm7, note7));
//					System.out.println(String.format("\t<%s> note: <%s>", farm8, note8));
//					System.out.println(String.format("\t<%s> note: <%s>", farm9, note9));
//					System.out.println(String.format("\t<%s> note: <%s>", farm10, note10));
//					System.out.println(String.format("\t<%s> note: <%s>", farm11, note11));
//					}

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
					mapTurniFarmacie.put(data, farmList);
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
		Farmacia toAdd = pharmMap.get(farm);
		if (toAdd != null) {
			toAdd = clonaPharm(toAdd);
			toAdd.setNote(note);
//			System.out.println(String.format("Aggiunta nota %s per codice %s", note, toAdd.getCodice()));
		}
		return toAdd;
	}

	private static Farmacia clonaPharm(Farmacia farmacia) {
		Farmacia pharm = new Farmacia();
		pharm.setCodice(farmacia.getCodice());
		pharm.setIndirizzo(farmacia.getIndirizzo());
		pharm.setLocalità(farmacia.getLocalità());
		pharm.setNome(farmacia.getNome());
		pharm.setNote(farmacia.getNote());
		pharm.setTelefono(farmacia.getTelefono());
		return pharm;
	}

	private static Farmacia specialFarm() {
		Farmacia farmacia = new Farmacia();
		farmacia.setLocalità("INFO FARMACIE DI");
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
		File fileTurni = new File(String.format("%s/FARMACIAPP/%s", sdcard.getPath(), NOME_FILE_ELENCO_FARMACIE));
//		System.out.println("PATH -> " + fileTurni.getPath());
		return fileTurni.exists();
	}

	public static boolean existScheduleFile(Context context) {
		checkDir(context);
		File fileTurni = new File(String.format("%s/FARMACIAPP/%s", sdcard.getPath(), NOME_FILE_TURNI));
//		System.out.println("PATH -> " + fileTurni.getPath());
		return fileTurni.exists();
	}
}
