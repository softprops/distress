package distress

import xsbti.{ AppMain, AppConfiguration }

object App {
  def apply(args: Array[String]): Int = {
    val (url, concur, req) = args match {
      case Array() => ("http://www.google.com", 10, 10)
      case Array(url) => (url, 10, 10)
      case Array(url, c) => (url, c.toInt, 10)
      case Array(url, c, r) => (url, c.toInt, r.toInt)
    }
    val r = Run(url, concur, req)_
    for(i <- 0 until 4) r(SilentReport) // heat blanket for jvm
    r(ConsoleReport)
    0
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
