FROM java:openjdk-8-jdk

CMD ["service firewall stop"]

#Create the work directory
RUN  mkdir -p  /opt/Socialbackend

#Move the API source to /opt
ADD . /opt/Socialbackend

#Maven installation and configuration
ENV MAVEN_VERSION 3.5.2

#Maven installation
RUN curl -sSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
&& mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
&& ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

#Expose the port 9123
EXPOSE 9123

#Compiling and running the API

WORKDIR /opt/Socialbackend

RUN mvn clean install wildfly-swarm:run

