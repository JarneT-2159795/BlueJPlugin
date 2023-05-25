---
layout: home
title: Information for teachers
permalink: /teacher-info
---

## Creating groups

To create groups, please use the `groups-creator.py` script in the root of the directory. Fill in the emails of the teacher for each group and press Enter when done. This file has to be distributed to the students and placed in the BlueJ user directory according to the [installation instructions](installation).

## Recompiling the plugin

When the plugin has to be recompiled the `credentials.json` file has to be downloaded from the [Google Cloud Console](https://console.cloud.google.com/) and placed in the `resources` folder, together with `pmd-ruleset.xml`. On the homepage of the BlueJPlugin project, click `APIs & Services`. On the left on the screen, navigate to `Credentials` and download the OAuth 2.0 Client ID. Rename the file to match `credentials.json` and place it in the `resources` folder.
