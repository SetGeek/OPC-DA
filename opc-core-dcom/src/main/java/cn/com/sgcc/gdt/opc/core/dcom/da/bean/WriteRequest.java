package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jinterop.dcom.core.JIVariant;

/**
 * 写入请求
 * @author ck.yang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class WriteRequest {

    private int serverHandle = 0;

    private JIVariant value = JIVariant.EMPTY();

}
