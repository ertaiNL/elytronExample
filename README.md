# Wildfly Elytron Example

In these examples I try to integrate parts that many separate examples try to display. 
These examples are tested with Wildfly 11. As JBoss EAP 7.1 is based on Wildfly 11 it should also work there.

WARNING: Is work in progress

## How to run ##

First build the project, start Wildfly and start a CLI-session.

```
mvn clean verify
$JBOSS_HOME/bin/standalone.sh
$JBOSS_HOME/bin/jboss-cli.sh
```

Then add the security-module and restart wildfly

```
module add --name=nl.ertai.elytron.example.security-module --resources=security-module-1.0.0-SNAPSHOT.jar --dependencies=org.wildfly.security.elytron,org.wildfly.extension.elytron
shutdown
$JBOSS_HOME/bin/standalone.sh
$JBOSS_HOME/bin/jboss-cli.sh
```

Then the realm defined in the security-module and add that to a security-domain

```
/subsystem=elytron/custom-realm=ExampleRealm:add(module=nl.ertai.elytron.example.security-module,class-name=nl.ertai.elytron.example.realms.ExampleRealm,configuration={"DefaultGroup"="groupName","SecondParameter"="SomeValue"})
/subsystem=elytron/security-domain=ExampleDomain:add(realms=[{"realm"="ExampleRealm","role-decoder"="groups-to-roles"}],permission-mapper="default-permission-mapper",default-realm="ExampleRealm")
```

For the securityService JACC is used so that needs to be added

```
/subsystem=elytron/policy=jacc:add(jacc-policy={})
```

Then add a http-authenticator for Undertow

```
/subsystem=elytron/http-authentication-factory=ExampleDomain-http-authenticator:add(http-server-mechanism-factory=global, security-domain=ExampleDomain, mechanism-configurations=[{mechanism-name=BASIC}])
/subsystem=undertow/application-security-domain=ExampleDomain:add(http-authentication-factory=ExampleDomain-http-authenticator)
```

Then add a sasl-authenticator for EJB

```
/subsystem=elytron/sasl-authentication-factory=ExampleDomain-sasl-authenticator:add(sasl-server-factory=configured, security-domain=ExampleDomain, mechanism-configurations=[{mechanism-name=BASIC}])
/subsystem=ejb3/application-security-domain=ExampleDomain:add(security-domain=ExampleDomain)
```

## How to remove ##

Start wildfly and CLI
```
$JBOSS_HOME/bin/standalone.sh
$JBOSS_HOME/bin/jboss-cli.sh
```

Remove configuration
```
/subsystem=undertow/application-security-domain=ExampleDomain:remove
/subsystem=elytron/http-authentication-factory=ExampleDomain-http-authenticator:remove
/subsystem=elytron/policy=jacc:remove
/subsystem=elytron/security-domain=ExampleDomain:remove
/subsystem=elytron/custom-realm=ExampleRealm:remove
```

Remove the module
```
module remove --name=nl.ertai.elytron.example.security-module
```
