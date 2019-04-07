package com.dhu.fabric.chatroom.bean;

import lombok.Data;

/**
 * 链码和所属channel等信息
 *
 * @author LiYupi
 */
@Data
public class Chaincode {

    /**
     * 要访问的链码所属频道名称
     */
    private String channelName;
    /**
     * 链码名称
     */
    private String chaincodeName;
    /**
     * 链码安装路径
     */
    private String chaincodePath;
    /**
     * 链码版本号
     */
    private String chaincodeVersion;
    /**
     * 执行链码操作等待时间
     */
    private int invokeWaitTime = 100000;
    /**
     * 部署链码实例等待时间
     */
    private int deployWaitTime = 120000;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getChaincodePath() {
        return chaincodePath;
    }

    public void setChaincodePath(String chaincodePath) {
        this.chaincodePath = chaincodePath;
    }

    public String getChaincodeVersion() {
        return chaincodeVersion;
    }

    public void setChaincodeVersion(String chaincodeVersion) {
        this.chaincodeVersion = chaincodeVersion;
    }

    public int getInvokeWaitTime() {
        return invokeWaitTime;
    }

    public void setInvokeWaitTime(int invokeWaitTime) {
        this.invokeWaitTime = invokeWaitTime;
    }

    public int getDeployWaitTime() {
        return deployWaitTime;
    }

    public void setDeployWaitTime(int deployWaitTime) {
        this.deployWaitTime = deployWaitTime;
    }

}