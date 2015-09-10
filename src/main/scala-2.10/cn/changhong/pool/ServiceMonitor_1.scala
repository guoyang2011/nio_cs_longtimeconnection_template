package cn.changhong.pool

import java.net.{StandardSocketOptions, InetSocketAddress}
import java.nio.channels.{ SelectionKey, Selector, ServerSocketChannel}
import java.util.concurrent.atomic.AtomicInteger
import ServiceConfig._

/**
 * Created by yangguo on 15/9/10.
 */
object ServiceMonitor_1{
  def registerChannelEvent(selector:Selector,channel:java.nio.channels.SocketChannel,ops:Int): Unit ={
    if(channel!=null){
      channel.setOption(StandardSocketOptions.SO_KEEPALIVE,Boolean.box(true))
      channel.configureBlocking(false)
      channel.register(selector,ops)
    }
  }
  def registerChannelEvent(key:SelectionKey,eventOpts:Int)={
    key.selector().wakeup()
    key.interestOps(key.interestOps()| eventOpts)


  }
  def readDataFromChannel(key:SelectionKey)={
    def readJob()= {
      val socketChannel = key.channel().asInstanceOf[java.nio.channels.SocketChannel]
      try {
        val buffer = Util.boundedReadDataFromChannel(socketChannel)
        registerChannelEvent(key,SelectionKey.OP_READ)
        val bytes = buffer.array()
        buffer.clear()
        Util.asycRun(Util.boundedWriteDataToChannel(new String(bytes), socketChannel))
      }catch{
        case ex:Throwable=> {

          println("->>>>"+ex.getMessage())
          Util.unSafeClose(socketChannel)
          key.cancel()
        }
      }
    }
    Util.asycRun(readJob)
  }
  def apply()={
    val serverSocketChannel=ServerSocketChannel.open()
    val serverSocket=serverSocketChannel.socket()
    serverSocket.bind(new InetSocketAddress(Util.backend_service_port_value.toInt))
    serverSocketChannel.configureBlocking(false)
    val selector=Selector.open()
    serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT)

    val atomicInteger=new AtomicInteger(0)

    while(true){
      val n=selector.select(1000)
      if(n!=0){
        println("event "+n)
        val it=selector.selectedKeys().iterator()
        while(it.hasNext){
          val key=it.next()
          it.remove()
          if(key.isValid) {
            if (key.isAcceptable) {
              val server = key.channel().asInstanceOf[ServerSocketChannel]
              val channel = server.accept()
              registerChannelEvent(selector, channel, SelectionKey.OP_READ)
            }
            if (key.isReadable) {
              key.interestOps(key.interestOps() & ~SelectionKey.OP_READ)
              println(s"%----->>>${atomicInteger.getAndIncrement()}%")
              readDataFromChannel(key)
            }
          }
        }
      }

    }
  }
}
