===============================
====== Table of Contents ======
===============================
1. What is this?
2. How do I use this?
3. Reference



===============================
====== 1. What is this? =======
===============================
This is simply a way to have multiple style of skins without having to package each individual style and all the combinations of them.



===============================
==== 2. How do I use this? ====
===============================
To use this you must understand a file structure for your addons. For me I would put all my addons in an addons folder at the root of the skin, with this at the root as well.
You also need to configure every aspect of the customizer. From tabs, to categories to the addons themselves. You should have a default addon for each category as this is important.
After you have inputted all the categories and addons, go into the application and click apply to each default value twice. IE. Select default, click apply, select default, click apply.
This will set it up to where it knows the current status of the files and addons. Once that is done simply package all the addons and the customizer files with the skin and you have an instant
customizable skin.


===============================
======== 3. Reference =========
===============================
Tabs:
    - name
    - index

Categories:
    - tab
    - name

Addon:
    - category
    - name
    - path

Current:
    - tab
    - addons (as if you were writing the addon)