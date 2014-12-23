===============================
====== Table of Contents ======
===============================
1. What is this?
2. How do I use this?
4. Reference



===============================
====== 1. What is this? =======
===============================
This is simply a way to have multiple style of skins without having to package each individual style and all the combinations of them.



===============================
==== 2. How do I use this? ====
===============================
In order to understand how this works, you need to understand the folder format for this. 
Each addon should have individual folders for each type. For example, you would put your "Other" addon for "Osu" that replaces notes in "addons/Other/Osu/Note" or something similar.
Once you have the folder structure, you should now know what to edit in "Customizer.json". In "Customizer.json", it is just a giant array of addons that describe the addon to the system.
First the application should be in the root of your skin folder. With that, you need to have "Customizer.json" in the root as well. The json format is pretty self explanatory.
The type field is the type of the addon, it will be put into categories in the application.
The name field is the name of the addon, it will also show up as the display name in the application.
The path file is the path that the system will use to copy to your skin directory.
Note: It is always helpful to always have default skin replacements.
Note 2: If an image called "preview.png" is put in the addon path, it will be displayed.