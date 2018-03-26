package cn.com.sgcc.gdt.opc.lib.da;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jinterop.dcom.core.JIVariant;

/**
 * 写入请求
 * @author: ck.yang
 */
@Getter
@AllArgsConstructor
public class WriteRequest {
    /** 数据项 */
    private Item item = null;
    /** 值 */
    private JIVariant value = null;

}
