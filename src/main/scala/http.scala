package distress

import dispatch.{ OkFunctionHandler, Http }
import com.ning.http.client.Response
import com.ning.http.client.filter.RequestFilter

/**
 * A noop handler that simply tracks time in milliseconds. Requests
 * that return a non-200 status response will throw a dispatch.StatusCode
 * exception
 */
class Timestamp extends OkFunctionHandler[Response](identity) {
  lazy val began = System.currentTimeMillis
  def sinceRequested = System.currentTimeMillis - began
  def begin = began
}

/**
 * If the underlying async handler happens to be
 * an intance of a Timestamp, tell it to begin
 */
class Stamper extends RequestFilter {
  import com.ning.http.client.filter.FilterContext
  def filter(fc: FilterContext[_]) = {
    fc.getAsyncHandler() match {
      case ts: Timestamp =>
        ts.begin
      case _ => ()
    }
    fc
  }
}

object Client {
  def of(concurrency: Int) =
     new Http().configure {
       _.addRequestFilter(new Stamper)
        .setMaximumConnectionsPerHost(concurrency)
     }
}
