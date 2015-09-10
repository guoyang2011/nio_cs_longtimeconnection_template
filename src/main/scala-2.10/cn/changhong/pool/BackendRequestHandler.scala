package cn.changhong.pool

import org.apache.commons.pool.impl.GenericObjectPool

/**
 * Created by yangguo on 15/9/10.
 */
class BackendRequestHandler(conf:Map[String,String]){
  def this()=this(Map(
    Util.backend_service_host->Util.backend_service_host_value,
    Util.backend_service_port->Util.backend_service_port_value))

  val _pool=new GenericObjectPool(new BackendChannelPoolableFactory(conf))
  _pool.setMaxActive(10)
  def request(msg:String)={
    val channel=_pool.borrowObject().asInstanceOf[BackendChannel]
    try{
      channel.send(msg)
    }finally {
      _pool.returnObject(channel)
    }
  }
}

