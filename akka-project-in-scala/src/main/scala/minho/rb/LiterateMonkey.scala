package minho.rb

import annotation.tailrec

import scala.util.Random
import akka.actor.{Props, ActorSystem, Actor}
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import akka.util.duration._
import akka.dispatch.{Await, Future}

case class Result(shakespeareMagic: Set[String], unworthyWords: Set[String])

object LiterateMonkey extends App {
  lazy val system = ActorSystem("ShakespeareGenerator")
  lazy val shakespeare = system.actorOf(Props[ShakespeareActor], "shakespeare")
  implicit val timeout = Timeout(5000 milliseconds)
  val start = System.nanoTime
  val numberMonkeys = 1000

  (shakespeare ? numberMonkeys) onSuccess { case result : Result =>
    // We have a result - make some fancy pantsy presentation of it
    val builder = new StringBuilder
    builder.append("SHAKESPEARE WORDS:\n")
    result.shakespeareMagic.foreach { w => builder.append(w + "\n") }
    builder.append("UNWORTHY WORDS CREATED: " + result.unworthyWords.size + "\n")
    builder.append("In " + (System.nanoTime - start)/1000 + "us\n")
    println(builder.toString)
  }
}

class ShakespeareActor extends Actor {
  import ShakespeareActor._

  lazy val randomGenerator = new Random
  implicit val timeout = Timeout(5000 milliseconds)

  import context.dispatcher

  def receive = {
    case actors: Int =>
      val futures = for (x <- 1 to actors) yield {
        context.actorOf(Props[MonkeyWorker]) ? randomGenerator.nextInt(100) mapTo manifest[Set[String]]
      }

      Future.sequence(futures) map { wordSets =>
        val mergedSet = wordSets reduce ( (a, b) => a ++ b )
        val (shakespeare, unworthy) = mergedSet partition (x => Blueprint.contains(x))
        Result(shakespeare, unworthy)
      } pipeTo sender
  }
}

object ShakespeareActor {
  lazy val Blueprint = Set("to", "be", "or", "not")
}

class MonkeyWorker extends Actor {
  import WorkerActor._

  lazy val randomGenerator = new Random
  
  def receive = {
    // We simplify the word generation by saying that every word can be between 1 and 26 letters.
    // If in real life, i.e. when using monkeys, some statistical bias would be useful when handing out the
    // type writing instructions.
    case tries: Int =>
      var words = Set.empty[String]
      1 to tries foreach { x => words += generateWork(randomGenerator.nextInt(Letters.size)) }
      sender ! words
  }

  def generateWork(letters: Int) = {
    @tailrec
    def trGeneration(letterNumber: Int, result: String): String = letterNumber match {
      case 0 => result.reverse
      case n => trGeneration(n - 1, result + Letters(randomGenerator.nextInt(Letters.size - 1)))
    }

    trGeneration(letters, "")
  }
}

object WorkerActor {
  lazy val Letters = ('a' to 'z').toSeq
}
