package distress

trait Report {
  def apply(s: Stats): Unit
}

object SilentReport extends Report {
  def apply(s: Stats) = {}
}

object ConsoleReport extends Report with Math {
  def apply(s: Stats) {
    println("uri: %s concurrency: %s total: %s" format(s.uri,
                                                       s.concurrency,
                                                       s.total))
    col("errors", s.errors)
    col("completed", s.completed)
    col("min", s.timings.min)
    col("max", s.timings.max)
    col("errors", s.errors)
    col("completed", s.completed)

    val avg = mean(s.timings)
    col("μ", avg)
    col("σ", stddev(s.timings, avg))
    col("status","count")
    s.byStatus.foreach(col)
  }

  private def col(label: Any, value: Any): Unit =
    println("%20s\t%s" format(label, value))

  private def col(t: (Int, Int)): Unit = t match {
    case (l, r) => col(l.toString, r)
  }
}
