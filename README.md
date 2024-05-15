
[![Build Status](https://travis-ci.org/codecentric/springboot-sample-app.svg?branch=master)](https://travis-ci.org/codecentric/springboot-sample-app)
[![Coverage Status](https://coveralls.io/repos/github/codecentric/springboot-sample-app/badge.svg?branch=master)](https://coveralls.io/github/codecentric/springboot-sample-app?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Minimal [Spring Boot](http://projects.spring.io/spring-boot/) sample app.

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `de.codecentric.springbootsample.Application` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Deploying the application to OpenShift

The easiest way to deploy the sample application to OpenShift is to use the [OpenShift CLI](https://docs.openshift.org/latest/cli_reference/index.html):

```shell
oc new-app codecentric/springboot-maven3-centos~https://github.com/codecentric/springboot-sample-app
```

This will create:

* An ImageStream called "springboot-maven3-centos"
* An ImageStream called "springboot-sample-app"
* A BuildConfig called "springboot-sample-app"
* DeploymentConfig called "springboot-sample-app"
* Service called "springboot-sample-app"

If you want to access the app from outside your OpenShift installation, you have to expose the springboot-sample-app service:

## Auth - SpringSecurity
## DB - PostgreSQL

## INSTRUCTION BY DINGUS
##
## Зайдите в -application.properties- измените -spring.datasource.password=9064- на ваш пароль
## пароль от PostgreSQL
## зайдите в pgAdmin4
## запустите -EducationPlatformApplication-
## Если выводит ошибку соединения, тогда зайдите в -application.properties- и сверху напишите
## -server.port = yourport- порт можно писать любой
## если вылезет ошибка соединения - проставьте порт еще в gAdmin4
##
## для получения данных с базы зайдите в pgAdmin4, кликните file->preferences, проматайте вниз до 
## -Binary paths- поставьте 16-ю, укажите путь к бину PostgreSQL, после этого кликните по -education_platform-
## нажмите на -restore- и выберите .sql файл
