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

	public String getName()
	{
		return category;
	}

	public int getIndex()
	{
		return index;
	}
}

class Current
{
	private String tab;
	private CurrentAddons[] addons;

	public String getTab()
	{
		return tab;
	}

	public CurrentAddons[] getAddons()
	{
		return addons;
	}
}

class CurrentAddons
{
	private String category, name;

	public String getCategory()
	{
		return category;
	}

	public String getName()
	{
		return name;
	}
}
