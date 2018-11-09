# Wildfly Elytron Example

In these examples I try to integrate parts that many separate examples try to display. 
These examples are tested with Wildfly 11. As JBoss EAP 7.1 is based on Wildfly 11 it should also work there.

WARNING: Is work in progress

## How to run ##

First build the project, start Wildfly and start a CLI-session.

`mvn clean verify`
`$JBOSS_HOME/bin/standalone.sh`
`$JBOSS_HOME/bin/jboss-cli.sh`

Then add the security-module and restart wildfly

`module add --name=nl.ertai.elytron.example.security-module --resources=security-module-1.0.0-SNAPSHOT.jar --dependencies=org.wildfly.security.elytron,org.wildfly.extension.elytron`
`shutdown`
`$JBOSS_HOME/bin/standalone.sh`
`$JBOSS_HOME/bin/jboss-cli.sh`

Then the realm defined in the security-module and add that to a security-domain

`/subsystem=elytron/custom-realm=ExampleRealm:add(module=nl.ertai.elytron.example.security-module,class-name=nl.ertai.elytron.example.realms.ExampleRealm,configuration={"DefaultGroup"="groupName","SecondParameter"="SomeValue"})`
`/subsystem=elytron/security-domain=ExampleDomain:add(realms=[{"realm"="ExampleRealm","role-decoder"="groups-to-roles"}],permission-mapper="default-permission-mapper",default-realm="ExampleRealm")`

## How to remove ##

Start wildfly and CLI
`$JBOSS_HOME/bin/standalone.sh`
`$JBOSS_HOME/bin/jboss-cli.sh`

Remove configuration
`/subsystem=elytron/security-domain=ExampleDomain:remove`
`/subsystem=elytron/custom-realm=ExampleRealm:remove`

Remove the module
`module remove --name=nl.ertai.elytron.example.security-module`
