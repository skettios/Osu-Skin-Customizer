package com.skettios.osc;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Written By: Skettios
 * TODO: When clicking apply list all of the addons that will be applied. With confirmation
 */
public class OsuSkinCustomizer extends JFrame
{
	private final int OSU_INDEX = 0;
	private final int TAIKO_INDEX = 1;
	private final int MANIA_INDEX = 2;

	/**
	 * 1st Index = Type (Osu, Taiko, Mania, etc.)
	 * 2nd Index = Identifier (What it does)
	 */
	private String[][] ADDON_TYPES = {
			{ "Osu_Complete", "Osu_Note" },
			{ "Taiko_Complete", "Taiko_Note" },
			{ "Mania_Complete", "Mania_Note" }
	};
	private String[] TITLES = { "Osu", "Taiko", "Mania" };

	/**
	 * Key = Addon Type
	 * Value = Addon
	 */
	private HashMap<String, Addon> osuAddonsToApply = new HashMap<String, Addon>();
	private HashMap<String, Addon> taikoAddonsToApply = new HashMap<String, Addon>();
	private HashMap<String, Addon> maniaAddonsToApply = new HashMap<String, Addon>();

	private HashMap<String, Addon> osuCompleteAddons = new HashMap<String, Addon>();
	private HashMap<String, Addon> taikoCompleteAddons = new HashMap<String, Addon>();
	private HashMap<String, Addon> maniaCompleteAddons = new HashMap<String, Addon>();

	private HashMap<String, Addon> osuNoteAddons = new HashMap<String, Addon>();
	private HashMap<String, Addon> taikoNoteAddons = new HashMap<String, Addon>();
	private HashMap<String, Addon> maniaNoteAddons = new HashMap<String, Addon>();

	// Main Panel and it's components.
	private JPanel mainPanel = new JPanel(new GridLayout());
	private JTabbedPane tabs = new JTabbedPane();

	// Osu Panel and it's components.
	private JPanel osuPanel = new JPanel(new FlowLayout());
	private JPanel osuAddonPanel = new JPanel();
	private JScrollPane osuAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JLabel osuCompleteLabel = new JLabel("Complete Skin");
	private JComboBox osuCompleteComboBox = new JComboBox();
	private JLabel osuNoteLabel = new JLabel("Note Skins");
	private JComboBox osuNoteComboBox = new JComboBox();
	private JLabel osuPreviewLabel = new JLabel();
	private JButton osuApplyButton = new JButton("Apply!");

	// Taiko Panel and it's components.
	private JPanel taikoPanel = new JPanel(new FlowLayout());
	private JPanel taikoAddonPanel = new JPanel();
	private JScrollPane taikoAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JLabel taikoCompleteLabel = new JLabel("Complete Skin");
	private JComboBox taikoCompleteComboBox = new JComboBox();
	private JLabel taikoNoteLabel = new JLabel("Note Skins");
	private JComboBox taikoNoteComboBox = new JComboBox();
	private JLabel taikoPreviewLabel = new JLabel();
	private JButton taikoApplyButton = new JButton("Apply!");

	// Mania Panel and it's components.
	private JPanel maniaPanel = new JPanel(new FlowLayout());
	private JPanel maniaAddonPanel = new JPanel();
	private JScrollPane maniaAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JLabel maniaCompleteLabel = new JLabel("Complete Skin");
	private JComboBox maniaCompleteComboBox = new JComboBox();
	private JLabel maniaNoteLabel = new JLabel("Note Skins");
	private JComboBox maniaNoteComboBox = new JComboBox();
	private JLabel maniaPreviewLabel = new JLabel();
	private JButton maniaApplyButton = new JButton("Apply!");

	// Current states of addons
	private String currentOsuCompleteAddon = null;
	private String currentTaikoCompleteAddon = null;
	private String currentManiaCompleteAddon = null;
	private String currentOsuNoteAddon = null;
	private String currentTaikoNoteAddon = null;
	private String currentManiaNoteAddon = null;
	private String currentTab = null;

	public OsuSkinCustomizer()
	{
		parseJSON(new File("Customizer.json"));

		this.setTitle("Osu Skin Customizer");
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.add(mainPanel);

		addActionListeners();
		addChangeListeners();
	}

