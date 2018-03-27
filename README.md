# OpenDiabetes


## Getting started

[Download](https://github.com/lucasbuschlinger/BachelorPraktikum/archive/master.zip) the project source code or clone the git repository from GitHub.

## Setting up the project

The project was setup using [IntelliJ IDEA](https://www.jetbrains.com/idea/) which is recommended to use, but the internal build system that is used (Ant) makes it possible to integrate the project in almost any common IDE like [NetBeans](https://netbeans.org/) or [Eclipse](https://eclipse.org/getting_started/). 

The guide covers how to setup the project using [IntelliJ IDEA](https://www.jetbrains.com/idea/), for a detailed description in other IDE's refer to the [Sample Plugin Project](https://github.com/Magnusgaertner/OpenDiabetesPluginTutorial/blob/master/README.md#importing-and-building-with-ides)

### IntelliJ
1. Use File > Open... to import the sources. 

2. Mark the BaseLibs and libs folders as Resources Root and add the jars inside as libraries to the project. Also mark src as Sources Root. 

3. Right-click the build.xml and add it as an Ant build file. You can then open the belonging pane under View > Tools Windows > Ant Build. 

4. Finally, you have to set the project's SDK under File > Project Structure... > Project. The targets can be executed by simply double clicking them.

## Further steps

You now have a working OpenDiabetes project setup on your machine. The following topics guide to documents that will help you on different tasks in the project: 

- [Building project dependencies](https://github.com/lucasbuschlinger/BachelorPraktikum/wiki/Building-(Ant-Targets))
- [How to write your plugin](https://github.com/lucasbuschlinger/BachelorPraktikum/wiki/Creating-Plugins)
- [Project architecture for contributions](https://github.com/lucasbuschlinger/BachelorPraktikum/wiki/Plugin-Architecture)
- [Class documentation (JavaDoc)](https://lucasbuschlinger.github.io/BachelorPraktikum/)
