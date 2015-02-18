package com.skettios.osc;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Start
{
	public static void main(String[] args)
	{
		File tabs = new File("Tabs.json");
		File categories = new File("Categories.json");
		File current = new File("Current.json");
		File addons = new File("Addons.json");

		if (!tabs.exists() || !categories.exists() || !current.exists() || !addons.exists())
		{
			JOptionPane.showMessageDialog(null, "Either Tab.json, Categories.json, Current.json or Addons.json is missing", "Missing File(s)!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		ArrayList<Tab> tabArray = parseTabs(new File("Tabs.json"));
		HashMap<String, ArrayList<Category>> categoryArray = parseCategories(new File("Categories.json"));
		HashMap<String, ArrayList<Addon>> addonArray = parseAddons(new File("Addons.json"));

		OsuSkinCustomizer customizer = new OsuSkinCustomizer();
		customizer.start(categoryArray, tabArray, addonArray);
	}

	private static ArrayList<Tab> parseTabs(File json)
	{
		ArrayList<Tab> ret = new ArrayList<Tab>();
		try
		{
			Gson gson = new Gson();
			Tab[] tabs = gson.fromJson(new FileReader(json), Tab[].class);
			for (Tab tab : tabs)
				ret.add(tab);

			Collections.sort(ret, new Comparator<Tab>()
			{
				@Override
				public int compare(Tab o1, Tab o2)
				{
					return o1.getIndex() - o2.getIndex();
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return ret;
	}

	private static HashMap<String, ArrayList<Category>> parseCategories(File json)
	{
		HashMap<String, ArrayList<Category>> ret = new HashMap<String, ArrayList<Category>>();
		try
		{
			Gson gson = new Gson();
			Category[] categories = gson.fromJson(new FileReader(json), Category[].class);
			for (Category category : categories)
			{
				if (ret.containsKey(category.getTab()))
				{
					ret.get(category.getTab()).add(category);
				}
				else
				{
					ArrayList<Category> categoryList = new ArrayList<Category>();
					categoryList.add(category);
					ret.put(category.getTab(), categoryList);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	private static HashMap<String, ArrayList<Addon>> parseAddons(File json)
	{
		HashMap<String, ArrayList<Addon>> ret = new HashMap<String, ArrayList<Addon>>();
		try
		{
			Gson gson = new Gson();
			Addon[] addons = gson.fromJson(new FileReader(json), Addon[].class);
			for (Addon addon : addons)
			{
				if (ret.containsKey(addon.getCategory()))
				{
					ret.get(addon.getCategory()).add(addon);
				}
				else
				{
					ArrayList<Addon> addonList = new ArrayList<Addon>();
					addonList.add(addon);
					ret.put(addon.getCategory(), addonList);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return ret;
	}

	private static void parseCurrentSettings(File json)
	{

	}
}
