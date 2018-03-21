package cn.com.sgcc.gdt.opc.client.bean;

import cn.com.sgcc.gdt.opc.core.dcom.list.ClassDetails;
import lombok.*;

import java.io.Serializable;

/**
 * 服务器信息
 *
 * @author ck.yang
 */
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ServerInfo extends ClassDetails implements Serializable {
    private String progId;
    private String clsId;
    private String description;
}
