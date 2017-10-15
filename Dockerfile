FROM jboss/wildfly

ADD target/SocialBackend.war /opt/jboss/wildfly/standalone/deployments/
