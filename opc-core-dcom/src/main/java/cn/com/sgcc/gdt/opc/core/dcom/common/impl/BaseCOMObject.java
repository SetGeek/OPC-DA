package cn.com.sgcc.gdt.opc.core.dcom.common.impl;

import org.jinterop.dcom.core.IJIComObject;

/**
 * 基本的COM对象
 * @author ck.yang
 */
public class BaseCOMObject {
    private IJIComObject comObject = null;

    /**
     * Create a new base COM object
     *
     * @param comObject The COM object to wrap but be addRef'ed
     */
    public BaseCOMObject(final IJIComObject comObject) {
        this.comObject = comObject;
    }

    protected synchronized IJIComObject getCOMObject() {
        return this.comObject;
    }
}
