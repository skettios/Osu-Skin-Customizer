package com.skettios.osc.client;

public class AddonManifest
{
	private String addon, tab, category, contentPath;
	private String[] files;

	public String getName()
	{
		return addon;
	}

	public String getTab()
	{
		return tab;
	}

	public String getCategory()
	{
		return category;
	}

	public String getContentPath()
	{
		return contentPath;
	}

	public String[] getFiles()
	{
		return files;
	}
}
