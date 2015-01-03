package com.skettios.osc;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class OsuSkinCustomizer extends JFrame
{
	private JPanel mainPanel = new JPanel(new GridLayout());
	private JTabbedPane tabs = new JTabbedPane();

	private HashMap<Tab, HashMap<Category, Addon[]>> settings = new HashMap<Tab, HashMap<Category, Addon[]>>();

	public OsuSkinCustomizer()
	{
		this.setTitle("Osu Skin Customizer");
		this.setSize(800, 600);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initialize();
	}

	private void initialize()
	{
		parseSettings(new File("Settings.json"));

		tabs.setAlignmentX(LEFT_ALIGNMENT);
		tabs.setAlignmentY(TOP_ALIGNMENT);

		for (Tab tab : settings.keySet())
			addTab(tab);

		mainPanel.add(tabs);
		add(mainPanel);
	}

	private void addTab(Tab tab)
	{
		// Add the tab
		JPanel tabPanel = new JPanel();
		JPanel addonPanel = new JPanel();
		JScrollPane addonScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JLabel previewLabel = new JLabel();
		JButton applyButton = new JButton("Apply");

		tabPanel.setName(tab.getTab());
		addonPanel.setLayout(new BoxLayout(addonPanel, BoxLayout.PAGE_AXIS));
		addonScrollPane.setPreferredSize(new Dimension(788, 100));
		previewLabel.setPreferredSize(new Dimension(788, 408));
		applyButton.setPreferredSize(new Dimension(788, 20));

		// Add all the categories in the tab with addons
		for (Category category : tab.getCategories())
		{
			JLabel categoryLabel = new JLabel(category.getCategory());
			JComboBox categoryComboBox = new JComboBox();

			categoryLabel.setAlignmentX(CENTER_ALIGNMENT);

			for (Addon addon : category.getAddons())
				categoryComboBox.addItem(addon.getAddon());

			addonPanel.add(categoryLabel);
			addonPanel.add(categoryComboBox);
		}

		addonScrollPane.getViewport().add(addonPanel);
		tabPanel.add(addonScrollPane);
		tabPanel.add(previewLabel);
		tabPanel.add(applyButton);

		tabs.add(tabPanel);
	}

	private void parseSettings(File json)
	{
		try
		{
			Gson gson = new Gson();
			Tab[] tabs = gson.fromJson(new FileReader(json), Tab[].class);
			for (Tab tab : tabs)
			{
				for (Category category : tab.getCategories())
				{
					HashMap<Category, Addon[]> addons = new HashMap<Category, Addon[]>();
					addons.put(category, category.getAddons());
					settings.put(tab, addons);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void start()
	{
		this.setVisible(true);
	}

	public static void main(String[] args)
	{
		OsuSkinCustomizer osc = new OsuSkinCustomizer();
		osc.start();
	}

	class Tab
	{
		private String tab;
		private Category[] categories;

		public String getTab()
		{
			return tab;
		}

		public Category[] getCategories()
		{
			return categories;
		}
	}

	class Category
	{
		private String category;
		private Addon[] addons;

		public String getCategory()
		{
			return category;
		}

		public Addon[] getAddons()
		{
			return addons;
		}
	}

	class Addon
	{
		private String addon, path;

		public String getAddon()
		{
			return addon;
		}

		public String getPath()
		{
			return path;
		}
	}
}
