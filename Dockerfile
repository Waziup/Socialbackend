FROM jboss/wildfly

USER root

#Maven installation and configuration
ENV MAVEN_VERSION 3.5.2
RUN curl -sSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
&& mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
&& ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

RUN  mkdir -p  /opt/Socialbackend
WORKDIR /opt/Socialbackend

#install dependencies
ADD pom.xml /opt/SocialBackend
RUN mvn verify clean --fail-never

#install full app
ADD . /opt/Socialbackend
RUN mvn install 

#Expose the port 9123
EXPOSE 9123

#deploy
RUN cp target/SocialBackend-swarm.jar /opt/jboss/wildfly/standalone/deployments/
