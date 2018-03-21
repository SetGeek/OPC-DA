package cn.com.sgcc.gdt.opc.lib.da;

import cn.com.sgcc.gdt.opc.core.dcom.common.EventHandler;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResult;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.ResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.da.IOPCDataCallback;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcDatasource;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.ValueData;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCAsyncIO2;
import cn.com.sgcc.gdt.opc.lib.common.NotConnectedException;
import cn.com.sgcc.gdt.opc.lib.da.exception.DuplicateGroupException;
import org.jinterop.dcom.common.JIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * 异步访问通道
 * @author ck.yang
 */
public class Async20Access extends AccessBase implements IOPCDataCallback {
    private static Logger logger = LoggerFactory.getLogger(Async20Access.class);

    private EventHandler eventHandler = null;

    private boolean initialRefresh = false;

    public Async20Access(final Server server, final int period, final boolean initialRefresh) throws IllegalArgumentException, UnknownHostException, NotConnectedException, JIException, DuplicateGroupException {
        super(server, period);
        this.initialRefresh = initialRefresh;
    }

    public Async20Access(final Server server, final int period, final boolean initialRefresh, final String logTag) throws IllegalArgumentException, UnknownHostException, NotConnectedException, JIException, DuplicateGroupException {
        super(server, period, logTag);
        this.initialRefresh = initialRefresh;
    }

    @Override
    protected synchronized void start() throws JIException, IllegalArgumentException, UnknownHostException, NotConnectedException, DuplicateGroupException {
        if (isActive()) {
            return;
        }

        super.start();

        this.eventHandler = this.group.attach(this);
        if (!this.items.isEmpty() && this.initialRefresh) {
            final OPCAsyncIO2 async20 = this.group.getAsyncIO20();
            if (async20 == null) {
                throw new NotConnectedException();
            }

            this.group.getAsyncIO20().refresh(OpcDatasource.OPC_DS_CACHE, 0);
        }
    }

    @Override
    protected synchronized void stop() throws JIException {
        if (!isActive()) {
            return;
        }

        if (this.eventHandler != null) {
            try {
                this.eventHandler.detach();
            } catch (final Throwable e) {
                logger.warn("Failed to detach group", e);
            }

            this.eventHandler = null;
        }

        super.stop();
    }

    @Override
    public void cancelComplete(final int transactionId, final int serverGroupHandle) {
    }

    @Override
    public void dataChange(final int transactionId, final int serverGroupHandle, final int masterQuality, final int masterErrorCode, final KeyedResultSet<Integer, ValueData> result) {
        logger.debug("dataChange - transId {}, items: {}", transactionId, result.size());

        final Group group = this.group;
        if (group == null) {
            return;
        }

        for (final KeyedResult<Integer, ValueData> entry : result) {
            final Item item = group.findItemByClientHandle(entry.getKey());
            logger.debug("Update for '{}'", item.getId());
            updateItem(item, new ItemState(entry.getErrorCode(), entry.getValue().getValue(), entry.getValue().getTimestamp(), entry.getValue().getQuality()));
        }
    }

    @Override
    public void readComplete(final int transactionId, final int serverGroupHandle, final int masterQuality, final int masterErrorCode, final KeyedResultSet<Integer, ValueData> result) {
        logger.debug("readComplete - transId {}", transactionId);
    }

    @Override
    public void writeComplete(final int transactionId, final int serverGroupHandle, final int masterErrorCode, final ResultSet<Integer> result) {
        logger.debug("writeComplete - transId {}", transactionId);
    }
}
