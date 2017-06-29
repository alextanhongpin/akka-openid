/*import scala.util.{ Failure, Success }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.{ MongoDriver, MongoConnection }
import reactivemongo.api.collections.bson.BSONCollection
// Write documents
import reactivemongo.bson._
import reactivemongo.bson.{ BSONDocument, BSONDocumentReader, Macros }
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.commands.MultiBulkWriteResult
import reactivemongo.api.collection.bson.BSONCollection
import reactivemongo.api.QueryOpts

import com.typesafe.config.ConfigFactory

case class Person(name: String, age: Int)

// import reactivemongo.api.MongoConnectionOptions

// val driver1 = new reactivemongo.api.MongoDriver
// val conOpts = MongoConnectionOptions(readPreference = "primary")
// val connection3 = driver1.connection(List("localhost"), options = conOpts)


val mongoURI = ConfigFactory.load().getStringList("mongodb.servers").toList(0)
val driver = new MongoDriver

val database = for {
    uri <- Future.fromTry(MongoConnection.parseURI(mongoURI))
    con = driver.connection(uri)
    dn <- Future(uri.db.get)
    db <- con.database(dn)
} yield db

database.onComplete {
    case resolution =>
        println(s"DB resolution: $resolution")
        driver.close()
}

def dbFromConnection(connection: MongoConnection): Future[BSONCollection] =
    val db = ConfigFactory.load().getString("mongodb.db")
    connection.database(db).
        map(_.collection("some-collection"))

// Write documents
val document1 = BSONDocument(
    "firstName" -> "john",
    "lastName" -> "doe",
    "age" -> 29)

def insertDoc1(coll: BSONCollection, doc: BSONDocument): Future[Unit] = {
    val writeRes: Future[WriteResult] = coll.insert(document1)

    writeRes.onComplete {
        case Failure(e) => e.printStackTrace()
        case Success(writeResult) =>
            println("successfully inserted document with result: $writeResult")
    }

    writeRes.map(_ => {}) // in this example, do nothing with success
}

val person = Person("John Doe", 29)

val future2 = personColl.insert(person)
future2.onComplete {
    case Failure(e) => throw e
    case Success(writeResult) => {
        println(s"successfully inserted document: $writeResult")
    }
}


// Error Handling
val future: Future[WriteResult] = personColl.insert(person)
val end: Future[Unit] = future.map(_ => {}).recover {
    case WriteResult.Code(11000) =>
        // If the result is defined with the error code 11000 (duplicate error)
        println("Match the code 11000")
    case WriteResult.Message("Must match this exact message") =>
        println("Match the error message")
    case _ => ()
}

// Insert multiple documents
def bsonCollection: reactivemongo.api.collections.bson.BSONCollection = ???
def persons: List[Person] = ???
val personColl = bsonCollection

val bulkResult1: Future[MultiBulkWriteResult] = 
    personColl.bulkInsert(ordered = false)(
        BSONDocument("name" -> "document1"),
        BSONDocument("name" -> "document2"),
        BSONDocument("name" -> "document3")
        )
val bulkDocs =
    persons.map(implicitly[personColl.ImplicitlyDocumentProducer](_))

val bulkResult2 = personColl.bulkInsert(ordered = true)(bulkDocs: _*)


// Update a document
val selector = BSONDocument("name" -> "john")
val modifier = BSONDocument(
    "$set" -> BSONDocument(
        "lastName" -> "London",
        "firstName" -> "Jack"),
        "$unset" -> BSONDocument("name" -> 1))

val futureUpdate1 = personColl.update(selector, modifier)

// Remove a document
val selector1 = BSONDocument("firstName" -> "John")
val futureRemove1 = personColl.remove(selector1)

futureRemove1.onComplete {
    case Failure(e) => throw e
    case Success(writeResult) => println("Successfully removed document")
}

// Remove only the first match
def removeFirst(selector: BSONDocument) = 
    personColl.remove(selector, firstMatchOnly = true)

def update(collection: BSONCollection, age: Int): Future[Option[Person]] = {
    import collection.BatchCommands.FindAndModifyCommand.FindAndModifyResult
    implicit val reader = Macros.reader[Person]

    val result: Future[FindAndModifyResult] = collection.findAndUpdate(
        BSONDocument("name" -> "James"),
        BSONDocument("$set" -> BSONDocument("age" -> 17)),
        fetchNewObject = true)
    
    result.map(_.result[Person])
}


implicit val writer = Macros.writer[Person]

def result(coll: BSONCollection): Future[coll.BatchCommands.FindAndModifyCommand.FindAndModifyResult] = coll.findAndUpdate(
  BSONDocument("name" -> "James"),
  Person(name = "Foo", age = 25),
  upsert = true)
  // insert a new document if a matching one does not already exist

def removedPerson(coll: BSONCollection, name: String)(implicit ec: ExecutionContext, reader: BSONDocumentReader[Person]): Future[Option[Person]] =
  coll.findAndRemove(BSONDocument("name" -> name)).
    map(_.result[Person])


// Performing queries
// findOne
def findOlder1(collection: BSONCollection): Future[Option[BSONDocument]] = {
    val query = BSONDocument("age" -> BSONDocument("$gt" -> 27))

    collection.find(query).one[BSONDocument]
}

// Limit result
def findOlder2(collection: BSONCollection) = {
    val query = BSONDocument("age" -> BSONDocument("$gt" -> 27))

    // Only fetch the name field for the result documents
    val projection = BSONDocument("name" -> 1)
    collection.find(query, projection).cursor[BSONDocument]().
        collect[List](25) // get up to 25 documents

}

def findNOlder(collection: BSONCollection, limit: Int) = {
    val queryBuilder =
        collection.find(BSONDocument("age" -> BSONDocument("$gt" -> 27)))

    queryBuilder.options(QueryOpts().batchSize(limit)).
        cursor[BSONDocument]().collect[List](10)
}

// Find and sort documents
def findOlder3(collection: BSONCollection) = {
    val query = BSONDocument("age" -> BSONDocument("$gt" -> 27))

    collection.find(query).
        sort(BSONDocument("lastName" -> 1)).
        cursor[BSONDocument].collect[List]()
}

// Readers

case class Person(id: BSONObjectID, firstName: String, lastName: String, age: Int)

object Person {
    implicit object PersonReader extends BSONDocumentReader[Person] {
        def read(doc: BSONDocument): Person = {
            val id = doc.getAs[BSONObjectID]("_id").get
            val firstName = doc.getAs[String]("firstName").get
            val lastName = doc.getAs[String]("lastName").get
            val age = doc.getAs[Int]("age").get

            Person(id, firstName, lastName, age)
        }
    }
}*/