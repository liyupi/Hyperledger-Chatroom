package com.dhu.fabric.chatroom.sdk;

import com.dhu.fabric.chatroom.bean.Chaincode;
import com.dhu.fabric.chatroom.bean.Orderers;
import com.dhu.fabric.chatroom.bean.Peers;
import lombok.Data;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Objects;

/**
 * 链码配置类
 *
 * @author LiYupi
 */
@Data
public class FabricConfig {

    private static Logger log = Logger.getLogger(FabricConfig.class);

    /**
     * 节点服务器对象
     */
    private Peers peers;
    /**
     * 排序服务器对象
     */
    private Orderers orderers;
    /**
     * 智能合约对象
     */
    private Chaincode chaincode;
    /**
     * channel-artifacts所在路径：默认channel-artifacts所在路径/xxx/WEB-INF/classes/fabric/channel-artifacts/
     */
    private String channelArtifactsPath;
    /**
     * crypto-config所在路径：默认crypto-config所在路径/xxx/WEB-INF/classes/fabric/crypto-config/
     */
    private String cryptoConfigPath;
    private boolean registerEvent = false;

    public FabricConfig() {
        // 默认channel-artifacts所在路径 /xxx/WEB-INF/classes/fabric/channel-artifacts/
        channelArtifactsPath = getChannlePath() + "/fabric/channel-artifacts/";
        // 默认crypto-config所在路径 /xxx/WEB-INF/classes/fabric/crypto-config/
        cryptoConfigPath = getChannlePath() + "/fabric/crypto-config/";
    }

    /**
     * 默认fabric配置路径
     *
     * @return D:/installSoft/apache-tomcat-9.0.0.M21-02/webapps/xxx/WEB-INF/classes/fabric/channel-artifacts/
     */
    private String getChannlePath() {
        String dirs = Objects.requireNonNull(ChaincodeManager.class.getClassLoader().getResource("fabric")).getFile();
        log.debug("directorys = " + dirs);
        File directory = new File(dirs);
        log.debug("directory = " + directory.getPath());
        return directory.getPath();
    }

}