---
layout: home
title: Installation of the plugin
permalink: /installation
---

## Installing BlueJ

You can download the installer for your operating system on the [homepage](https://www.bluej.org/) of the BlueJ project. Remember to take note of where the program is being installed as we will need to know this later on.

## Required files

The plugin consists of three (3) files:

* BlueJPlugin.jar: the main plugin file. [Download here](assets/BlueJPlugin.jar)
* Intentionals.jar: required to override certain checks on a per-variable basis. [Download here](assets/Intentionals.jar)
* groups.json: required to send questions to the teacher. The teacher will provide this file.

## Find your BlueJ installation directory

To install the plugin we first need to know where the BlueJ installation is placed. This will depend on your operating system and whether or not you specified a custom installation directory when installing the IDE. If you use Windows the most likely location is `C:\Program Files\BlueJ\`. When you use MacOS you can use Finder to navigate to the `Apps` folder, right-click on the BlueJ app and pick `Show package contents`.

## Place the files in the correct folders

To install the plugin, all three files have to be placed in the correct folder. Open your BlueJ installation folder and place:

* BlueJPlugin.jar in the `extensions2` folder,
* Intentionals.jar in the `userlib` folder.

For the last part we need to start BlueJ and open the `About BlueJ` menu. On Windows this will be located in the `Help` menu and for MacOS it is located in the `BlueJ` menu. In this window, click on the `Open folder` button. In the folder that is now opened, you should place the `groups.json` file you received from your teacher.

## Finished

The plugin is now installed. For more information on how to use the plugin, you can head over to the [usage page](usage).
