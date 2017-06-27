# Akka-OpenID client

Attempt to create an openid client with Akka-HTTP. Present version of Scala is `2.12.2`, but we are going to use Scala version `2.11.1` due to the library compatibility.

## Starting the database

Run the docker mongodb container

```bash
$ bash db.sh
$ docker exec -i -t akka-openid bash
```

## Reactive-Mongo

When using this library, you have to specify a logging framework, e.g. SLF4J. The example below shows how to include it in your `build.sbt`. The reason is because SLF4J is now used by the ReactiveMongo logging, so a SLF4J binding must be provided. Read more about it [here](http://reactivemongo.org/releases/0.12/documentation/tutorial/setup.html).

```sbt
"org.reactivemongo" % "reactivemongo_2.11" % "0.12.4",
"org.slf4j" % "slf4j-simple" % "1.7.25",
"org.slf4j" % "slf4j-api" % "1.7.25"
```

Else, the following error will be raised.

```bash
$ NoClassDefFoundError: : org/slf4j/LoggerFactory
```