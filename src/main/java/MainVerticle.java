import com.dhu.fabric.chatroom.sdk.ChaincodeManager;
import com.dhu.fabric.chatroom.utils.FabricClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import javafx.util.Pair;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Vertx后端和WebSocket服务
 *
 * @author LiYupi
 */
public class MainVerticle extends AbstractVerticle {
    /**
     * 用户存储，<用户名, socketId>
     */
    private Map<Pair<String, String>, ServerWebSocket> userMap = new HashMap<>();

    private ChaincodeManager chaincodeManager;

    {
        try {
            chaincodeManager = FabricClient.obtain().getManager();
            System.out.println("chaincodeManager start successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 启动类
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(MainVerticle.class.getName());
    }

    @Override
    public void start() throws Exception {
        // 设定后端服务，启动时被调用
        Router router = Router.router(vertx);
        setRouterList(router);
        HttpServer httpServer = vertx.createHttpServer();
        webSocketMethod(httpServer);
        httpServer.requestHandler(router::accept).listen(5927, res -> {
            System.out.println("server listening on port 5927");
        });
    }

    // 接口
    private void setRouterList(Router router) {
        // 同名检测
        router.route("/checkName").handler(routingContext -> {
            String nickname = routingContext.request().getParam("nickname");
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            // 如果有同名，返回0
            response.end(hasSameName(nickname) ? "0" : "1");
        });
        // 用户列表
        router.route("/getUsers").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            List<String> userList = new LinkedList<>();
            // 从用户集中取出
            for (Pair<String, String> pair : userMap.keySet()) {
                userList.add(pair.getKey());
            }
            // 返回用户列表Json数组
            response.end(new JsonArray(userList).toString());
        });
        // 获取聊天记录
        router.route("/getAllMessage").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            Map<String, String> messageMap = null;
            try {
                // 查询区块链上的聊天记录
                messageMap = chaincodeManager.query("getAllMessage", new String[]{"2018-08-05 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.end(JsonObject.mapFrom(messageMap).toString());
        });
    }

    // 同名检测
    private boolean hasSameName(String nickname) {
        for (Pair<String, String> pair : userMap.keySet()) {
            if (pair.getKey().equals(nickname)) {
                return false;
            }
        }
        return true;
    }

    // 启动webSocket服务
    private void webSocketMethod(HttpServer httpServer) {
        httpServer.websocketHandler(webSocket -> {
            String nickname = webSocket.path().substring(1);
            Pair<String, String> pair = new Pair<>(nickname, webSocket.binaryHandlerID());
            userMap.put(pair, webSocket);
            System.out.println(nickname + " connect success");
            String comeInMessage = "sm" + nickname + "进入房间";
            broadcast(nickname, comeInMessage);
            // 监听消息
            webSocket.frameHandler(frame -> {
                String userMessage = "um" + nickname + " : " + frame.textData();
                try {
                    // 存储用户消息到区块链上
                    chaincodeManager.invoke("saveMessage",
                            new String[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), userMessage});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 广播消息
                broadcast(nickname, userMessage);
            }).closeHandler(res -> {
                // socket连接断开，移除用户
                System.out.println(nickname + " close connection");
                String quitMessage = "sm" + nickname + "离开房间";
                userMap.remove(pair);
                broadcast(nickname, quitMessage);
            });
        });
    }

    // 广播消息
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
