<h1>CLC CLI</h1>
<h2>General</h2>

App requirements are described [here](Homework%20Java%20Developer%20role.pdf).

Out of given spec is removal of `--token` option. Token is moved to system variables along with API endpoint, 
described in [Requirements](#requirements). There are several reasons for that:

* Passing global options (options before command name) are not supported in Spring Shell 2.x onwards because it causes 
confusion if the options are intended for SpringBoot or Shell app. 
More about it [here](https://github.com/spring-projects/spring-shell/discussions/613).
* From UX perspective it would be cumbersome for user to enter token for each call. This way it is set once and reused seamlessly.
* Seven Bridges Client library docs recommend storing this piece of information in sytem variables.

App is created in Spring Boot, using Spring Shell project, GraalVM Native Image Support and Seven Bridges Client library.
It can be built with any Java SDK, but in order to build native app GraalVM SDK must be used.

Building executable jar:
```angular2html
./mvnw -Pnative native:compile 
```
Building native app:
```angular2html
./mvnw clean compile package
```
After building native app executable called `cgc-cli` can be found in `target` folder.
<h2 name="requirements">Requirements</h2>
For this app to run properly 2 enviroment variables must be set:
1. `SB_AUTH_TOKEN` - Token acquired from [Cancer Genomics Cloud Platform](https://cgc.sbgenomics.com/developer/token)
2. `SB_API_ENDPOINT` - Endpoint URI that can be found on the same page

If these variables are not set in the environment application will warn you about that on each attempt to interact with the service.
