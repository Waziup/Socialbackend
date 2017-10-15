FROM jboss/wildfly

ADD target/SocialBackend.war /opt/wildfly/standalone/deployments/
