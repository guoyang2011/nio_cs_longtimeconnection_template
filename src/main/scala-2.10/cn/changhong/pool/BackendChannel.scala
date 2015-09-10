package cn.changhong.pool

/**
 * Created by yangguo on 15/9/10.
 */
@ThreadSafe
class BackendChannel(conf:Map[String,String]){
  private val lock=new Object
  private val _blockChannel=new BlockingChannel(conf)
  private var isActivator=false
  private var _id:String=""
  def setId(id:String)=_id=id
  def getId()=_id
  def initConnection=lock synchronized{
    _blockChannel.connect()
  }
  def isActive=isActivator
  def setIsActive(bool:Boolean)=isActivator=bool

  def getInfo:String={
    _blockChannel.getInfo
  }
  private def reconnect()={
    _blockChannel.disconnect()
    _blockChannel.connect()
  }

  private def connect():BlockingChannel={
    disconnect()
    _blockChannel.connect()
    _blockChannel
  }
  private def disconnect(): Unit ={
    _blockChannel.disconnect()
  }
  private def getOrMakeConnection(): Unit ={
    if(!_blockChannel.isConnected) connect()
  }
  def close()=lock synchronized {
    disconnect()
  }
  def send(request:String):String=lock synchronized{
    try{
      getOrMakeConnection()
      _blockChannel.sendRequest(request)
    }catch{
      case ex:Throwable=>
        try{
          reconnect()
          _blockChannel.sendRequest(request)
        }catch {
          case ex:Throwable=>
            disconnect()
            throw ex
        }
    }
  }
}

