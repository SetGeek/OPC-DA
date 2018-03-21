package cn.com.sgcc.gdt.opc.lib.da;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;

/**
 * 数据项
 * @author: ck.yang
 */
@Slf4j
@AllArgsConstructor
@Getter
public class Item {
    /** 组 */
    private Group group = null;
    /** 服务处理编号 */
    private int serverHandle = 0;
    /** 客户端处理编号 */
    private int clientHandle = 0;
    /** 项 编号 */
    private String id = null;

    /**
     * 切换激活状态
     * @param state
     * @throws JIException
     */
    public void setActive(final boolean state) throws JIException {
        this.group.setActive(state, this);
    }

    /**
     * 读取
     * @param device
     * @return
     * @throws JIException
     */
    public ItemState read(final boolean device) throws JIException {
        return this.group.read(device, this).get(this);
    }

    /**
     * 写入
     * @param value
     * @return
     * @throws JIException
     */
    public Integer write(final JIVariant value) throws JIException {
        return this.group.write(new WriteRequest[]{new WriteRequest(this, value)}).get(this);
    }
}
