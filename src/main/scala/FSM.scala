/**
  * MANIPULATING ACTOR BEHAVIOR RUNTIME
  * Same logic, but FSM style (more than 2 states)
  */

import akka.actor.{ActorSystem, FSM, Props, Stash}
import UserStorage._
// Using finite state machines
// change actor behavior at runtime

object UserStorageFSM {
  //FSM state
  sealed trait State
  case object Connected extends State
  case object Disconnected extends State

  //FSM data
  sealed trait Data
  case object EmptyData extends Data

  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Read extends DBOperation
    case object Update extends DBOperation
    case object Delete extends DBOperation
  }

  case object Connect
  case object Disconnect
  case class Operation(op: DBOperation, user: User)

  case class User(username: String, email: String)
}

class UserStorageFSM extends FSM[UserStorageFSM.State, UserStorageFSM.Data] with Stash {
  import UserStorageFSM._

  // 1. define start with
  startWith(Disconnected, EmptyData)
  // 2. define states
  when(Disconnected) {
    // FSM receives event
    case Event(Connect, _) =>
      println("User storage connected to DB")
      // goto event, like the one we had with Become/unbecome
      unstashAll()
      goto(Connected) using EmptyData
    case Event(_, _) =>
      stash()
      stay using EmptyData

  }
  // 3. initialize
  initialize()
}

object FSMApp extends App {
  import UserStorage._

  val system = ActorSystem("FSM")

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