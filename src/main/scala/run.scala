package distress

import dispatch._
import java.util.concurrent.atomic.AtomicInteger

case class Stats(uri: String, concurrency: Int, total: Int,
                 completed: Int, errors: Int, timings: List[Long],
                 byStatus: Map[Int, Int])

object Run {
  def apply(uri: String, concurrency: Int, total: Int)(report: Report) = {
    val req = url(uri)
    val client = Client.of(concurrency)
    @volatile var successes = Map.empty[Int, Int]
    @volatile var timings = List.empty[Long]
    val completed = newCounter
    val errors = newCounter
    val start = System.currentTimeMillis
    val requests = for (r <- 0 until total) yield {
      val ts = new Timestamp
      client(req > ts)
        .onComplete({
          case _ =>
            completed.incrementAndGet()
            timings = ts.sinceRequested :: timings
        })
        .onSuccess({
          case r =>
            successes = successes + (
              r.getStatusCode -> {
                successes.getOrElse(r.getStatusCode, 1)
              })
        })
        .onFailure({
          case _ =>
            errors.incrementAndGet()
        })
    }
    (Http.promise.all(requests)
     .onComplete {
       case r =>
         client.shutdown()
         report(Stats(uri,
                      concurrency,
                      total,
                      completed.get(),
                      errors.get(),
                      timings,
                      successes))
     }
     .recover {
       case _ => client.shutdown()
     })()
  }
}
