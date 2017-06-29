package com.alextanhongpin.db

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{
    BSONDocumentWriter, BSONDocumentReader, Macros, document
}
import reactivemongo.api.collections.bson.BSONCollection
import com.typesafe.config.ConfigFactory
// Required for getStringList in the config factory
import collection.JavaConversions._

trait HealthRepo {
    val mongoURIs = ConfigFactory.load().getStringList("mongodb.servers").toList
    // val mongoUri = "mongodb://localhost:27017"
    println("Got mongoURI =>" + mongoURIs)

    import ExecutionContext.Implicits.global

    // Connect to the database. Must be done once per application
    val driver = new MongoDriver
    val parsedURI = MongoConnection.parseURI(mongoURIs(0))
    val connection = parsedURI.map(driver.connection(_))

    // Database and collections: Get references
    val futureConnection = Future.fromTry(connection)
    val dbName = ConfigFactory.load().getString("mongodb.db")
    def db1: Future[DefaultDB] = futureConnection.flatMap(_.database(dbName))
    // def db2: Future[DefaultDB] = futureConnection.flatMap(_.database("anotherdb"))
    def personCollection = db1.map(_.collection("person"))

    // Write documents: insert or update
    implicit def personWriter: BSONDocumentWriter[Person] = Macros.writer[Person]
    
    def createPerson(person: Person): Future[Unit] =
        personCollection.flatMap(_.insert(person).map(ok => println(ok))) // use personWriter
    

    // val person = Person("john", "doe", 1)
    // createPerson(person)
    // println("created person")
    def updatePerson(person: Person): Future[Int] = {
        val selector = document(
            "firstName" -> person.firstName,
            "lastName" -> person.lastName
        )

        // Update the matching person
        personCollection.flatMap(_.update(selector, person).map(_.n))
    }

    implicit def personReader: BSONDocumentReader[Person] = Macros.reader[Person]

    def findPersonByAge(age: Int): Future[List[Person]] =
        personCollection.flatMap(_.find(document("age" -> age)).
            cursor[Person]().collect[List]()) // Collect usng the result cursor

    /*val person = findPersonByAge(1).onComplete {
        case Failure(e) => e.printStackTrace()
        case Success(writeResult) =>
            println(s"successfully get document with result: $writeResult")
    }*/
    // person.map(_ => println)
    // Custom persistent type
    case class Person(firstName: String, lastName: String, age: Int)
}


// object AkkaHttpHelloWorld extends App with HealthRepo {}
 