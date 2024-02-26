
# ServiceMiner


## Build and Run :

You need only maven and a 1.8 jdk to run this project

**Build :**

    mvn clean install
**Run :**

    mvn exec:java@default "-Dexec.args=arg1 arg2 arg3"
**Build and Run  :**

    mvn clean install exec:java@default "-Dexec.args=arg1 arg2"

**Arguments :**
This program takes two or three arguments :

    argument 1 : kdm model xmi file location from the resources directory (with extension)
    argument 2 : kdm model name
   
    
# Necessary files

## KDM Model

In order to use this program, you need to have a generated a kdm xmi model from your project beforehand.
To do this you need the [MoDisco](https://www.eclipse.org/MoDisco/) plugin for eclipse and to discover xmi model from your project source code and then discover KDM model from this model, you will then obtain a xmi file containing a kdm model that you can feed to this program. Inside this file there should also be the model name that you need to put as an argument.

