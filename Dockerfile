FROM ubuntu:22.04
# Install NodeJs and Java
RUN apt-get update && apt-get install curl -y && apt-get clean && curl -fsSL https://deb.nodesource.com/setup_20.x -o /tmp/nodesource_setup.sh
RUN bash /tmp/nodesource_setup.sh
RUN apt-get update && apt-get install nodejs openjdk-11-jdk wget -y && apt-get clean
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
# Add Maven dependencies
ENV MAVEN_VERSION 3.9.6
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH
RUN wget http://archive.apache.org/dist/maven/maven-3/"$MAVEN_VERSION"/binaries/apache-maven-"$MAVEN_VERSION"-bin.tar.gz && \
  tar -zxvf apache-maven-"$MAVEN_VERSION"-bin.tar.gz && \
  rm apache-maven-"$MAVEN_VERSION"-bin.tar.gz && \
  mv apache-maven-"$MAVEN_VERSION" /usr/lib/mvn
# Create directory for the app
RUN mkdir /opt/ghostface
COPY . /opt/ghostface
WORKDIR /opt/ghostface/frontend
RUN npm install -g @angular/cli && npm install && npm run build
WORKDIR /opt/ghostface
RUN mvn clean install
WORKDIR /opt/ghostface/backend
# Clean mvnw file from spaces and CR characters
RUN sed -i 's/\r$//' mvnw
EXPOSE 80
ENTRYPOINT ./mvnw quarkus:dev
