import java.util.concurrent.atomic.AtomicInteger
package object distress {
  def newCounter = new AtomicInteger(0)
}