	public void start()
	{
		tabs.setAlignmentY(TOP_ALIGNMENT);
		tabs.setAlignmentX(LEFT_ALIGNMENT);

		mainPanel.add(tabs);
		initializeOsuPanel();
		initializeTaikoPanel();
		initializeManiaPanel();
	}

	private void initializeOsuPanel()
	{
		osuAddonPanel.setLayout(new BoxLayout(osuAddonPanel, BoxLayout.PAGE_AXIS));

		for (String addon : osuCompleteAddons.keySet())
			osuCompleteComboBox.addItem(addon);

		for (String addon : osuNoteAddons.keySet())
			osuNoteComboBox.addItem(addon);

		osuCompleteLabel.setAlignmentX(CENTER_ALIGNMENT);
		osuNoteLabel.setAlignmentX(CENTER_ALIGNMENT);

		osuAddons.setPreferredSize(new Dimension(788, 100));
		osuPreviewLabel.setPreferredSize(new Dimension(800, 408));
		osuApplyButton.setPreferredSize(new Dimension(788, 20));

		osuAddons.getViewport().add(osuAddonPanel);
		if (!osuCompleteAddons.values().isEmpty())
		{
			osuAddonPanel.add(osuCompleteLabel);
			osuAddonPanel.add(osuCompleteComboBox);
		}
		if (!osuNoteAddons.values().isEmpty())
		{
			osuAddonPanel.add(osuNoteLabel);
			osuAddonPanel.add(osuNoteComboBox);
		}
		osuPanel.add(osuAddons);
		osuPanel.add(osuPreviewLabel);
		osuPanel.add(osuApplyButton);

		tabs.addTab(TITLES[0], osuPanel);
	}

	private void initializeTaikoPanel()
	{
		taikoAddonPanel.setLayout(new BoxLayout(taikoAddonPanel, BoxLayout.PAGE_AXIS));

		for (String addon : taikoCompleteAddons.keySet())
			taikoCompleteComboBox.addItem(addon);

		for (String addon : taikoNoteAddons.keySet())
			taikoNoteComboBox.addItem(addon);

		taikoCompleteLabel.setAlignmentX(CENTER_ALIGNMENT);
		taikoNoteLabel.setAlignmentX(CENTER_ALIGNMENT);

		taikoAddons.setPreferredSize(new Dimension(788, 100));
		taikoPreviewLabel.setPreferredSize(new Dimension(800, 408));
		taikoApplyButton.setPreferredSize(new Dimension(788, 20));

		taikoAddons.getViewport().add(taikoAddonPanel);
		if (!taikoCompleteAddons.values().isEmpty())
		{
			taikoAddonPanel.add(taikoCompleteLabel);
			taikoAddonPanel.add(taikoCompleteComboBox);
		}
		if (!taikoNoteAddons.values().isEmpty())
		{
			taikoAddonPanel.add(taikoNoteLabel);
			taikoAddonPanel.add(taikoNoteComboBox);
		}
		taikoPanel.add(taikoAddons);
		taikoPanel.add(taikoPreviewLabel);
		taikoPanel.add(taikoApplyButton);

		tabs.addTab(TITLES[1], taikoPanel);
	}

	private void initializeManiaPanel()
	{
		maniaAddonPanel.setLayout(new BoxLayout(maniaAddonPanel, BoxLayout.PAGE_AXIS));

		for (String addon : maniaCompleteAddons.keySet())
			maniaCompleteComboBox.addItem(addon);

		for (String addon : maniaNoteAddons.keySet())
			maniaNoteComboBox.addItem(addon);

		maniaCompleteLabel.setAlignmentX(CENTER_ALIGNMENT);
		maniaNoteLabel.setAlignmentX(CENTER_ALIGNMENT);

		maniaAddons.setPreferredSize(new Dimension(788, 100));
		maniaPreviewLabel.setPreferredSize(new Dimension(800, 408));
		maniaApplyButton.setPreferredSize(new Dimension(788, 20));

		maniaAddons.getViewport().add(maniaAddonPanel);
		if (!maniaCompleteAddons.values().isEmpty())
		{
			maniaAddonPanel.add(maniaCompleteLabel);
			maniaAddonPanel.add(maniaCompleteComboBox);
		}
		if (!maniaNoteAddons.values().isEmpty())
		{
			maniaAddonPanel.add(maniaNoteLabel);
			maniaAddonPanel.add(maniaNoteComboBox);
		}
		maniaPanel.add(maniaAddons);
		maniaPanel.add(maniaPreviewLabel);
		maniaPanel.add(maniaApplyButton);

		tabs.addTab(TITLES[2], maniaPanel);
	}

