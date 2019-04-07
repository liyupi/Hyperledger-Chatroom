package com.dhu.fabric.chatroom.utils;

import com.dhu.fabric.chatroom.bean.Chaincode;
import com.dhu.fabric.chatroom.bean.Orderers;
import com.dhu.fabric.chatroom.bean.Peers;
import com.dhu.fabric.chatroom.sdk.ChaincodeManager;
import com.dhu.fabric.chatroom.sdk.FabricConfig;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import static com.dhu.fabric.chatroom.config.FabricClientConfig.*;

/**
 * Fabric客户端
 * 提供操作区块链的功能
 */
public class FabricClient {

    private static Logger log = Logger.getLogger(FabricClient.class);

    private ChaincodeManager manager;

    private static FabricClient instance = null;

    public static FabricClient obtain()
            throws CryptoException, InvalidArgumentException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TransactionException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (null == instance) {
            synchronized (FabricClient.class) {
                if (null == instance) {
                    instance = new FabricClient();
                }
            }
        }
        return instance;
    }

    private FabricClient()
            throws CryptoException, InvalidArgumentException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TransactionException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        manager = new ChaincodeManager(getConfig());
    }

    /**
     * 获取节点服务器管理器
     *
     * @return 节点服务器管理器
     */
    public ChaincodeManager getManager() {
        return manager;
    }

    /**
     * 根据节点作用类型获取节点服务器配置
     *
     * @return 节点服务器配置
     */
    private FabricConfig getConfig() {
        FabricConfig config = new FabricConfig();
        config.setOrderers(getOrderers());
        config.setPeers(getPeers());
        config.setChaincode(getChaincode(CHANNEL_NAME, CHAINCODE_NAME, CHAINCODE_PATH, CHAINCODE_VERSION));
        config.setChannelArtifactsPath(getChannleArtifactsPath());
        config.setCryptoConfigPath(getCryptoConfigPath());
        return config;
    }

    private Orderers getOrderers() {
        Orderers orderer = new Orderers();
        orderer.setOrdererDomainName(ORDERER_DOMAIN_NAME);
        orderer.addOrderer("orderer.example.com", "grpc://localhost:7050");
        return orderer;
    }

    /**
     * 获取节点服务器集
     *
     * @return 节点服务器集
     */
    private Peers getPeers() {
        Peers peers = new Peers();
        peers.setOrgName("Org1");
        peers.setOrgMSPID("Org1MSP");
        peers.setOrgDomainName("org1.example.com");
        peers.addPeer("peer0.org1.example.com", "peer0.org1.example.com", "grpc://localhost:7051", "grpc://localhost:7053", "http://localhost:7054");
        return peers;
    }

    /**
     * 获取智能合约
     *
     * @param channelName      频道名称
     * @param chaincodeName    智能合约名称
     * @param chaincodePath    智能合约路径
     * @param chaincodeVersion 智能合约版本
     * @return 智能合约
     */
    private Chaincode getChaincode(String channelName, String chaincodeName, String chaincodePath, String chaincodeVersion) {
        Chaincode chaincode = new Chaincode();
        chaincode.setChannelName(channelName);
        chaincode.setChaincodeName(chaincodeName);
        chaincode.setChaincodePath(chaincodePath);
        chaincode.setChaincodeVersion(chaincodeVersion);
        chaincode.setInvokeWaitTime(100000);
        chaincode.setDeployWaitTime(120000);
        return chaincode;
    }

    /**
     * 获取channel-artifacts配置路径
     *
     * @return /WEB-INF/classes/fabric/channel-artifacts/
     */
    private String getChannleArtifactsPath() {
        String directorys = Objects.requireNonNull(FabricClient.class.getClassLoader().getResource("fabric")).getFile();
        System.out.println(directorys);

        log.debug("directorys = " + directorys);
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());
        return directory.getPath() + "/channel-artifacts/";
    }

    /**
     * 获取crypto-config配置路径
     *
     * @return /WEB-INF/classes/fabric/crypto-config/
     */
    private String getCryptoConfigPath() {
        String directorys = Objects.requireNonNull(FabricClient.class.getClassLoader().getResource("fabric")).getFile();
        System.out.println(directorys);

        log.debug("directorys = " + directorys);
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());
        return directory.getPath() + "/crypto-config/";
    }
}
