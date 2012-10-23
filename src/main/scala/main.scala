package distress

object Main {
  def main(args: Array[String]) {
    val (concur, req) = args match {
      case Array() => (10, 10)
      case Array(n) => (n.toInt, 10)
      case Array(c, r) => (c.toInt, r.toInt)
    }
    new Run("http://www.google.com", concur, req)(ConsoleReport)
  }
}
