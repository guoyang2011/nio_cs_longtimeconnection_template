package cn.changhong.redis

import java.util.{UUID, Collections}

import kafka.consumer.SimpleConsumer

/**
 * Created by yangguo on 15/9/2.
 */
case class OverrideHashCode(value:Int) extends Comparable[OverrideHashCode]{
  override def compareTo(o: OverrideHashCode): Int = value-o.value

}
object HashCode {
  def main(args: Array[String]) {

    val hc=OverrideHashCode(10)
    println(hc.hashCode())

  }
}
