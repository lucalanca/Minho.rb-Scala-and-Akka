package minho.rb


abstract class Term
case class Var(name: String) extends Term
case class Fun(arg: String, body: Term) extends Term
case class Appz(f: Term, v: Term) extends Term

object CaseClasses extends Application {
	val foo = Fun("x", Fun("y", Appz(Var("x"), Var("y"))))
	println(foo)
}