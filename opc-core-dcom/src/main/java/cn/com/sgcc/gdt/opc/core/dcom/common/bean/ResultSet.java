package cn.com.sgcc.gdt.opc.core.dcom.common.bean;

import java.util.ArrayList;

/**
 * 结果集合
 * @author ck.yang
 */
public class ResultSet<T> extends ArrayList<Result<T>> {

    private static final long serialVersionUID = 6392417310208978252L;

    public ResultSet() {
        super();
    }

    public ResultSet(final int size) {
        super(size); // me
    }
}
