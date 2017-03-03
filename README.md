# Projet GL

#### Team 

Kouki Amri Sarra

#### For the Professor

- letter have to be Mutiplayer : OK ( you will find 2 jar files server.jar and client.jar)
- Save Score : OK

- Design Pattern :
    Singleton: visible in our Jeux class throu private constructor

    Lazy initialization : Initialization of the class Jeux fires only when there's a constructor call in the code

- SOLID : OK (Visible through all Game Package)

- TDD : only 2 tests (KO I guess)

- Use Maven/gradle : OK (Maven: pom.xml present at the root of out directory)

- Java/Maven conventions : OK


####How to run the game

Make sure you have ANT and Maven Installed and add to the system PATH_var

To run the game using the Jar file :

```
cd /dir
java -jar server.jar
java -jar client.jar

make sure the noms_communs.txt is in the root of your disk D:
```

To build the game for Eclpise(Marse/luna) :

```
mvn install
mvn clean install
```

