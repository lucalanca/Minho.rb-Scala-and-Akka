package minho.rb

object ComprehensionTest1 extends Application {
  def even(from: Int, to: Int): List[Int] =
    for (i <- List.range(from, to) if i % 2 == 0) yield i
  Console.println(even(0, 20))
}

object ComprehensionTest2 extends Application {
  def foo(n: Int, v: Int) =
    for (i <- 0 until n;
         j <- i until n if i + j == v) yield
      Pair(i, j);
  foo(20, 32) foreach {
    case (i, j) =>
      println("(" + i + ", " + j + ")")
  }
}


