import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Adapter
import akka.actor.typed.javadsl.Behaviors
import akka.grpc.javadsl.WebHandler
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.stream.SystemMaterializer
import akka.stream.javadsl.Source
import com.example.*
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class GreeterServiceImpl(system: ActorSystem<Void>): Greeter {
    override fun sayHello(request: Helloworld.HelloRequest): CompletionStage<Helloworld.HelloReply> {
        return CompletableFuture.completedFuture(Helloworld.HelloReply.newBuilder().setMessage("Hello ${request.name}").build())
    }

    override fun sayRepeatHello(`in`: Helloworld.RepeatHelloRequest?): Source<Helloworld.HelloReply, NotUsed> {
        TODO("Not yet implemented")
    }
}

fun main() {
    val logger = LoggerFactory.getLogger("main")
    val system = ActorSystem.create<Void>(Behaviors.empty(), "root")
    val service = GreeterHandlerFactory.create(GreeterServiceImpl(system), system)
    val handlers = WebHandler.grpcWebHandler(listOf(service), system, SystemMaterializer.get(system).materializer())

    val bound = Http.get(Adapter.toClassic(system)).bindAndHandleAsync(
        service,
        ConnectHttp.toHost("localhost", 8000),
        SystemMaterializer.get(system).materializer())
    bound.thenAccept { binding ->
        logger.info("GRPC Server bound to ${binding.localAddress()}")
    }

    val web = Http.get(Adapter.toClassic(system)).bindAndHandleAsync(
        handlers,
        ConnectHttp.toHost("localhost", 8001),
        SystemMaterializer.get(system).materializer())
    web.thenAccept { binding ->
        logger.info("GRPC-WEB Server bound to ${binding.localAddress()}")
    }
}
