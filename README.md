# P05_DAJAVA_SpringBoot_EndPoints

## First Run

Build project with Maven file _pom.xml_ to import all dependencies.
    
    mvn package
    mvn clean install
    
## Resources
    
The project need _Java JDK 11_ or newer.
Open JDK is recommended: https://adoptopenjdk.net

The project use _Spring Boot 2.3_ https://start.spring.io 

### Dependencies 

    Lombok, 
    Spring Web (Tomcat),
    Spring boot Actuator (httptrace is used to log end-points activities),
    Thymeleaf (used here for static pages),
    Springfox-swagger-ui for End-Points Html page


## Configuration
You can feed application with a json File. The URL can be changed in *application.properties* file. Here is a sample :


	#Spring Configuration Metadata
    app.alerts.json-file-path=file URL or PATH
    
## Application End-Points

All application End-Points can be find on one html page (swagger):

    http://localhost:8080/swagger-ui.html