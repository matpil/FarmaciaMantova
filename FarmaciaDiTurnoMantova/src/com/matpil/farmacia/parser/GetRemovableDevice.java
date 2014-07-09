package com.matpil.farmacia.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class GetRemovableDevice {

	public static String[] getDirectories() {
		File tempFile;
		String[] directories = null;
		String[] splits;
		ArrayList<String> arrayList = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		String lineRead;

		try {
			arrayList.clear(); // redundant, but what the hey
			bufferedReader = new BufferedReader(new FileReader("/proc/mounts"));

			while ((lineRead = bufferedReader.readLine()) != null) {
//				System.out.println("lineRead: " + lineRead);
				splits = lineRead.split(" ");

				// System external storage
				if (splits[1].equals(Environment.getExternalStorageDirectory().getPath())) {
					arrayList.add(splits[1]);
//					System.out.println("gesd split 1: " + splits[1]);
					continue;
				}

				// skip if not external storage device
				if (!splits[0].contains("/dev/block/")) {
					continue;
				}

				// skip if mtdblock device

				if (splits[0].contains("/dev/block/mtdblock")) {
					continue;
				}

				// skip if not in /mnt node

				if (!splits[1].contains("/mnt")) {
					continue;
				}

				// skip these names

				if (splits[1].contains("/secure")) {
					continue;
				}

				if (splits[1].contains("/mnt/asec")) {
					continue;
				}

				// Eliminate if not a directory or fully accessible
				tempFile = new File(splits[1]);
				if (!tempFile.exists()) {
					continue;
				}
				if (!tempFile.isDirectory()) {
					continue;
				}
				if (!tempFile.canRead()) {
					continue;
				}
				if (!tempFile.canWrite()) {
					continue;
				}

				// Met all the criteria, assume sdcard
				arrayList.add(splits[1]);
			}

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
		}

		// Send list back to caller

		if (arrayList.size() == 0) {
			arrayList.add("sdcard not found");
		}
		directories = new String[arrayList.size()];
		for (int i = 0; i < arrayList.size(); i++) {
			directories[i] = arrayList.get(i);
		}
		return directories;
	}
}