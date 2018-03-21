package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 传输请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IORequest {
    /** 数据项编号 */
    private String itemID;
    /** 最大生存时间 */
    private int maxAge;

}