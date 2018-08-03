import com.fasterxml.jackson.databind.util.JSONPObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainVerticle extends AbstractVerticle
{
    // <nickname, socketId>, webSocket
    Map<Pair<String, String>, ServerWebSocket> userMap = new HashMap<>();

    public static void main(String[] args)
    {
        Vertx.vertx().deployVerticle(MainVerticle.class.getName());
    }

    @Override
    public void start() throws Exception
    {
        Router router = Router.router(vertx);
        setRouterList(router);
        HttpServer httpServer = vertx.createHttpServer();
        webSocketMethod(httpServer);
        httpServer.requestHandler(router::accept).listen(5927, res -> {
            System.out.println("server listening on port 5927");
        });
    }

    private void setRouterList(Router router)
    {
        router.route("/checkName").handler(routingContext -> {
            String nickname = routingContext.request().getParam("nickname");
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            if (hasSameName(nickname))
            {
                response.end("0");
            } else
            {
                response.end("1");
            }
        });
        router.route("/getUsers").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            List<String> userList = new LinkedList<>();
            for (Pair<String, String> pair : userMap.keySet())
            {
                userList.add(pair.getKey());
            }
            response.end(new JsonArray(userList).toString());
        });
    }

    private boolean hasSameName(String nickname)
    {
        for (Pair<String, String> pair : userMap.keySet())
        {
            if (pair.getKey().equals(nickname))
            {
                return false;
            }
        }
        return true;
    }

    private void webSocketMethod(HttpServer httpServer)
    {
        httpServer.websocketHandler(webSocket -> {
            String nickname = webSocket.path().substring(1);
            Pair<String, String> pair = new Pair<>(nickname, webSocket.binaryHandlerID());
            userMap.put(pair, webSocket);
            System.out.println(nickname + " connect success");
            String comeInMessage = nickname + "进入了房间";
            broadcast(nickname, comeInMessage);
            webSocket.frameHandler(frame -> {
                broadcast(nickname, frame.textData());
            });
            webSocket.closeHandler(res -> {
                System.out.println(nickname + " close connection");
                userMap.remove(pair);
            });
        });
    }

    private void broadcast(String nickname, String message)
    {
        for (Map.Entry<Pair<String, String>, ServerWebSocket> entry : userMap.entrySet())
        {
            if (entry.getKey().getKey().equals(nickname))
            {
                continue;
            }
            entry.getValue().writeTextMessage(nickname);
        }
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
    }
}
