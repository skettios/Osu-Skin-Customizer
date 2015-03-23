package com.skettios.osc.client;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class OSCClient extends JFrame {
	private Settings settings;

	private JPanel mainPanel;
	private JTabbedPane tabs;

	private ArrayList<Tab> tabList;
	private HashMap<String, ArrayList<Category>> categoryMap;

	private AddonDiscovery addonDiscovery;

	private HashMap<String, HashMap<String, String>> currentSelectedInTab;
	private ArrayList<String> filesToDelete;

	public OSCClient(Settings settings) {
		this.settings = settings;

		setTitle(settings.getTitle());
		setResizable(false);
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		mainPanel = new JPanel(new GridLayout());
		tabs = new JTabbedPane();

		tabList = new ArrayList<>();
		categoryMap = new HashMap<>();

		currentSelectedInTab = new HashMap<>();
		filesToDelete = new ArrayList<>();

		sortTabs();
		sortCategories();

		addonDiscovery = new AddonDiscovery("addons");
		addonDiscovery.discoverAddons(settings);

		parseCurrentSettings(new File("addons/current.json"));
		addFileToDelete();
	}

	public void start() {
		tabs.setAlignmentX(LEFT_ALIGNMENT);
		tabs.setAlignmentY(TOP_ALIGNMENT);

		for (Tab tab : tabList)
			addTab(tab.getName(), tab.getIndex());

		mainPanel.add(tabs);

		add(mainPanel);

		setVisible(true);
	}

	private void sortTabs() {
		for (Tab tab : settings.getTabs())
			tabList.add(tab);

		Collections.sort(tabList, new Comparator<Tab>() {
			@Override
			public int compare(Tab o1, Tab o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
	}

	private void sortCategories() {
		for (Tab tab : tabList) {
			ArrayList<Category> ret = new ArrayList<>();
			for (Category category : tab.getCategories())
				ret.add(category);

			Collections.sort(ret, new Comparator<Category>() {
				@Override
				public int compare(Category o1, Category o2) {
					return o1.getIndex() - o2.getIndex();
				}
			});

			categoryMap.put(tab.getName(), (ArrayList<Category>) ret.clone());
		}
	}

	private void addTab(String tabName, int index) {
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

		buttonApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveCurrentSettings(new FileOutputStream("addons/current.json"));
					doDelete();
					doCopy();
					parseCurrentSettings(new File("addons/current.json"));
					addFileToDelete();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		if (categoryMap.get(tabName) != null || !categoryMap.get(tabName).isEmpty()) {

			for (Category category : categoryMap.get(tabName)) {
				JLabel label = new JLabel(category.getName());
				JComboBox comboBox = new JComboBox();

				label.setAlignmentX(CENTER_ALIGNMENT);

				for (AddonContainer addon : addonDiscovery.getAddons()) {
					if (addon != null) {
						if (addon.getAddonManifest().getTab().equalsIgnoreCase(tabName) && addon.getAddonManifest().getCategory().equalsIgnoreCase(category.getName()))
							comboBox.addItem(addon.getAddonManifest().getName());
					}
					newAddonPanel.add(label);
					newAddonPanel.add(comboBox);
				}

				comboBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String name = comboBox.getSelectedItem().toString();
						if (currentSelectedInTab.containsKey(tabName)) {
							if (currentSelectedInTab.get(tabName) != null)
								currentSelectedInTab.get(tabName).put(category.getName(), name);
						} else {
							HashMap<String, String> addonMap = new HashMap<>();
							addonMap.put(category.getName(), name);
							currentSelectedInTab.put(tabName, addonMap);
						}

						for (AddonContainer addon : addonDiscovery.getAddons()) {
							if (addon.getAddonManifest().getTab().equalsIgnoreCase(tabName) && addon.getAddonManifest().getCategory().equalsIgnoreCase(category.getName()) && addon.getAddonManifest().getName().equalsIgnoreCase(comboBox.getSelectedItem().toString())) {
								previewLabel.setIcon(new ImageIcon("addons/" + addon.getAddonManifest().getName() + "/preview.png"));
							}
						}
					}
				});
			}
		}


		tabs.add(newPanel, index);
	}

	private void parseCurrentSettings(File json) {
		try {
			Gson gson = new Gson();
			Current[] currentAddons = gson.fromJson(new FileReader(json), Current[].class);
			if (currentAddons != null) {
				for (Current current : currentAddons) {
					HashMap<String, String> addons = new HashMap<>();
					if (current.getAddons() != null) {
						for (CurrentAddons addon : current.getAddons())
							addons.put(addon.getCategory(), addon.getName());
					}

					currentSelectedInTab.put(current.getTab(), addons);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveCurrentSettings(OutputStream out) {
		try {
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.setIndent("	");
			writer.beginArray();
			for (Tab tab : settings.getTabs()) {
				writer.beginObject();
				writer.name("tab").value(tab.getName());
				writeTabSettings(writer, tab.getName());
				writer.endObject();
			}
			writer.endArray();

			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeTabSettings(JsonWriter writer, String tabName) {
		try {
			if (currentSelectedInTab.get(tabName) != null) {
				HashMap<String, String> addons = currentSelectedInTab.get(tabName);
				writer.name("addons");
				writer.beginArray();
				for (String string : addons.keySet()) {
					writer.beginObject();
					writer.name("category").value(string);
					writer.name("name").value(addons.get(string));
					writer.endObject();
				}
				writer.endArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addFileToDelete() {
		for (Tab tab : settings.getTabs()) {
			for (Category category : tab.getCategories()) {
				if (currentSelectedInTab.containsKey(tab.getName())) {
					String currentAddon = currentSelectedInTab.get(tab.getName()).get(category.getName());
					for (AddonContainer container : addonDiscovery.getAddons()) {
						if (container.getAddonManifest().getName().equalsIgnoreCase(currentAddon)) {
							for (String filePath : container.getAddonManifest().getFiles())
								filesToDelete.add(filePath);
						}
					}
				}
			}
		}
	}

	private void doDelete() {
		for (String path : filesToDelete) {
			try {
				File fileToDelete = new File(path);
				if (fileToDelete.exists())
					FileUtils.forceDelete(fileToDelete);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doCopy() {
		try {
			File destDir = new File(".");
			for (Tab tab : settings.getTabs()) {
				if (currentSelectedInTab.containsKey(tab.getName())) {
					ArrayList<String> list = new ArrayList<>(currentSelectedInTab.get(tab.getName()).values());
					if (list != null || !list.isEmpty()) {
						for (String string : list) {
							for (AddonContainer addon : addonDiscovery.getAddons()) {
								if (addon.getAddonManifest().getName().equalsIgnoreCase(string)) {
									for (String path : addon.getAddonManifest().getFiles()) {
										if (path.contains("/")) {
											int slashPos = path.indexOf('/');
											String dirName = path.substring(0, slashPos);
											FileUtils.copyDirectoryToDirectory(new File("addons/" + addon.getAddonManifest().getName() + "/" + addon.getAddonManifest().getContentPath() + "/" + dirName), destDir);
										} else {
											FileUtils.copyFileToDirectory(new File("addons/" + addon.getAddonManifest().getName() + "/" + addon.getAddonManifest().getContentPath() + "/" + path), destDir);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
