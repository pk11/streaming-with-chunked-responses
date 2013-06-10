package controllers

import play.api._
import play.api.mvc._
import play.api.libs.{ Comet }
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import com.lambdaworks.redis.RedisClient
import com.lambdaworks.redis.pubsub.RedisPubSubListener
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import play.libs.Akka

object Application extends Controller {

  private val CONNECTION_TIMEOUT = (30 seconds)

  def chunked = Action { request =>
    
    val connection = new RedisClient("localhost").connectPubSub

    println("in chunked controller")

    val e: Enumerator[Array[Byte]] = Concurrent.unicast[Array[Byte]](
      onStart = (c) => {
        println("start...")
        println("onstart: " + connection)
        Akka.system.scheduler.scheduleOnce(CONNECTION_TIMEOUT)  {
          c.eofAndEnd()
        }
        connection.addListener(new RedisPubSubListener[String, String] {
          def message(channel: String, message: String): Unit = {
            if (channel == "stream") {
              c.push(message.getBytes)
            }
          }
          def message(x$1: String,x$2: String,x$3: String): Unit = {}
          def psubscribed(x$1: String,x$2: Long): Unit = {}
          def punsubscribed(x$1: String,x$2: Long): Unit = {}
          def subscribed(x$1: String,x$2: Long): Unit = {}
          def unsubscribed(x$1: String,x$2: Long): Unit = {}
        })

        connection.subscribe("stream")
        println("non-blocking")
      },

      onComplete = {
        println("onComplete:" + connection)
        connection.close()
      },

      onError = (e, i) => {
        println(e)
        println("onError:" + connection)
        connection.close()
      })

    request.queryString.get("iframe")
    .map(_ => Ok.stream(e.map(new String(_)) &>  Comet(callback = "parent.onchunks")))
    .getOrElse(Ok.stream(e))

  }

}
