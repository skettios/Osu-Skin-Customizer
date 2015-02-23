package com.skettios.osc.client;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SettingsParser
{
	private File settingsFile;
	private Settings settings;

	public SettingsParser(File file)
	{
		if (!file.exists())
		{
			JOptionPane.showMessageDialog(null, String.format("%s could not be found, please create!", file.getName()), "Missing File!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		settingsFile = file;
	}

	public SettingsParser(String file)
	{
		this(new File(file));
	}

	public SettingsParser parseSettings()
	{
		try
		{
			Gson gson = new Gson();
			settings = gson.fromJson(new FileReader(settingsFile), Settings.class);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return this;
	}

	public Settings getSettings()
	{
		return settings;
	}
}
