package minho.rb

class Point(xc: Int, yc: Int) {
  var x: Int = xc
  var y: Int = yc
  def move(dx: Int, dy: Int) {
    x = x + dx
    y = y + dy
  }
  override def toString(): String = "(" + x + ", " + y + ")";
}

object Classes extends App {
	var p = new Point(1,2)
	println(p)
	p.move(1,1)
	println(p)
}
