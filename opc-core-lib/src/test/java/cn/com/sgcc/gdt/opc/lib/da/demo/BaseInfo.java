package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;

/**
 * TODO
 *
 * @author ck.yang
 */
public class BaseInfo {

    /**
     * 连接信息
     * @return
     */
    public static ConnectionInformation getConnInfo(){
        ConnectionInformation connInfo = new ConnectionInformation();
        connInfo.setDomain("192.168.2.254");
        connInfo.setHost("192.168.2.254");
        connInfo.setClsId("7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729");//kepware
//        connInfo.setClsId("B57C679B-665D-4BB0-9848-C5F2C4A6A280");//ksDemo
        connInfo.setUser("Administrator");
        connInfo.setPassword("GDTvm6.5");
        return connInfo;
    }
}
