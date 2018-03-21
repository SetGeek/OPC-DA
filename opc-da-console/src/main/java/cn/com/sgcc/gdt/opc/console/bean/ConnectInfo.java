package cn.com.sgcc.gdt.opc.console.bean;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 连接服务器的信息
 *
 * @author ck.yang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConnectInfo extends ConnectionInformation {
    private String id;
    /**服务器IP地址:默认为 localhost*/
    private String host = "localhost";
    /** 服务器主机所在域：默认为localhost*/
    private String domain = "localhost";
    /** 服务器主机密码：默认为 Administrator */
    private String user = "Administrator";
    /** 服务器的密码：默认为 null */
    private String password;
    /** 服务器软件唯一标识：默认为null */
    private String clsId;
    /** 服务器软件id：默认为null */
    private String progId;
    /** 数据刷新超时时间：默认为1000L ms */
    private Long timeout = 1000L;
    /** 数据刷新时间：默认为1000L ms */
    private Long heartbeat = 1000L;

}
