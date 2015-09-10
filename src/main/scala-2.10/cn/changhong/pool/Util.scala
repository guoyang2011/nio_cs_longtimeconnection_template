package cn.changhong.pool

import java.io.{Closeable, EOFException}
import java.nio.ByteBuffer
import java.nio.channels._
import java.util.concurrent.{ExecutorService, ThreadFactory, Executors}

import scala.annotation.StaticAnnotation

/**
 * Created by yangguo on 15/9/10.
 */
class UnThreadSafe extends StaticAnnotation

class ThreadSafe extends StaticAnnotation

class NoCompleted(msg:String) extends StaticAnnotation

case class NoAliveException(msg:String) extends Exception

object ClientConfig{
  implicit val threadPool=Executors.newFixedThreadPool(10)
}
object ServiceConfig{
  implicit val threadPool=Executors.newFixedThreadPool(10)
}

object Util{
  val backend_service_host="backend.service.host"
  val backend_service_host_value="localhost"

  val backend_service_port="backend.service.port"
  val backend_service_port_value="19999"

  def asycDomainRun(fn: =>Unit)={
    val thread=new Thread(new Runnable {
      override def run(): Unit = fn
    })
    thread.setDaemon(true)
    thread.start()
  }

  def asycRun(fn: => Unit)(implicit pool:ExecutorService) = {
    pool.submit(new Runnable {
      override def run(): Unit = fn
    })
  }
  def boundedReadDataFromChannel(channel:ReadableByteChannel):ByteBuffer={
    val contentSizeBuffer=readDataFromChannelToBuffer(channel,ByteBuffer.allocate(4))
    val buffer=readDataFromChannelToBuffer(channel,ByteBuffer.allocate(contentSizeBuffer.getInt()))
    contentSizeBuffer.clear()
    buffer
  }
  def unSafeClose(stream:Closeable)={
    try{
      if(stream!=null) stream.close()
    }catch{
      case ex:Throwable=>ex.printStackTrace()
    }
  }
  def readDataFromChannelToBuffer(channel:ReadableByteChannel,byteBuffer:ByteBuffer):ByteBuffer={
    requireChannelAlive(channel)
    while(byteBuffer.hasRemaining){
      if(channel.read(byteBuffer)== -1) {
        unSafeClose(channel)
        throw new EOFException(s"channel maybe closed")
      }
    }
    byteBuffer.rewind()
    byteBuffer
  }
  def requireChannelAlive(channel:Channel)={
    if(!channel.isOpen ) throw new NoAliveException("channel must be opened and connected")
  }
  def boundedWriteDataToChannel(msg:AnyRef,channel: WritableByteChannel):Int={
    requireChannelAlive(channel)
    val content=msg.toString
    val contentLength=content.size
    val length=4+contentLength
    val contentBuffer=ByteBuffer.allocate(length)
    contentBuffer.putInt(contentLength)
    contentBuffer.put(content.getBytes)
    contentBuffer.rewind()
    val size=channel.write(contentBuffer)
    contentBuffer.clear()
    size
  }

}

