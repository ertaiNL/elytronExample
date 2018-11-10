# Wildfly Elytron Example

In these examples I try to integrate parts that many separate examples try to display. 
These examples are tested with Wildfly 11. As JBoss EAP 7.1 is based on Wildfly 11 it should also work there.

WARNING: Is work in progress

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
Then restart Wildfly and run the `setup.cli` in the root of this project to configure Wildfly

## How to remove ##

When Wildfly is running run the `teardown.cli` in the root of this project to remove the configuration.
Then remove the module by running the following in the CLI
```
module remove --name=nl.ertai.elytron.example.security-module
```
