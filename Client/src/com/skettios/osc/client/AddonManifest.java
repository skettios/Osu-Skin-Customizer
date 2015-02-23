package com.skettios.osc.client;

public class AddonManifest
{
	private String addon;
	private String[] files;

	public String getName()
	{
		return addon;
	}

	public String[] getFiles()
	{
		return files;
	}
}
