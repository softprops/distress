package distress

trait Report {
  def apply(s: Stats): Unit
}

object SilentReport extends Report {
  def apply(s: Stats) = {}
}

object JsonReport extends Report with Math {
  import org.json4s.JsonDSL._
  import org.json4s.native.JsonMethods._
  def apply(s: Stats) {
    val avg = mean(s.timings)
    println(pretty(render(
      ("uri" -> s.uri) ~ ("concurrency" -> s.concurrency) ~ ("total" -> s.total) ~
      ("errors" -> s.errors) ~ ("completed" -> s.completed) ~ ("min" -> s.timings.min) ~
      ("max" -> s.timings.max) ~ ("avg" -> avg) ~ ("stddev" -> stddev(s.timings, avg)) ~
      ("status" -> s.byStatus.map {  case (s, cnt) => (s.toString, cnt) })
    )))
  }
}

object ConsoleReport extends Report with Math {
  def apply(s: Stats) {
    println("uri: %s concurrency: %s total: %s"
            .format(s.uri, s.concurrency, s.total))
    col("errors", s.errors)
    col("completed", s.completed)
    col("min", s.timings.min)
    col("max", s.timings.max)

    val avg = mean(s.timings)
    col("μ", avg)
    col("σ", stddev(s.timings, avg))
    col("status","count")
    s.byStatus.foreach(col)
  }

  private def col(label: Any, value: Any): Unit =
    println("%20s\t%s" format(label, value))

  private def col(t: (Int, Long)): Unit = t match {
    case (l, r) => col(l.toString, r)
  }
}
