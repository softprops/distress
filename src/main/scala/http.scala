package distress

import dispatch.{ DaemonThreads, FunctionHandler, Http }
import com.ning.http.client.{
  AsyncHttpClient, AsyncHttpClientConfig, Response
}
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig
import com.ning.http.client.filter.RequestFilter
import java.util.{ concurrent => juc }

/**
 * A noop handler that simply tracks time in milliseconds
 */
class Timestamp extends FunctionHandler[Response](identity) {
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
