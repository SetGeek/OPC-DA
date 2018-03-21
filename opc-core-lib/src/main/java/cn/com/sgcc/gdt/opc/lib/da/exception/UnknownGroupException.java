package cn.com.sgcc.gdt.opc.lib.da.exception;

import lombok.Data;
import lombok.ToString;

/**
 * 未知的Group异常
 * @author: ck.yang
 */
@Data
@ToString
public class UnknownGroupException extends Exception {
    private String name = null;

    public UnknownGroupException(final String name) {
        super();
        this.name = name;
    }
}
