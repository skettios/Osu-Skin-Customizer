package com.skettios.osc.client;

public class Settings
{
	private String title;
	private Tab[] tabs;

	public String getTitle()
	{
		return title;
	}

	public Tab[] getTabs()
	{
		return tabs;
	}
}

class Tab
{
	private String tab;
	private int index;
	private Category[] categories;

	public String getName()
	{
		return tab;
	}

	public int getIndex()
	{
		return index;
	}

	public Category[] getCategories()
	{
		return categories;
	}
}

class Category
{
	private String category;
	private int index;
	private Addon[] addons;

	public String getName()
	{
		return category;
	}

	public int getIndex()
	{
		return index;
	}

	public Addon[] getAddons()
	{
		return addons;
	}
}

class Addon
{
	private String addon, manifest;
	private int index;

	public String getName()
	{
		return addon;
	}

	public int getIndex()
	{
		return index;
	}

	public String getManifest()
	{
		return manifest;
	}
}
