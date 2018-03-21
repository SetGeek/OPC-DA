package cn.com.sgcc.gdt.opc.lib.da.exception;

import cn.com.sgcc.gdt.opc.lib.da.Item;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加失败异常
 * @author: ck.yang
 */
@Data
@ToString
public class AddFailedException extends Exception {

    /** 错误信息 */
    private Map<String, Integer> error = new HashMap<>();

    /** 失败的数据项 */
    private Map<String, Item> items = new HashMap<>();

    public AddFailedException(final Map<String, Integer> errors, final Map<String, Item> items) {
        super();
        this.error = errors;
        this.items = items;
    }
}
