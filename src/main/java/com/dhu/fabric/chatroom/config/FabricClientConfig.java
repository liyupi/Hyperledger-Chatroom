package com.dhu.fabric.chatroom.config;

/**
 * Fabric客户端配置
 *
 * @author Yupi Li
 */
public interface FabricClientConfig {
    String CHANNEL_NAME = "mychannel";
    String CHAINCODE_NAME = "mycc";
    String CHAINCODE_PATH = "github.com/hyperledger/fabric/aberic/chaincode/go/chatroom";
    String CHAINCODE_VERSION = "1.0";

    String ORDERER_DOMAIN_NAME = "example.com";

}
