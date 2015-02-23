package com.skettios.osc.client;

import java.io.File;

public class Start
{
	public static void main(String[] args)
	{
		OSCClient client = new OSCClient(new SettingsParser(new File("addons/settings.json")).parseSettings().getSettings());
		client.start();
	}
}
