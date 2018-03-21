package cn.com.sgcc.gdt.opc.core.dcom.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结果
 * @author ck.yang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    /** 值 */
    private T value;
    /** 错误码 */
    private int errorCode;
    /** 是否失败 */
    public boolean isFailed() {
        return this.errorCode != 0;
    }
}
