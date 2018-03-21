package cn.com.sgcc.gdt.opc.lib.da;

import java.net.UnknownHostException;
import java.util.Map;

import cn.com.sgcc.gdt.opc.lib.common.NotConnectedException;
import cn.com.sgcc.gdt.opc.lib.da.exception.DuplicateGroupException;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步访问通道
 * @author: ck.yang
 */
@Slf4j
public class SyncAccess extends AccessBase implements Runnable {

    private Thread runner = null;

    private Throwable lastError = null;

    /**
     *
     * @param server 服务
     * @param period 刷新时间
     * @throws IllegalArgumentException 错误参数异常
     * @throws UnknownHostException 未知主机异常
     * @throws NotConnectedException 未连接服务异常
     * @throws JIException 连接异常
     * @throws DuplicateGroupException 重复组异常
     */
    public SyncAccess(final Server server, final int period) throws IllegalArgumentException, UnknownHostException, NotConnectedException, JIException, DuplicateGroupException {
        super(server, period);
    }

    /**
     *
     * @param server 服务
     * @param period 刷新时间
     * @param logTag 日志标签
     * @throws IllegalArgumentException 错误参数异常
     * @throws UnknownHostException 未知主机异常
     * @throws NotConnectedException 未连接服务异常
     * @throws JIException 连接异常
     * @throws DuplicateGroupException 重复组异常
     */
    public SyncAccess(final Server server, final int period, final String logTag) throws IllegalArgumentException, UnknownHostException, NotConnectedException, JIException, DuplicateGroupException {
        super(server, period, logTag);
    }

    @Override
    public void run() {
        while (this.active) {
            try {
                runOnce();
                if (this.lastError != null) {
                    this.lastError = null;
                    handleError(null);
                }
            } catch (Throwable e) {
                log.error("同步读取失败！", e);
                handleError(e);
                this.server.disconnect();
            }

            try {
                Thread.sleep(getPeriod());
            } catch (InterruptedException e) {
            }
        }
    }

    protected void runOnce() throws JIException {
        if (!this.active || this.group == null) {
            return;
        }

        Map<Item, ItemState> result;

        // lock only this section since we could get into a deadlock otherwise
        // calling updateItem
        synchronized (this) {
            Item[] items = this.items.keySet().toArray(new Item[this.items.size()]);
            result = this.group.read(false, items);
        }

        for (Map.Entry<Item, ItemState> entry : result.entrySet()) {
            updateItem(entry.getKey(), entry.getValue());
        }

    }

    @Override
    protected synchronized void start() throws JIException, IllegalArgumentException, UnknownHostException, NotConnectedException, DuplicateGroupException {
        super.start();

        this.runner = new Thread(this, "UtgardSyncReader");
        this.runner.setDaemon(true);
        this.runner.start();
    }

    @Override
    protected synchronized void stop() throws JIException {
        super.stop();

        this.runner = null;
        this.items.clear();
    }
}
