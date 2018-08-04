import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import javafx.util.Pair;
import org.hyperledger.fabric.sdk.aberic.ChaincodeManager;
import org.hyperledger.fabric.sdk.aberic.utils.FabricManager;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainVerticle extends AbstractVerticle {
    // <nickname, socketId>, webSocket
    Map<Pair<String, String>, ServerWebSocket> userMap = new HashMap<>();
    ChaincodeManager chaincodeManager;

    {
        try {
            chaincodeManager = FabricManager.obtain().getManager();
        } catch (CryptoException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(MainVerticle.class.getName());
    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        setRouterList(router);
        HttpServer httpServer = vertx.createHttpServer();
        webSocketMethod(httpServer);
        httpServer.requestHandler(router::accept).listen(5927, res -> {
            System.out.println("server listening on port 5927");
        });
    }

    private void setRouterList(Router router) {
        router.route("/checkName").handler(routingContext -> {
            String nickname = routingContext.request().getParam("nickname");
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            if (hasSameName(nickname)) {
                response.end("0");
            } else {
                response.end("1");
            }
        });
        router.route("/getUsers").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            List<String> userList = new LinkedList<>();
            for (Pair<String, String> pair : userMap.keySet()) {
                userList.add(pair.getKey());
            }
            response.end(new JsonArray(userList).toString());
        });
    }

    private boolean hasSameName(String nickname) {
        for (Pair<String, String> pair : userMap.keySet()) {
            if (pair.getKey().equals(nickname)) {
                return false;
            }
        }
        return true;
    }

    private void webSocketMethod(HttpServer httpServer) {
        httpServer.websocketHandler(webSocket -> {
            String nickname = webSocket.path().substring(1);
            Pair<String, String> pair = new Pair<>(nickname, webSocket.binaryHandlerID());
            userMap.put(pair, webSocket);
            System.out.println(nickname + " connect success");
            String comeInMessage = "sm" + nickname + "进入房间";
            broadcast(nickname, comeInMessage);
            webSocket.frameHandler(frame -> {
                String userMessage = "um" + nickname + " : " + frame.textData();
                broadcast(nickname, userMessage);
            });
            webSocket.closeHandler(res -> {
                System.out.println(nickname + " close connection");
                String quitMessage = "sm" + nickname + "离开房间";
                userMap.remove(pair);
                broadcast(nickname, quitMessage);
            });
        });
    }

    private void broadcast(String nickname, String message) {
        for (Map.Entry<Pair<String, String>, ServerWebSocket> entry : userMap.entrySet()) {
            if (entry.getKey().getKey().equals(nickname)) {
                continue;
            }
            entry.getValue().writeTextMessage(message);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
