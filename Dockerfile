# Use official OpenJDK 17 as the base image
FROM openjdk:17

# Set working directory in the container
WORKDIR /app

# Copy all project files into the container
COPY bin ./bin
COPY lib ./lib
COPY src ./src
COPY images ./images
COPY NewsWebApp.java ./

# Compile Java code
RUN javac -cp "lib/gson-2.10.1.jar" -d bin src/NewsWebApp.java

# Expose port 8080 for the web server
EXPOSE 8080

# Command to start your app
CMD ["java", "-cp", "bin:lib/gson-2.10.1.jar", "NewsWebApp"]
