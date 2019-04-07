package com.dhu.fabric.chatroom.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric创建的peer信息，包含有cli、org、ca、couchdb等节点服务器关联启动服务信息集合
 *
 * @author LiYupi
 */
@Data
public class Peers {

    /**
     * 当前指定的组织名称
     */
    private String orgName; // Org1
    /**
     * 当前指定的组织名称
     */
    private String orgMSPID; // Org1MSP
    /**
     * 当前指定的组织所在根域名
     */
    private String orgDomainName; //org1.example.com
    /**
     * orderer 排序服务器集合
     */
    private List<Peer> peers;

    public Peers() {
        peers = new ArrayList<>();
    }

    /**
     * 新增排序服务器
     */
    public void addPeer(String peerName, String peerEventHubName, String peerLocation, String peerEventHubLocation, String caLocation) {
        peers.add(new Peer(peerName, peerEventHubName, peerLocation, peerEventHubLocation, caLocation));
    }

    /**
     * 获取排序服务器集合
     */
    public List<Peer> get() {
        return peers;
    }

    /**
     * 节点服务器对象
     */
    @Data
    public class Peer {

        /**
         * 当前指定的组织节点域名
         */
        private String peerName; // peer0.org1.example.com
        /**
         * 当前指定的组织节点事件域名
         */
        private String peerEventHubName; // peer0.org1.example.com
        /**
         * 当前指定的组织节点访问地址
         */
        private String peerLocation; // grpc://x.x.x.x:7051
        /**
         * 当前指定的组织节点事件监听访问地址
         */
        private String peerEventHubLocation; // grpc://x.x.x.x:7053
        /**
         * 当前指定的组织节点ca访问地址
         */
        private String caLocation; // http://x.x.x.x:7054

        /**
         * 当前peer是否增加Event事件处理
         */
        private boolean addEventHub = false;

        public Peer(String peerName, String peerEventHubName, String peerLocation, String peerEventHubLocation, String caLocation) {
            this.peerName = peerName;
            this.peerEventHubName = peerEventHubName;
            this.peerLocation = peerLocation;
            this.peerEventHubLocation = peerEventHubLocation;
            this.caLocation = caLocation;
        }

    }

}