//package cn.changhong.redis
//
//import backtype.storm.tuple.{Values, Fields}
//import cn.changhong.storm.wordcount.Util.RedisPoolManager
//import redis.clients.jedis.HostAndPort
//import storm.trident.TridentTopology
//import storm.trident.operation.builtin.Count
//import storm.trident.operation.{TridentCollector, BaseFunction}
//import storm.trident.testing.MemoryMapState.Factory
//import storm.trident.testing.{MemoryMapState, Split, FixedBatchSpout}
//import storm.trident.tuple.TridentTuple
//
///**
// *
// * Created by yangguo on 15-8-20.
// */
//class split extends BaseFunction{
//  override def execute(tuple: TridentTuple, collector: TridentCollector): Unit = {
//    tuple.getString(0).split(" ").foreach(word=>collector.emit(new Values(word)))
//  }
//}
//object TridentTopologyTest{
//  def apply()={
//    val batchSpolt=new FixedBatchSpout(new Fields("sentence"),3,new Values("hasd "))
//    val topology=new TridentTopology
//    val wordcounts=topology
//      .newStream("spout1",batchSpolt)
//      .each(new Fields("sentence"),new Split,new Fields("word"))
//      .groupBy(new Fields("word"))
//      .persistentAggregate(new Factory(),new Count(),new Fields("count"))
//      .parallelismHint(6)
//  }
//}
//
//object Start {
//
//
//  def main(args: Array[String]) {
//    val hosts="10.9.52.11:6379,10.9.52.12:6379,10.9.52.12:6379,10.9.52.11:7379,10.9.52.12:7379,10.9.52.12:7379".split(",").map{hostAndport=>
//      val hp=hostAndport.split(":")
//      new HostAndPort(hp(0),hp(1).toInt)
//    }.toList
//    val redisPool=RedisPoolManager.clusterApply(hosts)
//    println(redisPool.zincrby("word_count_set",1,"spark"))
//    val range=redisPool.zrangeWithScores("word_count_set",0,-1)
//    val it=range.iterator()
//    while(it.hasNext){
//      val element=it.next()
//      println(element.getElement+","+element.getScore)
//    }
////    println(redisPool.zrangeWithScores("word_count_set",0,-1))
//  }
//}
