import scala.concurrent.{Await, Future}

import scala.concurrent.duration._

object Test extends App {

  val count = java.lang.Integer.parseInt(args(0))
  val path = args(1)

  val res: List[Future[Long]] = (1 to count).map{ i =>
    val start = System.currentTimeMillis()
    import dispatch._, Defaults._
    val svc = url(s"http://127.0.0.1:8080/$path")
    Http(svc OK as.String).map( r => System.currentTimeMillis() - start)
  }.toList

  import scala.concurrent.ExecutionContext.Implicits.global
  val a: Future[List[Long]] = Future.sequence(res)
  val times = Await.result(a, 1.minute)
  val min = times.min
  val max = times.max
  val mean = times.sum.toDouble / count
  val st = times.toArray.sorted
  val median = st(count/2)
  val ninetyFiveCentile = st(count*95/100)

  println(
    s"""
       |Total:  $count
       |Min:    $min
       |Max:    $max
       |Mean:   $mean
       |Median: $median
       |95cent: $ninetyFiveCentile
     """.stripMargin)

}