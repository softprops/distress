import java.util.concurrent.atomic.AtomicLong
package object distress {
  def newCounter = new AtomicLong(0)
}
