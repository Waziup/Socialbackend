FROM jboss/wildfly

USER root
ENV JAVA_OPTS -Xms256m -Xmx512m -Djava.net.preferIPv4Stack=true

#Maven installation and configuration
ENV MAVEN_VERSION 3.5.2
RUN curl -sSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
&& mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
&& ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

#install dependencies
RUN  mkdir -p  /opt/Socialbackend
WORKDIR /opt/Socialbackend
ADD pom.xml /opt/Socialbackend
RUN mvn verify clean --fail-never

#install full app
ADD . /opt/Socialbackend
RUN mvn install

#deploy
RUN cp target/SocialBackend.war /opt/jboss/wildfly/standalone/deployments/

#Expose the port 9123 and run
EXPOSE 9123
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-Djboss.http.port=9123", "-Djboss.https.port=8444"]
