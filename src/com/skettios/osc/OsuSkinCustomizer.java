package com.skettios.osc;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class OsuSkinCustomizer extends JFrame
{
	private JPanel mainPanel = new JPanel(new GridLayout());
	private JTabbedPane tabs = new JTabbedPane();

	private HashMap<String, ArrayList<JPanel>> tabPanes = new HashMap<String, ArrayList<JPanel>>();

	private HashMap<String, Tab> currentTabs = new HashMap<String, Tab>();
	private HashMap<String, ArrayList<Category>> currentCategories = new HashMap<String, ArrayList<Category>>();
	private HashMap<String, ArrayList<Addon>> currentAddons = new HashMap<String, ArrayList<Addon>>();

	private HashMap<String, HashMap<String, Addon>> currentSelectedInTab = new HashMap<String, HashMap<String, Addon>>();
	private HashMap<String, HashMap<String, Addon>> filesToReplace = new HashMap<String, HashMap<String, Addon>>();

	public OsuSkinCustomizer()
	{
		this.setTitle("Osu Skin Customizer");
		this.setResizable(false);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void start(HashMap<String, ArrayList<Category>> categories, ArrayList<Tab> appTabs, HashMap<String, ArrayList<Addon>> addons)
	{
		tabs.setAlignmentX(LEFT_ALIGNMENT);
		tabs.setAlignmentY(TOP_ALIGNMENT);

		currentAddons = addons;
		currentCategories = categories;

		for (Tab tab : appTabs)
		{
			addNewTab(tab.getName(), tab.getIndex());
			currentTabs.put(tab.getName(), tab);
		}

		mainPanel.add(tabs);

		this.add(mainPanel);

		this.setVisible(true);
	}

	private void addNewTab(final String tabName, int index)
	{
		JPanel newPanel = new JPanel();
		JPanel newAddonPanel = new JPanel();
		JScrollPane newAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		final JLabel previewLabel = new JLabel();
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

		buttonApply.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					parseCurrentSettings(new File("Current.json"));
					doDelete(tabName);
					doCopy(tabName);
					saveCurrentSettings(new FileOutputStream("Current.json"));
				}
				catch (Exception i)
				{
					i.printStackTrace();
				}
				finally
				{
					JOptionPane.showMessageDialog(null, "Addons have been applied!", "Apply", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		if (currentCategories.values() != null || !currentCategories.values().isEmpty())
		{
			ArrayList<Category> categories = currentCategories.get(tabName);
			if (categories != null)
			{
				for (final Category category : categories)
				{
					JLabel label = new JLabel(category.getName());
					final JComboBox comboBox = new JComboBox();

					label.setAlignmentX(CENTER_ALIGNMENT);

					if (currentAddons.get(category.getName()) != null)
					{
						ArrayList<Addon> addons = currentAddons.get(category.getName());
						if (addons != null || !addons.isEmpty())
						{
							for (Addon addon : addons)
								comboBox.addItem(addon.getName());

							newAddonPanel.add(label);
							newAddonPanel.add(comboBox);
						}
					}

					comboBox.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							String name = comboBox.getSelectedItem().toString();
							int index = comboBox.getSelectedIndex();
							if (currentSelectedInTab.containsKey(tabName))
							{
								if (currentSelectedInTab.get(tabName) != null)
								{
									currentSelectedInTab.get(tabName).put(category.getName(), currentAddons.get(category.getName()).get(index));
								}
							}
							else
							{
								HashMap<String, Addon> addonMap = new HashMap<String, Addon>();
								currentSelectedInTab.put(tabName, addonMap);
							}

							previewLabel.setIcon(new ImageIcon(currentAddons.get(category.getName()).get(index).getPath() + "/preview.png"));
						}
					});
				}
			}
		}

		tabs.add(newPanel, index);
		ArrayList<JPanel> panelArrayList = new ArrayList<JPanel>();
		panelArrayList.add(newPanel);
		panelArrayList.add(newAddonPanel);
		tabPanes.put(tabName, panelArrayList);
	}

	private ArrayList<JPanel> getTab(String tabName)
	{
		return tabPanes.get(tabName);
	}

	private void doCopy(String tabName)
	{
		try
		{
			File destDir = new File(".");
			ArrayList<Addon> list = new ArrayList<Addon>(currentSelectedInTab.get(tabName).values());
			for (Addon addon : list)
			{
				File addonDirectory = new File(addon.getPath());
				File[] addonContents = addonDirectory.listFiles();
				for (int i = 0; i < addonContents.length; i++)
				{
					if (addonContents[i].isFile())
					{
						if (addonContents[i].getName().equalsIgnoreCase("preview.png"))
							continue;

						FileUtils.copyFileToDirectory(addonContents[i], destDir);
					}
					else if (addonContents[i].isDirectory())
					{
						FileUtils.copyDirectoryToDirectory(addonContents[i], destDir);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void doDelete(String tabName)
	{
		parseCurrentSettings(new File("Current.json"));
		ArrayList<String> filePaths = new ArrayList<String>();
		HashMap<String, Addon> replace = filesToReplace.get(tabName);
		if (replace != null)
		{
			for (Addon addon : replace.values())
			{
				File addonDirectory = new File(addon.getPath());
				if (addonDirectory != null)
				{
					File[] addonContents = addonDirectory.listFiles();
					if (addonContents != null)
					{
						for (int i = 0; i < addonContents.length; i++)
						{
							if (addonContents[i].isFile())
							{
								filePaths.add(addonContents[i].getName());
							} else if (addonContents[i].isDirectory())
							{
								scanFolder(addonContents[i], filePaths);
							}
						}
					}
				}
			}
		}

		for (String path : filePaths)
		{
			try
			{
				File fileToDelete = new File(path);
				if (fileToDelete.exists())
					FileUtils.forceDelete(fileToDelete);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void scanFolder(File directory, ArrayList<String> filePaths)
	{
		File[] contents = directory.listFiles();
		for (int i = 0; i < contents.length; i++)
		{
			if (contents[i].isFile())
			{
				filePaths.add(directory.getName() + "/" + contents[i].getName());
			}
			else if (contents[i].isDirectory())
			{
				scanFolder(contents[i], filePaths);
			}
		}
	}

	private void saveCurrentSettings(OutputStream out)
	{
		try
		{
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.setIndent("	");
			writer.beginArray();
			for (String tab : tabPanes.keySet())
			{
				writer.beginObject();
				writer.name("tab").value(tab);
				writeTabSettings(writer, tab);
				writer.endObject();
			}
			writer.endArray();

			writer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void writeTabSettings(JsonWriter writer, String tabName)
	{
		try
		{
			if (currentSelectedInTab.get(tabName) != null)
			{
				HashMap<String, Addon> addons = currentSelectedInTab.get(tabName);
				writer.name("addons");
				writer.beginArray();
				for (Addon addon : addons.values())
				{
					writer.beginObject();
					writer.name("category").value(addon.getCategory());
					writer.name("name").value(addon.getName());
					writer.name("path").value(addon.getPath());
					writer.endObject();
				}
				writer.endArray();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void parseCurrentSettings(File json)
	{
		try
		{
			Gson gson = new Gson();
			Current[] currentAddons = gson.fromJson(new FileReader(json), Current[].class);
			if (currentAddons != null)
			{
				for (Current current : currentAddons)
				{
					HashMap<String, Addon> addons = new HashMap<String, Addon>();
					if (current.getAddons() != null)
					{
						for (Addon addon : current.getAddons())
							addons.put(addon.getCategory(), addon);
					}
					filesToReplace.put(current.getTab(), addons);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
