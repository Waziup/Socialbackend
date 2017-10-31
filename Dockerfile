FROM jboss/wildfly

ADD target/SocialBackend.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 9123

ENTRYPOINT ["/opt/jboss/wildfly/bin/standalone.sh"]

CMD ["-Djboss.http.port=9123", "-b", "0.0.0.0"]

