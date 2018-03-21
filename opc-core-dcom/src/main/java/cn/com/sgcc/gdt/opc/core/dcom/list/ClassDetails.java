package cn.com.sgcc.gdt.opc.core.dcom.list;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * OPC服务软件的信息
 * @author ck.yang
 */
@Data
@ToString
@EqualsAndHashCode
public class ClassDetails {
    /** 服务器软件clsId */
    private String clsId;
    /** 服务器软件progId */
    private String progId;
    /** 服务器软件描述 */
    private String description;

}
