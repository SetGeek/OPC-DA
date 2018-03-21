package cn.com.sgcc.gdt.opc.lib.da;

/**
 * 自动重连的状态
 * @author ck.yang
 */
public enum AutoReconnectState {
    /**
     * 自动重连不可用
     */
    DISABLED,
    /**
     * 自动重连已激活，但是当前未建立连接
     */
    DISCONNECTED,
    /**
     * 自动重连已激活，当前未建立连接，重连控制器在等待一段延时后将会重连
     */
    WAITING,
    /**
     * 自动重连已激活，当前为建立连接，但是重连控制器正在尝试连接
     */
    CONNECTING,
    /**
     * 自动重连已激活，当前已建立建立
     */
    CONNECTED
}