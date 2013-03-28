package minho.rb


object MatchTest1 extends App {
  def matchTest(x: Int): String = x match {
    case 1 => "one"
    case 2 => "two"
    case _ => "many" // WHAT THE F*CK IS THIS?
  }
  println(matchTest(3))
}

object MatchTest2 extends App {
  def matchTest(x: Any): Any = x match {
    case 1 => "one"
    case "two" => 2
    case y: Int => "scala.Int"
    // Pattern Matching with Any --> SWEET
  }
  println(matchTest("two"))
}