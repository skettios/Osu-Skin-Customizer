package com.skettios.osc.builder;

import javax.swing.*;
import java.awt.*;

public class OSCBuilder extends JFrame {
	private JPanel mainPanel;
	private JTabbedPane tabs;

	public OSCBuilder() {
		setTitle("Osu Skin Customizer Builder");
		setResizable(false);
		setSize(800, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		mainPanel = new JPanel(new GridLayout());
		tabs = new JTabbedPane();
	}

	public void start() {
		tabs.setAlignmentX(LEFT_ALIGNMENT);
		tabs.setAlignmentY(TOP_ALIGNMENT);

		setupGeneralTab();

		mainPanel.add(tabs);
		add(mainPanel);

		setVisible(true);
	}

	private void setupGeneralTab() {
		JScrollPane panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.setName("General");

		tabs.add(panel, 0);
	}
}
