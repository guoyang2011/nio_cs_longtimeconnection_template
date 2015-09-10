package cn.changhong.pool

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels._
import java.nio.channels.SocketChannel
/**
 * Created by yangguo on 15/9/10.
 */
@UnThreadSafe
class BlockingChannel {
  var blockChannel: SocketChannel = null
  var connected: Boolean = false
  var conf: Map[String, String] = null
  var writableChannel: GatheringByteChannel = null
  var readableChannel: ReadableByteChannel = null
  val responseSizeBuffer = ByteBuffer.allocate(4)

  def this(config: Map[String, String]) = {
    this
    conf = config
  }

  def getInfo: String = {
    blockChannel.getLocalAddress.toString
  }

  def isConnected = connected

  def connect() = {
    if (!connected) {
      try {
        blockChannel = SocketChannel.open()
        blockChannel.configureBlocking(true)
        blockChannel.socket().setKeepAlive(true)
        blockChannel.socket().setTcpNoDelay(true)
        blockChannel.socket().connect(new InetSocketAddress(conf.getOrElse(Util.backend_service_host, Util.backend_service_host_value), conf.getOrElse(Util.backend_service_port, Util.backend_service_port_value).toInt))
        writableChannel = blockChannel
        readableChannel = Channels.newChannel(blockChannel.socket().getInputStream)
        connected = true
      } catch {
        case e: Throwable => {
          e.printStackTrace()
          disconnect()
        }
      }
    }
  }

  def disconnect() = {
    if (blockChannel != null) {
      try {
        blockChannel.close()
      } catch {
        case ex: Throwable =>
      }
      writableChannel = null
      readableChannel = null
      connected = false
    }
  }
  def sendRequest(request:String):String={
    Util.boundedWriteDataToChannel(request,writableChannel)
    val buffer=Util.boundedReadDataFromChannel(readableChannel)
    val res=new String(buffer.array())
    buffer.clear()
    res
  }
}


