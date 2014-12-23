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
			{ "Osu_Note" },
			{ "Taiko_Note" },
			{ "Mania_Note" }
	};
	private String[] TITLES = { "Osu", "Taiko", "Mania" };

	/**
	 * Key = Addon Type
	 * Value = Addon
	 */
	private HashMap<String, Addon> osuAddonsToApply = new HashMap<String, Addon>();
	private HashMap<String, Addon> taikoAddonsToApply = new HashMap<String, Addon>();
	private HashMap<String, Addon> maniaAddonsToApply = new HashMap<String, Addon>();

	private HashMap<String, Addon> osuNoteAddons = new HashMap<String, Addon>();
	private HashMap<String, Addon> taikoNoteAddons = new HashMap<String, Addon>();
	private HashMap<String, Addon> maniaNoteAddons = new HashMap<String, Addon>();

	// Main Panel and it's components.
	private JPanel mainPanel = new JPanel(new GridLayout());
	private JTabbedPane tabs = new JTabbedPane();

	// Osu Panel and it's components.
	private JPanel osuPanel = new JPanel(new FlowLayout());
	private JPanel osuAddonPanel = new JPanel(new GridLayout());
	private JScrollPane osuAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JLabel osuNoteLabel = new JLabel("Note Skins: ");
	private JComboBox osuNoteComboBox = new JComboBox();
	private JLabel osuPreviewLabel = new JLabel();
	private JButton osuApplyButton = new JButton("Apply!");

	// Taiko Panel and it's components.
	private JPanel taikoPanel = new JPanel(new FlowLayout());
	private JPanel taikoAddonPanel = new JPanel(new GridLayout());
	private JScrollPane taikoAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JLabel taikoNoteLabel = new JLabel("Note Skins: ");
	private JComboBox taikoNoteComboBox = new JComboBox();
	private JLabel taikoPreviewLabel = new JLabel();
	private JButton taikoApplyButton = new JButton("Apply!");

	// Mania Panel and it's components.
	private JPanel maniaPanel = new JPanel(new FlowLayout());
	private JPanel maniaAddonPanel = new JPanel(new GridLayout());
	private JScrollPane maniaAddons = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JLabel maniaNoteLabel = new JLabel("Note Skins: ");
	private JComboBox maniaNoteComboBox = new JComboBox();
	private JLabel maniaPreviewLabel = new JLabel();
	private JButton maniaApplyButton = new JButton("Apply!");

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
		osuAddons.setPreferredSize(new Dimension(788, 100));

		for (String addon : osuNoteAddons.keySet())
			osuNoteComboBox.addItem(addon);

		osuPreviewLabel.setIcon(new ImageIcon(osuNoteAddons.get(currentOsuNoteAddon).getPath() + "/preview.png"));
		osuPreviewLabel.setPreferredSize(new Dimension(800, 408));

		osuApplyButton.setPreferredSize(new Dimension(788, 20));

		osuAddons.getViewport().add(osuAddonPanel);

		osuAddonPanel.add(osuNoteLabel);
		osuAddonPanel.add(osuNoteComboBox);

		osuPanel.add(osuAddons);
		osuPanel.add(osuPreviewLabel);
		osuPanel.add(osuApplyButton);

		tabs.addTab(TITLES[0], osuPanel);
	}

	private void initializeTaikoPanel()
	{
		taikoAddons.setPreferredSize(new Dimension(788, 100));

		for (String addon : taikoNoteAddons.keySet())
			taikoNoteComboBox.addItem(addon);

		taikoPreviewLabel.setIcon(new ImageIcon(taikoNoteAddons.get(currentTaikoNoteAddon).getPath() + "/preview.png"));
		taikoPreviewLabel.setPreferredSize(new Dimension(800, 408));

		taikoApplyButton.setPreferredSize(new Dimension(788, 20));

		taikoAddons.getViewport().add(taikoAddonPanel);

		taikoAddonPanel.add(taikoNoteLabel);
		taikoAddonPanel.add(taikoNoteComboBox);

		taikoPanel.add(taikoAddons);
		taikoPanel.add(taikoPreviewLabel);
		taikoPanel.add(taikoApplyButton);

		tabs.addTab(TITLES[1], taikoPanel);
	}

	private void initializeManiaPanel()
	{
		maniaAddons.setPreferredSize(new Dimension(788, 100));

		for (String addon : maniaNoteAddons.keySet())
			maniaNoteComboBox.addItem(addon);

		maniaPreviewLabel.setIcon(new ImageIcon(maniaNoteAddons.get(currentManiaNoteAddon).getPath() + "/preview.png"));
		maniaPreviewLabel.setPreferredSize(new Dimension(800, 408));

		maniaApplyButton.setPreferredSize(new Dimension(788, 20));

		maniaAddons.getViewport().add(maniaAddonPanel);

		maniaAddonPanel.add(maniaNoteLabel);
		maniaAddonPanel.add(maniaNoteComboBox);

		maniaPanel.add(maniaAddons);
		maniaPanel.add(maniaPreviewLabel);
		maniaPanel.add(maniaApplyButton);

		tabs.addTab(TITLES[2], maniaPanel);
	}

	private void addActionListeners()
	{
		osuNoteComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentOsuNoteAddon = osuNoteComboBox.getSelectedItem().toString();
				osuPreviewLabel.setIcon(new ImageIcon(osuNoteAddons.get(currentOsuNoteAddon).getPath() + "/preview.png"));
				osuAddonsToApply.put(ADDON_TYPES[OSU_INDEX][0], osuNoteAddons.get(currentOsuNoteAddon));
			}
		});

		taikoNoteComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentTaikoNoteAddon = taikoNoteComboBox.getSelectedItem().toString();
				taikoPreviewLabel.setIcon(new ImageIcon(taikoNoteAddons.get(currentTaikoNoteAddon).getPath() + "/preview.png"));
				taikoAddonsToApply.put(ADDON_TYPES[TAIKO_INDEX][0], taikoNoteAddons.get(currentTaikoNoteAddon));
			}
		});

		maniaNoteComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				currentManiaNoteAddon = maniaNoteComboBox.getSelectedItem().toString();
				maniaPreviewLabel.setIcon(new ImageIcon(maniaNoteAddons.get(currentManiaNoteAddon).getPath() + "/preview.png"));
				maniaAddonsToApply.put(ADDON_TYPES[MANIA_INDEX][0], maniaNoteAddons.get(currentManiaNoteAddon));
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
					osuNoteAddons.put(addon.getName(), addon);
				if (addon.getType().equals(ADDON_TYPES[TAIKO_INDEX][0]))
					taikoNoteAddons.put(addon.getName(), addon);
				if (addon.getType().equals(ADDON_TYPES[MANIA_INDEX][0]))
					maniaNoteAddons.put(addon.getName(), addon);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
