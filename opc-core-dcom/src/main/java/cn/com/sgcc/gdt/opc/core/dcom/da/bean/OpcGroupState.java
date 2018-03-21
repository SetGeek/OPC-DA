package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * OPC组状态
 * @author ck.yang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpcGroupState {
    /** 更新频率 */
    private int updateRate = 1000;
    /** 激活状态 */
    private boolean active = true;
    /** 名称 */
    private String name = "";
    /** 时间偏差 */
    private int timeBias = 0;
    /** 死亡百分比 FIXME 不理解 */
    private float percentDeadband = 0.0f;
    /** 本地编号 */
    private int localeID = 0;
    /** 客户端处理器编号 */
    private int clientHandle = 0;
    /** 服务器处理编号 */
    private int serverHandle = 0;


}
