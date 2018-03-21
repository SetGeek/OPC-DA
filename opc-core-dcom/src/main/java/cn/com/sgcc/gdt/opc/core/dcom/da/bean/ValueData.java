package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jinterop.dcom.core.JIVariant;

/**
 * 数据值
 * @author ck.yang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValueData {
    /** 值 */
    private JIVariant value;
    /** 质量 */
    private short quality;
    /** 时间戳 */
    private Calendar timestamp;

}
