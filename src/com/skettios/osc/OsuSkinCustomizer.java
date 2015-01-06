package com.skettios.osc;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class OsuSkinCustomizer extends JFrame
{
	private Settings currentSettings;
	
	private JPanel mainPanel = new JPanel(new GridLayout());
	private JTabbedPane tabPanes = new JTabbedPane();
	
	private ArrayList<Integer[]> currentIndexes = new ArrayList<Integer[]>();
	private HashMap<Tab, HashMap<Category, Addon>> currentSelected = new HashMap<Tab, HashMap<Category, Addon>>();
	private HashMap<Tab, HashMap<Category, Addon>> previousSelected = new HashMap<Tab, HashMap<Category, Addon>>();
	
	private int currentTabIndex, currentCategoryIndex, currentAddonIndex;
	
	public OsuSkinCustomizer() 
	{
		try
		{
			File settings = new File("Settings.json");
			if (settings.exists())
				parseSettings(settings);
	//		else
	//			// Create Json
			
			File previous = new File("Previous.json");
			if (previous.exists())
				System.out.println("LOAD");
			else
				previous.createNewFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		this.setTitle(currentSettings.getTitle());
		this.setSize(800, 600);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		initialize();
	}
	
	private void initialize()
	{
		tabPanes.setAlignmentX(LEFT_ALIGNMENT);
		tabPanes.setAlignmentY(TOP_ALIGNMENT);
		
		for (Tab tab : currentSettings.getTabs())
			addTab(tab);
		
		tabPanes.addChangeListener(new ChangeListener() 
		{
			@Override
			public void stateChanged(ChangeEvent e) 
			{
				currentTabIndex = tabPanes.getSelectedIndex();
			}
		});
		
		mainPanel.add(tabPanes);
		add(mainPanel);
	}
	
	private void addTab(final Tab tab)
	{
		JPanel tabPanel = new JPanel();
		JPanel addonPanel = new JPanel();
		JScrollPane addonScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JLabel previewLabel = new JLabel();
		JButton applyButton = new JButton("Apply");
		
		tabPanel.setName(tab.getName());
		addonPanel.setLayout(new BoxLayout(addonPanel, BoxLayout.PAGE_AXIS));
		addonScrollPane.setPreferredSize(new Dimension(788, 100));
		previewLabel.setPreferredSize(new Dimension(788, 408));
		applyButton.setPreferredSize(new Dimension(788, 20));
		
		applyButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (!currentSelected.isEmpty() && !currentIndexes.isEmpty())
				{
					try
					{
						savePrevious(new FileOutputStream("Previous.json"));
						savePrevious(new FileOutputStream("Previous.json"));
						for (Tab tab : currentSelected.keySet())
						{
							for (Category category : currentSelected.get(tab).keySet())
							{
								System.out.println(tab.getName() + ":" + category.getName() + ":" + currentSelected.get(tab).get(category).getName());
							}
						}
						
						for (Integer[] array : currentIndexes)
						{
							System.out.println(array[0] + ":" + array[1] + ":" + array[2]);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						JOptionPane.showMessageDialog(null, "Addons have been applied.", "Addons Applied", JOptionPane.INFORMATION_MESSAGE);
						currentSelected.clear();
						currentIndexes.clear();
					}
				}
			}
		});
		
		// Add categories and addons!
		for (Category category : tab.getCategories())
		{
			JLabel categoryLabel = new JLabel(category.getName());
			final JComboBox categoryComboBox = new JComboBox();
			
			categoryLabel.setAlignmentX(CENTER_ALIGNMENT);
			categoryComboBox.setName(String.valueOf(category.getIndex()));
			
			for (Addon addon : category.getAddons())
				categoryComboBox.insertItemAt(addon.getName(), addon.getIndex());
			
			addonPanel.add(categoryLabel);
			addonPanel.add(categoryComboBox);
			
			categoryComboBox.addMouseListener(new MouseListener() 
			{
				@Override
				public void mouseReleased(MouseEvent e) 
				{	}
				
				@Override
				public void mousePressed(MouseEvent e) 
				{	}
				
				@Override
				public void mouseExited(MouseEvent e) 
				{	}
				
				@Override
				public void mouseEntered(MouseEvent e) 
				{	}
				
				@Override
				public void mouseClicked(MouseEvent e) 
				{	
					currentCategoryIndex = Integer.valueOf(categoryComboBox.getName());
				}
			});
			
			categoryComboBox.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					currentAddonIndex = categoryComboBox.getSelectedIndex();
					
					Integer[] indexes = { currentTabIndex, currentCategoryIndex, currentAddonIndex };
					
					for (int i = 0; i < currentIndexes.size(); i++)
					{
						Integer[] array = currentIndexes.get(i);
						if (array[0] == indexes[0] && array[1] == indexes[1])
							currentIndexes.remove(array);
					}
					
					currentIndexes.add(indexes);
					
					if (currentSelected.containsKey(tab))
					{
						if (currentSelected.get(tab) != null)
							currentSelected.get(tab).put(currentSettings.getTabs()[currentTabIndex].getCategories()[currentCategoryIndex], currentSettings.getTabs()[currentTabIndex].getCategories()[currentCategoryIndex].getAddons()[currentAddonIndex]);
					}
					else
					{
						HashMap<Category, Addon> addonMap = new HashMap<Category, Addon>();
						addonMap.put(currentSettings.getTabs()[currentTabIndex].getCategories()[currentCategoryIndex], currentSettings.getTabs()[currentTabIndex].getCategories()[currentCategoryIndex].getAddons()[currentAddonIndex]);
						currentSelected.put(tab, addonMap);
					}
				}
			});
		}
		
		addonScrollPane.getViewport().add(addonPanel);
		tabPanel.add(addonScrollPane);
		tabPanel.add(previewLabel);
		tabPanel.add(applyButton);
		
		tabPanes.add(tabPanel);
	}
	
	private void parseSettings(File json)
	{
		try
		{
			Gson gson = new Gson();
			currentSettings = gson.fromJson(new FileReader(json), Settings.class);
			
			Arrays.sort(currentSettings.getTabs(), new Comparator<Tab>()
			{
				@Override
				public int compare(Tab o1, Tab o2)
				{
					return o1.getIndex() - o2.getIndex();
				}
			});
			
			for (int i = 0; i < currentSettings.getTabs().length; i++)
			{
				Arrays.sort(currentSettings.getTabs()[i].getCategories(), new Comparator<Category>()
				{
					@Override
					public int compare(Category o1, Category o2)
					{
						return o1.getIndex() - o2.getIndex();
					}
				});
				
				for (int j = 0; j < currentSettings.getTabs()[i].getCategories().length; j++)
				{
					Arrays.sort(currentSettings.getTabs()[i].getCategories()[j].getAddons(), new Comparator<Addon>()
					{
						@Override
						public int compare(Addon o1, Addon o2)
						{
							return o1.getIndex() - o2.getIndex();
						}
					});
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void savePrevious(OutputStream out)
	{
		try
		{
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.setIndent("	");
			writer.beginArray();
			for (int i = 0; i < currentIndexes.size(); i++)
			{
				writer.beginObject();
				Integer[] array = currentIndexes.get(i);
				writer.name("tab").value(array[0]);
				writer.name("category").value(array[1]);
				writer.name("addon").value(array[2]);
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
	
	private void loadPrevious(File previous)
	{
		try
		{
			Gson gson = new Gson();
			Previous[] prevArray = gson.fromJson(new FileReader(previous), Previous[].class);
			for (Previous prev : prevArray)
			{
				HashMap<Category, Addon> addon = new HashMap<Category, Addon>();
				addon.put(currentSettings.getTabs()[prev.getTabIndex()].getCategories()[prev.getCategoryIndex()], currentSettings.getTabs()[prev.getTabIndex()].getCategories()[prev.getCategoryIndex()].getAddons()[prev.getAddonIndex()]);
				previousSelected.put(currentSettings.getTabs()[prev.getTabIndex()], addon);
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
	
	class Settings
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
		private String addon, path;
		private int index;
		
		public String getName()
		{
			return addon;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public String getPath()
		{
			return path;
		}
	}
	
	class Previous
	{
		private int tab, category, addon;
		
		public int getTabIndex()
		{
			return tab;
		}
		
		public int getCategoryIndex()
		{
			return category;
		}
		
		public int getAddonIndex()
		{
			return addon;
		}
	}
}
