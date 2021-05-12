This folder was initialized with the [Skeleton project template](https://git.uibk.ac.at/informatik/qe/skeleton).
See the original readme in [SKELETON_README.md](SKELETON_README.md).

## Docker deployment
To build the docker container run `docker build --pull --rm -f "Dockerfile" -t backend:latest "."` in your terminal from inside the `backend`-directory (where the Dockerfile and this readme are saved). Then run the application by executing `docker run --rm -d  -p 8080:8080/tcp backend:latest`.

## Code
The project utilizes Spring Boot and is configured as maven web application with:
- all relevant Spring Framework features enabled
- embedded Tomcat with support for JSF2
- embedded H2 in-memory database (including H2 console)
- support for PrimeFaces

## Running the project
 Execute  `mvn spring-boot:run` to start the skeleton project
 and connect to http://localhost:8080/ to access the skeleton
 web application. 
 
Logins: 
  - `admin:passwd`
  - `user1:passwd`
  - `user2:passwd`
  - `elvis:passwd`
