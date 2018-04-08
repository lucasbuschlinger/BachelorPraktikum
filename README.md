# OpenDiabetes


## Getting started

[Download](https://github.com/lucasbuschlinger/BachelorPraktikum/archive/master.zip) the project source code or clone the git repository from GitHub.

## Setting up the project

The internal build system that is used (Ant) makes it possible to integrate the project in almost any common IDE like [NetBeans](https://netbeans.org/).

This guide covers how to setup the project using [NetBeans](https://netbeans.org/).

The repository is already set up as a NetBeans project, which makes it easy to get started:

1. Use `File > Open Project` to open the import dialogue.

2. Select the folder where you cloned the repository to and click `Open Project`.

Now you are all set to start and develop plugins or make changes to the base.
Current mappings of Ant targets and NetBeans' buttons are (button = target):
- `Build Project` = plugin (building the plugins)
- `Run Project` = build-base (building the jar file for the OpenDiabetes Base)  

Other targets can be executed by right-clicking the `build.xml` in the `Projects` pane under `Run Target`.

## Further steps

You now have a working OpenDiabetes project setup on your machine. The following topics guide to documents that will help you on different tasks in the project:

- [Building project dependencies](https://github.com/lucasbuschlinger/BachelorPraktikum/wiki/Building-(Ant-Targets))
- [How to write your plugin](https://github.com/lucasbuschlinger/BachelorPraktikum/wiki/Creating-Plugins)
- [Project architecture for contributions](https://github.com/lucasbuschlinger/BachelorPraktikum/wiki/Plugin-Architecture)
- [Class documentation (JavaDoc)](https://lucasbuschlinger.github.io/BachelorPraktikum/)