	private void addActionListeners()
	{
		osuCompleteComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentOsuCompleteAddon = osuCompleteComboBox.getSelectedItem().toString();
				osuPreviewLabel.setIcon(new ImageIcon(osuCompleteAddons.get(currentOsuCompleteAddon).getName() + "/preview.png"));
				osuAddonsToApply.put(ADDON_TYPES[OSU_INDEX][0], osuCompleteAddons.get(currentOsuCompleteAddon));
			}
		});

		osuNoteComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentOsuNoteAddon = osuNoteComboBox.getSelectedItem().toString();
				osuPreviewLabel.setIcon(new ImageIcon(osuNoteAddons.get(currentOsuNoteAddon).getPath() + "/preview.png"));
				osuAddonsToApply.put(ADDON_TYPES[OSU_INDEX][1], osuNoteAddons.get(currentOsuNoteAddon));
			}
		});

		taikoNoteComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentTaikoNoteAddon = taikoNoteComboBox.getSelectedItem().toString();
				taikoPreviewLabel.setIcon(new ImageIcon(taikoNoteAddons.get(currentTaikoNoteAddon).getPath() + "/preview.png"));
				taikoAddonsToApply.put(ADDON_TYPES[TAIKO_INDEX][1], taikoNoteAddons.get(currentTaikoNoteAddon));
			}
		});

		maniaNoteComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentManiaNoteAddon = maniaNoteComboBox.getSelectedItem().toString();
				maniaPreviewLabel.setIcon(new ImageIcon(maniaNoteAddons.get(currentManiaNoteAddon).getPath() + "/preview.png"));
				maniaAddonsToApply.put(ADDON_TYPES[MANIA_INDEX][1], maniaNoteAddons.get(currentManiaNoteAddon));
			}
		});

		osuApplyButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ArrayList<Addon> addonsToApply = new ArrayList<Addon>(osuAddonsToApply.values());
				doCopy(addonsToApply);
			}
		});

		taikoApplyButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ArrayList<Addon> addonsToApply = new ArrayList<Addon>(taikoAddonsToApply.values());
				doCopy(addonsToApply);
			}
		});

		maniaApplyButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ArrayList<Addon> addonsToApply = new ArrayList<Addon>(maniaAddonsToApply.values());
				doCopy(addonsToApply);
			}
		});
	}

	private void addChangeListeners()
	{
		tabs.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				currentTab = TITLES[tabs.getSelectedIndex()];
			}
		});
	}

	public void doCopy(ArrayList<Addon> addonList)
	{
		try
		{
			File destDir = new File(".");
			for (Addon addon : addonList)
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

	private void parseJSON(File json)
	{
		try
		{
			Gson gson = new Gson();
			Addon[] addonArray = gson.fromJson(new FileReader(json), Addon[].class);
			for (Addon addon : addonArray)
			{
				if (addon.getType().equals(ADDON_TYPES[OSU_INDEX][0]))
					osuCompleteAddons.put(addon.getName(), addon);
				if (addon.getType().equals(ADDON_TYPES[OSU_INDEX][1]))
					osuNoteAddons.put(addon.getName(), addon);

				if (addon.getType().equals(ADDON_TYPES[TAIKO_INDEX][0]))
					taikoCompleteAddons.put(addon.getName(), addon);
				if (addon.getType().equals(ADDON_TYPES[TAIKO_INDEX][1]))
					taikoNoteAddons.put(addon.getName(), addon);

				if (addon.getType().equals(ADDON_TYPES[MANIA_INDEX][0]))
					maniaCompleteAddons.put(addon.getName(), addon);
				if (addon.getType().equals(ADDON_TYPES[MANIA_INDEX][1]))
					maniaNoteAddons.put(addon.getName(), addon);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
