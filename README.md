# SSCS HMC Stub

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sscs-hmc-stub&metric=coverage)](https://sonarcloud.io/summary/new_code?id=SSCSHA)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=sscs-hmc-stub&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=SSCSHA)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=sscs-hmc-stub&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=SSCSHA)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=sscs-hmc-stub&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=SSCSHA)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=sscs-hmc-stub&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=SSCSHA)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=sscs-hmc-stub&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=SSCSHA)

HMC Stub is a spring boot based application to mock HMC requests

## Notes

Since Spring Boot 2.1 bean overriding is disabled. If you want to enable it you will need to set `spring.main.allow-bean-definition-overriding` to `true`.

JUnit 5 is now enabled by default in the project. Please refrain from using JUnit4 and use the next generation

## Building and deploying the application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

```bash
  ./gradlew build
```

### Running the application

Create the image of the application by executing the following command:

```bash
  ./gradlew assemble
```

Create docker image:

```bash
  docker-compose build
```

Run the distribution (created in `build/install/sscs-hmc-stub` directory)
by executing the following command:

```bash
  docker-compose up
```

This will start the API container exposing the application's port
(set to `8100` in this template app).

In order to test if the application is up, you can call its health endpoint:

```bash
  curl http://localhost:8100/health
```

You should get a response similar to this:

```
  {"status":"UP","diskSpace":{"status":"UP","total":249644974080,"free":137188298752,"threshold":10485760}}
```

### Alternative script to run application

To skip all the setting up and building, just execute the following command:

```bash
./bin/run-in-docker.sh
```

For more information:

```bash
./bin/run-in-docker.sh -h
```

Script includes bare minimum environment variables necessary to start api instance. Whenever any variable is changed or any other script regarding docker image/container build, the suggested way to ensure all is cleaned up properly is by this command:

```bash
docker-compose rm
```

It clears stopped containers correctly. Might consider removing clutter of images too, especially the ones fiddled with:

```bash
docker images

docker image rm <image-id>
```

There is no need to remove postgres and java or similar core images.

### Other

Hystrix offers much more than Circuit Breaker pattern implementation or command monitoring.
Here are some other functionalities it provides:
 * [Separate, per-dependency thread pools](https://github.com/Netflix/Hystrix/wiki/How-it-Works#isolation)
 * [Semaphores](https://github.com/Netflix/Hystrix/wiki/How-it-Works#semaphores), which you can use to limit
 the number of concurrent calls to any given dependency
 * [Request caching](https://github.com/Netflix/Hystrix/wiki/How-it-Works#request-caching), allowing
 different code paths to execute Hystrix Commands without worrying about duplicating work

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

