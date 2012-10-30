package distress

trait Report {
  def apply(s: Stats): Unit
}

object ConsoleReport extends Report with Math {
  def apply(s: Stats) {
    println("uri: %s concurrency: %s total: %s" format(s.uri, s.concurrency, s.total))
    println("%20s\t%s" format("errors", s.errors))
    println("%20s\t%s" format("completed", s.completed))
    val avg = mean(s.timings)
    println("%20s\t%s" format("μ", avg))
    println("%20s\t%s" format("σ", stddev(s.timings, avg)))
    println("%20s\t%s" format("status","count"))
    s.byStatus map {
      case (s, ai) =>
        println("%20s\t%s" format(s,ai.get()))
    }
  }
}