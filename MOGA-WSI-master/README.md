
# MOGA-WSI

It's a java program with a javafx interface based on the paper *"A Spanning Tree Based Approach to Identifying Web Services"* by Hemant Jain, Huimin and Nageswara R. Chinta.
By using a static and dynamic dependency model and applying to it a maximum spanning tree, it generates a first model that is later refined with a genetic algorithm.


## Build and Run :

You need only maven and a 1.8 jdk to run this project

**Build :**

    mvn clean install
**Run :**

    mvn exec:java@default "-Dexec.args=arg1 arg2 arg3"
**Build and Run  :**

    mvn clean install exec:java@default "-Dexec.args=arg1 arg2 arg3"

**Arguments :**
This program takes two or three arguments :

    argument 1 : kdm model xmi file location from the resources directory (with extension)
    argument 2 : kdm model name
    argument 3 : dynamic dependencies json file location from the resources directory (with extension) (optional) 
    
**Example :**

    mvn clean install exec:java@default "-Dexec.args=test/miniTest_kdm.xmi miniTest test/miniTest.json"

**Other Profiles** 

You can also generate a json file for the serviceCutter software with this program, in order to use the second profile run this :


    mvn exec:java@generateJSONServiceCutter "-Dexec.args=arg1 arg2"
    
The two arguments are the same as the two first arguments for the regular execution


# Necessary files

## KDM Model

In order to use this program, you need to have a generated a kdm xmi model from your project beforehand.
To do this you need the [MoDisco](https://www.eclipse.org/MoDisco/) plugin for eclipse and to discover xmi model from your project source code and then discover KDM model from this model, you will then obtain a xmi file containing a kdm model that you can feed to this program. Inside this file there should also be the model name that you need to put as an argument.

## Dynamic Model

If you want to enhance your model with some usecases and the number of messages between classes you can add a custom json file with thos said links as they cannot really be deduced from the source code.
The syntax of this file is pretty straightforward : 

    [  
	  {  
		"name": "UseCase Name",  
		    "messages": [  
			     {  
			        "from": "classNameFrom",  
					"to": "classNameTo",  
				    "count": NumberofMessages  
				 },  [ other Messages...]  
			 ]
		 }, [ other UseCases... ]
	 ]

  **Example**

    [  
      {  
	      "name": "Test1",  
	      "messages": [  
	          {  
	            "from": "C1",  
		        "to": "C2",  
		        "count": 2  
		      },  
		      {  
	            "from": "C2",  
			    "to": "C3",  
		        "count": 1  
			  }  
	      ]  
      },  
      {  
	      "name": "Test2",  
	      "messages": [  
	          {  
			      "from": "C3",  
			      "to": "C2",  
			      "count": 12  
		      }  
	      ]  
      }  
    ]

## Interface

![Interface](http://image.noelshack.com/fichiers/2018/30/5/1532704240-serviceident.png)
![Interface2](http://image.noelshack.com/fichiers/2018/30/5/1532704935-serviceident2.png)
***Right canvas***
On the right you will see a graph either representing a hierarchical view of the classes and potential webservices based on the spanning tree or a webservices representation of the class set with clusters of classes representing a webservice.


***Top bar***
The button less and more services are used to select a catching point in terms of the number of services desired, it is linked to the hierarchical representation on the right which show at which point there are each number of webservices.
Once you click on validate slicing this number of webservices will be locked and it will go to ths second phase where the buttons on the right are activated and you can step the genetic algorithm with the three top buttons.
On the left you will have some informations as well as a button to save the fittest individual in a file on *yourHomeDirectory/bench/resultidentification.txt*.


***Left Panel***
On the left panel there are five variables that are modifiable :

**Population Size (integer) :** Change the size of the population for the next generation

**Mutation Rate (float 0..1) :** Chance for a gene to mutate

**Crossover Rate (float 0..1) :** Chance for two individuals to crossover

**Elite Number (integer) :** Number of individuals that are kept unchanged for the next generation

**Tournament Size (integer) :** Number of individuals that participate in a tournament where the fittest of the tournament is either used for crossover or kept in the next generation

There are also sliders to adjust the weight of each managerial goal

**Cohesion :** How the classes inside a service are coupled (more is more cohesion)

**Coupling :** How the classes inside a service are linked to classes from other services (more is less coupling)

**Complexity :** How the number of method in each service is roughly equal

**Component Count :** How the number os services is the one we want, also checks that each class is present and they are not repeated a lot, if the fittest doesn't have all classes or copies them, try to increase this value.

**Component Size :** How the number if classes in each service is roughly equal
> Weight is normalized so having each managerial goal at max will be the same as having them all almost at the minimum

There is a graph that shows the fittest fitness evolution over the generations, in theory it should never go down but it could if you modify the weights.

Finally there is also a progressbar to show each generation taking place.

## Things to keep track of


### Useful methods or variables
There is a method that removes all orphan nodes (nodes from which no dependencies where found) from the spanning tree as it would produces inaccurate results. The threshold of removing nodes needs to be changed directly from the orchestrator code right now.
There are also the weights of each link types that are quite arbitrary and can be changed to your liking in the LinkType enum.
Also the static field DYNAMIC_WEIGHT can be changed in MetamodelEnhancer to change the weight of the dynamic dependencies compared to the static dependencies computed
These should be read from a config file later.
