package cn.com.sgcc.gdt.opc.client.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据项
 * @author ck.yang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataItem implements Serializable {
    /** 数据主键 */
    private String itemId;
    /** 数据类型 */
    private String dataType;
    /** 数据值 */
    private Object value;
    /** 数据质量 */
    private Short quality;
    /** 数据时间 */
    private Date dataTime;
    /** 最近时刻 */
    private Date currMonment;
}
