package controllers;

import akka.actor.ActorSystem;
import com.example.HelloWorldServer;
import com.example.HelloWorldServerImplService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;
import play.mvc.*;

import scala.concurrent.ExecutionContext;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    WSClient ws;


    ActorSystem actorSystem;

    final ExecutionContext ctx;

    Logger LOG = LoggerFactory.getLogger(HomeController.class);

    @Inject
    public HomeController(ActorSystem actorSystem,WSClient ws) {
        this.actorSystem = actorSystem;
        this.ws = ws;
        ctx = actorSystem.dispatchers().lookup("contexts.db-operations");
    }




    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok("Okay");
    }


    public CompletionStage<Result> ws() {
        return ws.url("http://localhost:8081").get().thenApply(response ->
                ok(response.getBody())
        );
    }

    public Result blocking() {
        HelloWorldServerImplService service =  new HelloWorldServerImplService();
        HelloWorldServer server = service.getHelloWorldServerImplPort();
        String message = server.sayHello("John");
        return ok(message);
    }

    public CompletionStage<Result> future() {
        HelloWorldServerImplService service =  new HelloWorldServerImplService();
        HelloWorldServer server = service.getHelloWorldServerImplPort();
        CompletionStage<String> promiseOfResult = CompletableFuture.supplyAsync(()->server.sayHello("John"),(Executor) ctx);
        CompletionStage<Result>  result = promiseOfResult.thenApply(res->ok(res));
        return result;
    }

    public CompletionStage<Result> async() {
        HelloWorldServerImplService service =  new HelloWorldServerImplService();
        HelloWorldServer server = service.getHelloWorldServerImplPort();
        CompletableFuture<String> future = new CompletableFuture<>();
        server.sayHelloAsync("John",res-> {
            try{
                future.complete(res.get());
            }catch(Exception ex) {
              LOG.error("Exception {}",ex);
            }

        });
        return future.thenApply(res-> {
            return ok(res);
        });
    }
}
