# Wildfly Elytron Example

In these examples I try to integrate parts that many separate examples try to display. 
These examples are tested with Wildfly 11. As JBoss EAP 7.1 is based on Wildfly 11 it should also work there.

This contains examples for:
- Usage of custom-realm
- Usage of custom-realm-mapper
- Usave of custom-role-mapper

## How to build ##

The project can simply be build with maven
```
mvn clean verify
```

## How to run ##

When Wildfly is started run the following in the CLI to install the module
```
module add --name=nl.ertai.elytron.example.security-module --resources=security-module-1.0.0-SNAPSHOT.jar --dependencies=org.wildfly.security.elytron,org.wildfly.extension.elytron
```
Make a backup of your standalone.xml.
Then restart Wildfly and run the `setup.cli` in the root of this project to configure Wildfly.

## How to remove ##

When Wildfly is running run the `teardown.cli` in the root of this project to remove the configuration.
Then remove the module by running the following in the CLI
```
module remove --name=nl.ertai.elytron.example.security-module
```

## Structure ##
The project consists of 4 maven modules.
- ear: This maven-module packages the ejb and war modules to 1 file.
- ejb: This maven-module contains the ejb that is used to check the authentication there.
- module: This maven-module should be added as a wildfly-module in your installation.
- war: This maven-module contains the webpage to view the status.

The maven-module contains the examples for elytron.