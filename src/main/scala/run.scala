package distress

import dispatch._
import java.util.concurrent.atomic.AtomicInteger
import com.ning.http.client.{
  AsyncHttpClient, AsyncHttpClientConfig, Response
}

class Noop extends FunctionHandler[Response](identity)

class Run(uri: String, concurrency: Int, total: Int) {
  val req = url(uri)
  val client = new Http {
    override lazy val client =
      new AsyncHttpClient(new AsyncHttpClientConfig.Builder()
        .setMaximumConnectionsTotal(concurrency)
        .setMaximumConnectionsPerHost(concurrency)
        .setMaxRequestRetry(3)
        .build())
  }
  @volatile
  var successes = Map.empty[Int, AtomicInteger]
  val completed = newCounter
  val errors = newCounter
  val start = System.currentTimeMillis
  val requests = for (r <- 0 until total) yield {
    client(req > new Noop)
      .onComplete({
        case _ =>
          completed.incrementAndGet()
      })
      .onSuccess({
        case r =>
          successes = successes + (
            r.getStatusCode -> {
              val s = successes.getOrElse(r.getStatusCode, newCounter)
              s.incrementAndGet()
              s
            })
      })
      .onFailure({
        case _ =>
          errors.incrementAndGet()
      })
  }
  Promise.all(requests)
    .onComplete {
      case r =>
        Http.shutdown()
        println("%20s\t%s" format("status","count"))
        successes map {
          case (s, ai) =>
            println("%20s\t%s" format(s,ai.get()))
        }
  }
  .recover {
    case _ => Http.shutdown()
  }
}

object Main {
  def main(arg: Array[String]) {
    new Run("http://www.google.com", 1, 10)
  }
}
