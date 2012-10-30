package distress

import xsbti.{ AppMain, AppConfiguration }

object App {
  def apply(args: Array[String]): Int = {
    val (concur, req) = args match {
      case Array() => (10, 10)
      case Array(n) => (n.toInt, 10)
      case Array(c, r) => (c.toInt, r.toInt)
    }
    val r = Run("www.google.com", concur, req)_
    for(i <- 0 until 4) r(SilentReport)
    r(ConsoleReport)
    1
  }
}

object Main {
  def main(args: Array[String]) {
    System.exit(App(args))
  }
}

class Script extends AppMain {
  def run(conf: AppConfiguration) =
    new Exit(App(conf.arguments))
}

class Exit(val code: Int) extends xsbti.Exit
