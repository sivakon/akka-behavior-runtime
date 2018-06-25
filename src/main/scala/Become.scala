/**
  * MANIPULATING ACTOR BEHAVIOR RUNTIME
  * Become/unbecome
  */

import UserStorage.{Connect, DBOperation, DisConnect, Operation}
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Stash, Status}
// change actor behavior at runtime

case class User(username: String, email: String)

object UserStorage {

  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Write extends DBOperation
  }

  case object Connect
  case object DisConnect
  case class Operation(dbOperation: DBOperation, user: Option[User])

}

class UserStorage extends Actor with Stash {

  def connected : Receive = {
    case DisConnect =>
      println(s"User storage disconnected from DB")
      context.unbecome()
    case Operation(op, user) =>
      println(s"User storage receive ${op} to do in the user: ${user}")
  }
  def disconnected: Receive = {
    case Connect =>
      println(s"User storage connected to DB")
      unstashAll()
      context.become(connected)
    // create a default method in disconnect for stashing purposes
    case _ =>
      stash()
  }

  def receive = disconnected
}

object BecomeHotSwap extends App {
  import UserStorage._

  val system = ActorSystem("HotSwap-become")

  val userStorage = system.actorOf(Props[UserStorage], "userStorage")

  // this is usual route, connect, operation, disconnect.
  // If we swap connect and operation, operation first, connect later => we will
  // the first message

  userStorage ! Operation(DBOperation.Create, Some(User("Admin", "siva@sivakon.com")))

  userStorage ! Connect

  userStorage ! Operation(DBOperation.Create, Some(User("Admin", "siva@sivakon.com")))

  userStorage ! DisConnect

  Thread.sleep(100)

  system.terminate()

}