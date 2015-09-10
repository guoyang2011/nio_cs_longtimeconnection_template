package cn.changhong.pool

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by yangguo on 15/9/9.
 */
object ClientRunning{
  import ClientConfig._
  val backendServices = new BackendRequestHandler()
  val atomicInt = new AtomicInteger(0)
  def fn = {
    (1 to 10).foreach { index =>
      val requestContent=s"${Thread.currentThread().getName}:${atomicInt.getAndIncrement()}"
      val responseContent=( backendServices.request(requestContent))
      if(!requestContent.equals(responseContent)){
        try{
          throw new RuntimeException(s"[requestContent:${requestContent}!=responseContent:${responseContent}}] not equal")
        }catch{
          case ex:Throwable=>ex.printStackTrace()
        }
      }else{
        println(responseContent+":"+requestContent)
      }
    }
  }
  def apply()={
    (1 to 10).foreach(_ => Util.asycRun(fn))
  }
}
object ExampleStart extends App {
  def serverRunning = {
    Util.asycDomainRun(ServiceMonitor_1.apply)
  }
  //start server
  serverRunning
  Thread.sleep(1000)
  //start client
  ClientRunning()
  Thread.sleep(1000000)
}
