package distress

import com.ning.http.client.Response
import dispatch._, Defaults._
import scala.concurrent.Future

case class Stats(uri: String, concurrency: Int, total: Int,
                 completed: Long, errors: Long, timings: List[Long],
                 byStatus: Map[Int, Long])

object Run {
  import dispatch.retry.Success._
  def apply(uri: String, concurrency: Int, total: Int)(report: Report) = {
    val request = url(uri)
    val client = Client.of(concurrency)
    @volatile var successes = Map.empty[Int, Long]
    @volatile var timings = List.empty[Long]
    val completed = newCounter
    val errors = newCounter
    val requests: Seq[Future[Response]] = for (r <- 0 until total) yield {
      val ts = new Timestamp
      val res = client(request > ts)
      res.onComplete {
        case _ =>
          completed.incrementAndGet()
        timings = ts.sinceRequested :: timings
      }
      res.onSuccess {
        case r =>
          successes = successes + (
            r.getStatusCode -> (successes.getOrElse(r.getStatusCode, 0L) + 1L)
            )
      }
      res.onFailure {
        case _ =>
          errors.incrementAndGet()
      }
      res
    }
    val all = Future.sequence[Response, Seq](requests)
    all.onComplete {
      case _ =>
        client.shutdown()
        report(Stats(uri,
                     concurrency,
                     total,
                     completed.get(),
                     errors.get(),
                     timings,
                     successes))
    }
    all.recover {
      case _ => client.shutdown()
    }.apply()
  }
}
