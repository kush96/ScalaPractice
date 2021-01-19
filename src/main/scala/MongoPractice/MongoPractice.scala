import org.mongodb.scala._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object MongoPractice extends App{
  val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017/")
  val databaseList = mongoClient.listDatabaseNames()
//  mongoClient.
//  databaseList.foreach(x=>println(x))
  val database: MongoDatabase = mongoClient.getDatabase("mydb")
//  println(database.listCollectionNames())
  val dummyCollection = database.getCollection("users")
  val doc :Document = Document("name"->"Kush","info"->Document("address"->"Noida"))
//  dummyCollection.insertOne(doc)
  dummyCollection.countDocuments().foreach(x=>println(x))
//  val docs = dummyCollection.find().collect().subscribe(result)
//  dummyCollection.find().collect().subscribe((results: Seq[Document]) => println(s"Found: #${results.size}"))
  val e =dummyCollection.find().toFuture()
  dummyCollection.find().collect().filter(_%2==0)
  val f =Await.result(e,2 seconds)
  println("F = "+f)
}