# Use a more specific base image
FROM eclipse-temurin:17-jdk-jammy AS build

# Install Node.js and npm
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set up Maven
ENV MAVEN_VERSION=3.9.9
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} \
    && ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

# Set working directory
WORKDIR /app

# Copy source code
COPY . .

# Install dependencies and build frontend
WORKDIR /app/frontend
RUN npm ci && \
    npm run build

# Build backend
WORKDIR /app/backend
RUN ./mvnw package -DskipTests

# Final stage
FROM eclipse-temurin:17-jre-jammy

# Copy built artifacts from the build stage
COPY --from=build /app/backend/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /app/backend/target/quarkus-app/*.jar /deployments/
COPY --from=build /app/backend/target/quarkus-app/app/ /deployments/app/
COPY --from=build /app/backend/target/quarkus-app/quarkus/ /deployments/quarkus/

# Copy the haarcascades directory
COPY --from=build /app/backend/haarcascades /deployments/haarcascades

WORKDIR /deployments

# Expose Port
EXPOSE 80

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
