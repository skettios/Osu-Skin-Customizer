package com.skettios.osc;

import javax.swing.*;
import java.io.File;

public class Start
{
	public static void main(String[] args)
	{
		File json = new File("Customizer.json");
		if (!json.exists())
		{
			JOptionPane.showMessageDialog(null, "Couln't find Customizer.json! \nThis is needed for the program to run!", "Error!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		OsuSkinCustomizer gui = new OsuSkinCustomizer();
		gui.start();
	}
}
