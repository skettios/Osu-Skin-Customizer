package com.skettios.osc.client;

import java.io.File;
import java.util.ArrayList;

public class AddonDiscovery {
	private File addonDir;
	private ArrayList<AddonContainer> addons;

	public AddonDiscovery(File addonDir) {
		this.addonDir = addonDir;
		addons = new ArrayList<AddonContainer>();
	}

	public AddonDiscovery(String addonDir) {
		this(new File(addonDir));
	}

	public void discoverAddons(Settings settings) {
		if (addonDir.isDirectory())
			scanFolder(settings, addonDir);
	}

	private void scanFolder(Settings settings, File dir) {
		if (dir.isDirectory()) {
			File[] contents = dir.listFiles();
			for (File file : contents) {
				if (file.isDirectory()) {
					File[] innerContents = file.listFiles();
					for (File innerFile : innerContents) {
						if (innerFile.getName().equalsIgnoreCase("manifest.json")) {
							System.out.println("Adding: " + file.getName());
							addons.add(new AddonContainer(file));
						}
					}
				}
			}
		}
	}

	public ArrayList<AddonContainer> getAddons() {
		return addons;
	}
}
