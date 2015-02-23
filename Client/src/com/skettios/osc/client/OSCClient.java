package com.skettios.osc.client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class OSCClient extends JFrame
{
	private Settings settings;

	private JPanel mainPanel;
	private JTabbedPane tabs;

	private ArrayList<Tab> tabList;
	private HashMap<String, ArrayList<Category>> categoryMap;
	private HashMap<String, HashMap<String, ArrayList<Addon>>> addonMap;

	public OSCClient(Settings settings)
	{
		this.settings = settings;

		setTitle(settings.getTitle());
		setResizable(false);
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		mainPanel = new JPanel(new GridLayout());
		tabs = new JTabbedPane();

		tabList = new ArrayList<>();
		categoryMap = new HashMap<>();
		addonMap = new HashMap<>();

		sortTabs();
		sortCategories();
		sortAddons();
	}

	public void start()
	{
		tabs.setAlignmentX(LEFT_ALIGNMENT);
		tabs.setAlignmentY(TOP_ALIGNMENT);

		for (Tab tab : tabList)
			addTab(tab.getName(), tab.getIndex());

		mainPanel.add(tabs);

		add(mainPanel);

		setVisible(true);
	}

	private void sortTabs()
	{
		for (Tab tab : settings.getTabs())
			tabList.add(tab);

		Collections.sort(tabList, new Comparator<Tab>()
		{
			@Override
			public int compare(Tab o1, Tab o2)
			{
				return o1.getIndex() - o2.getIndex();
			}
		});
	}

	private void sortCategories()
	{
		for (Tab tab : tabList)
		{
			ArrayList<Category> ret = new ArrayList<>();
			for (Category category : tab.getCategories())
				ret.add(category);

			Collections.sort(ret, new Comparator<Category>()
			{
				@Override
				public int compare(Category o1, Category o2)
				{
					return o1.getIndex() - o2.getIndex();
				}
			});

			categoryMap.put(tab.getName(), (ArrayList<Category>) ret.clone());
		}
	}

	private void sortAddons()
	{
		for (Tab tab : tabList)
		{
			HashMap<String, ArrayList<Addon>> retMap = new HashMap<>();
			for (Category category : categoryMap.get(tab.getName()))
			{
				ArrayList<Addon> retList = new ArrayList<>();
				for (Addon addon : category.getAddons())
					retList.add(addon);

				Collections.sort(retList, new Comparator<Addon>()
				{
					@Override
					public int compare(Addon o1, Addon o2)
					{
						return o1.getIndex() - o2.getIndex();
					}
				});

				retMap.put(category.getName(), (ArrayList<Addon>) retList.clone());
			}

			addonMap.put(tab.getName(), (HashMap<String, ArrayList<Addon>>) retMap.clone());
		}
	}

	private void addTab(String tabName, int index)
	{
		JPanel newPanel = new JPanel();
		JPanel newAddonPanel = new JPanel();
		JScrollPane newAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JLabel previewLabel = new JLabel();
		JButton buttonApply = new JButton("Apply");

		newPanel.setName(tabName);
		newAddonPanel.setLayout(new BoxLayout(newAddonPanel, BoxLayout.PAGE_AXIS));
		newAddons.setPreferredSize(new Dimension(788, 100));
		previewLabel.setPreferredSize(new Dimension(800, 408));
		buttonApply.setPreferredSize(new Dimension(788, 20));

		newAddons.getViewport().add(newAddonPanel);
		newPanel.add(newAddons);
		newPanel.add(previewLabel);
		newPanel.add(buttonApply);

		if (categoryMap.get(tabName) != null || !categoryMap.get(tabName).isEmpty())
		{
			for (Category category : categoryMap.get(tabName))
			{
				JLabel label = new JLabel(category.getName());
				JComboBox comboBox = new JComboBox();

				label.setAlignmentX(CENTER_ALIGNMENT);

				if (addonMap.get(tabName).get(category.getName()) != null || !addonMap.get(tabName).get(getName()).isEmpty())
				{
					for (Addon addon : addonMap.get(tabName).get(category.getName()))
						comboBox.addItem(addon.getName());

					newAddonPanel.add(label);
					newAddonPanel.add(comboBox);
				}
			}
		}

		tabs.add(newPanel, index);
	}
}
