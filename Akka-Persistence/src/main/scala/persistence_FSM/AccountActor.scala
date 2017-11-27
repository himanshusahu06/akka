package persistence_FSM

import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState

import scala.reflect._

class AccountActor extends PersistentFSM[Account.State, Account.Data, Account.DomainEvent] {

  import Account._

  override def persistenceId: String = "persisted-fsm-id"

  override def domainEventClassTag: ClassTag[DomainEvent] = classTag[DomainEvent]

  override def applyEvent(domainEvent: DomainEvent, currentData: Data): Data = {
    domainEvent match {
      case AcceptedTransaction(amount, Credit) =>
        val newAmount = currentData.amount + amount
        println(s"New balance is ${newAmount}")
        Balance(newAmount)
      case AcceptedTransaction(amount, Debit) =>
        val newAmount = currentData.amount - amount
        println(s"New balance is ${newAmount}")
        if (newAmount > 0)
          Balance(newAmount)
        else
          ZeroBalance
      case RejectedTransaction(_, transactionType, reason) =>
        println(s"--->[ERROR] ${transactionType} transaction failed with reason: ${reason}")
        currentData
    }
  }

  startWith(Empty, ZeroBalance)

  when(Empty) {
    case Event(Operation(amount, Credit), _) =>
      println(s"Hi, It's your first Credit Operation.")
      goto(Active) applying AcceptedTransaction(amount, Credit)
    case Event(Operation(amount, Debit), _) =>
      println(s"Sorry your account has zero balance.")
      stay applying RejectedTransaction(amount, Debit, "Balance is Zero")
  }

  when(Active) {
    case Event(Operation(amount, Credit), balance) =>
      stay applying AcceptedTransaction(amount, Credit)
    case Event(Operation(amount, Debit), balance) =>
      val newBalance = balance.amount - amount
      if (newBalance >= 0) {
        stay applying AcceptedTransaction(amount, Debit)
      } else if (newBalance == 0) {
        goto(Empty) applying AcceptedTransaction(amount, Debit)
      } else {
        stay applying RejectedTransaction(amount, Debit, "Insufficient balance")
      }
  }
}

object Account {

  // fsm state
  sealed trait State extends FSMState

  case object Empty extends State {
    override def identifier: String = "Empty"
  }

  case object Active extends State {
    override def identifier: String = "Active"
  }


  // fsm data
  sealed trait Data {
    val amount: Float
  }

  case object ZeroBalance extends Data {
    override val amount: Float = 0.0f
  }

  case class Balance(override val amount: Float) extends Data


  // persistence events
  sealed trait DomainEvent

  case class AcceptedTransaction(amount: Float, transactionType: TransactionType) extends DomainEvent

  case class RejectedTransaction(amount: Float, transactionType: TransactionType, reason: String) extends DomainEvent


  // Tranaction types
  sealed trait TransactionType

  case object Credit extends TransactionType

  case object Debit extends TransactionType


  // Commands
  case class Operation(amount: Float, transactionType: TransactionType)

}
