package com.skettios.osc.client;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AddonManifestParser
{
	private File manifestFile;
	private AddonManifest manifest;

	public AddonManifestParser(File file)
	{
		manifestFile = file;
	}

	public AddonManifestParser(String file)
	{
		this(new File(file));
	}

	public AddonManifestParser parseManifest()
	{
		try
		{
			Gson gson = new Gson();
			manifest = gson.fromJson(new FileReader(manifestFile), AddonManifest.class);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return this;
	}

	public AddonManifest getManifest()
	{
		return manifest;
	}
}
