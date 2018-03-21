package cn.com.sgcc.gdt.opc.core.dcom.common;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;

/**
 * 时间处理器
 * @author ck.yang
 */
public interface EventHandler {
    public String getIdentifier();

    public IJIComObject getObject();

    public void detach() throws JIException;
}
