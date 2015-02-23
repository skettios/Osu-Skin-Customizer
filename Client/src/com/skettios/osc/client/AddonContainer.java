package com.skettios.osc.client;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AddonContainer
{
	private File addonDir;
	private AddonManifest addonManifest;

	public AddonContainer(File addonPath)
	{
		addonDir = addonPath;

		load();
	}

	public AddonContainer(String addonPath)
	{
		this(new File(addonPath));
	}

	private void load()
	{
		try
		{
			if (addonDir.isDirectory())
			{
				File[] content = addonDir.listFiles();
				for (File file : content)
				{
					if (file.getName().equalsIgnoreCase("manifest.json"))
						loadManifest(file);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void loadManifest(File manifest) throws IOException
	{
		System.out.println("Loading Manifest");

		Gson gson = new Gson();
		addonManifest = gson.fromJson(new FileReader(manifest), AddonManifest.class);
	}

	public AddonManifest getAddonManifest()
	{
		return addonManifest;
	}
}
