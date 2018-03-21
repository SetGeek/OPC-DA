package cn.com.sgcc.gdt.opc.core.dcom.common.bean;

import java.util.ArrayList;

/**
 * 带主键的结果集
 * @author ck.yang
 */
public class KeyedResultSet<K, V> extends ArrayList<KeyedResult<K, V>> {
    private static final long serialVersionUID = 1L;

    public KeyedResultSet() {
        super();
    }

    public KeyedResultSet(final int size) {
        super(size); // me
    }
}
