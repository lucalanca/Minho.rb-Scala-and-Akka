package minho.rb

import akka.actor.Actor
import akka.actor.ActorLogging

class MsgEchoActor extends Actor with ActorLogging {
  def receive: Receive = {
    case message =>
      log.info("Received Message {} in Actor {}", message, self.path.name)
  }
}

import scala.actors.threadpool.TimeUnit;

import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinRouter

object RoundRobbin {
  def main(args: Array[String]): Unit = {
    val _system = ActorSystem("RoundRobinRouterExample")
    val roundRobinRouter = _system.actorOf(Props[MsgEchoActor].withRouter(RoundRobinRouter(5)), name = "myRoundRobinRouterActor")
    1 to 10 foreach {
      i =>
        roundRobinRouter ! i
        if (i == 5) {
          TimeUnit.MILLISECONDS.sleep(100);
          System.out.println("\n");
        }
    }
    _system.shutdown()
  }
}

import akka.routing.SmallestMailboxRouter

object SmallestMailbox {
  def main(args: Array[String]): Unit = {
    val _system = ActorSystem.create("SmallestMailBoxRouterExample")
    val smallestMailBoxRouter = _system.actorOf(Props[MsgEchoActor].withRouter(SmallestMailboxRouter(5)), name = "mySmallestMailBoxRouterActor")
    1 to 10 foreach {
      i => smallestMailBoxRouter ! i
    }
    _system.shutdown()
  }
}

import akka.routing.BroadcastRouter


object Broadcast {

  def main(args: Array[String]): Unit = {
    val _system = ActorSystem("BroadcastRouterExample")
    val broadcastRouter = _system.actorOf(Props[MsgEchoActor].withRouter(BroadcastRouter(5)), name = "myBroadcastRouterActor")
    1 to 1 foreach {
      i => broadcastRouter ! i
    }
    _system.shutdown()
  }

}

import akka.dispatch.Await
import akka.pattern.ask
import akka.routing.ScatterGatherFirstCompletedRouter
import akka.util.duration._
import akka.util.Timeout
import java.util.Random

class RandomTimeActor extends Actor with ActorLogging{
  val randomGenerator = new Random()
  def receive: Receive = {
    case message =>
      val sleepTime = randomGenerator.nextInt(5)
      log.info("Actor # {} will return in {}", self.path.name, sleepTime)
      TimeUnit.SECONDS.sleep(sleepTime);
      sender.tell("Message from Actor #" + self.path)
  }
}

object ScatterGatherFirstCompleted {
  def main(args: Array[String]): Unit = {
    val _system = ActorSystem("SGFCRouterExample")
    val scatterGatherFirstCompletedRouter = _system.actorOf(Props[RandomTimeActor].withRouter(
      ScatterGatherFirstCompletedRouter(nrOfInstances = 5, within = 5 seconds)), name = "mySGFCRouterActor")

    implicit val timeout = Timeout(5 seconds)
    val futureResult = scatterGatherFirstCompletedRouter ? "message"
    val result = Await.result(futureResult, timeout.duration)
    System.out.println(result)

    _system.shutdown()
  }
}