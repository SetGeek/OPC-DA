package cn.com.sgcc.gdt.opc.lib.da.exception;

import lombok.Data;
import lombok.ToString;

/**
 * 重复的Group异常
 * @author: ck.yang
 */
@Data
@ToString
public class DuplicateGroupException extends Exception {

    private String name = null;

    public DuplicateGroupException(final String name) {
        super();
        this.name = name;
    }
}
