package distress

import dispatch.{ FunctionHandler, Http }
import com.ning.http.client.{
  AsyncHttpClient, AsyncHttpClientConfig, Response
}

import com.ning.http.client.filter.RequestFilter

class Timestamp extends FunctionHandler[Response](identity) {
  lazy val began = System.currentTimeMillis
  def sinceRequested = System.currentTimeMillis - began
  def begin = began
}

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

class Client(concurrency: Int) extends Http {
  override lazy val client =
    new AsyncHttpClient(new AsyncHttpClientConfig.Builder()
         //.setMaximumConnectionsTotal(concurrency)
                        .setMaximumConnectionsPerHost(concurrency)
                        .setMaxRequestRetry(3)
                        .addRequestFilter(new Stamper)
                        .build())
}
