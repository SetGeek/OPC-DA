package cn.com.sgcc.gdt.opc.core.dcom.common.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.EventHandler;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIFrameworkHelper;

/**
 * 事件处理器
 * @author ck.yang
 */
public class EventHandlerImpl implements EventHandler {
    private String identifier = null;

    private IJIComObject object = null;

    /**
     * 获取主键
     * @return
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public synchronized IJIComObject getObject() {
        return this.object;
    }

    /**
     * 设置信息
     * @param object
     * @param identifier
     */
    public synchronized void setInfo(final IJIComObject object, final String identifier) {
        this.object = object;
        this.identifier = identifier;
    }

    /**
     * 分离
     * @throws JIException
     */
    @Override
    public synchronized void detach() throws JIException {
        if (this.object != null && this.identifier != null) {
            try {
                JIFrameworkHelper.detachEventHandler(this.object, this.identifier);
            } finally {
                this.object = null;
                this.identifier = null;
            }
        }
    }

}
