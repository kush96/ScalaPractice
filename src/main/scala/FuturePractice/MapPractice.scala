package FuturePractice

object MapPractice extends App{
  val a =List(List(List(1,2,3),List(4,5,6)),List(List(7,8,9),List(10,11,12)))
  val b = List(List(1,2,3),List(4,5,6))
  val c = b.flatMap(l=>l)
//  println(b)
  val d = List(1,2,3)
  val e = d.map(x=>x+2).map(x=>x*3)
  print(e)
}
