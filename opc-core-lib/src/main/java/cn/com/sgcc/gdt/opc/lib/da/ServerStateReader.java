package cn.com.sgcc.gdt.opc.lib.da;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStateReader {
    private static Logger _log = LoggerFactory.getLogger(ServerStateReader.class);

    private Server _server = null;

    private ScheduledExecutorService _scheduler = null;

    private final List<ServerStateListener> _listeners = new CopyOnWriteArrayList<ServerStateListener>();

    private ScheduledFuture<?> _job = null;

    public ServerStateReader(final Server server) {
        super();
        this._server = server;
        this._scheduler = this._server.getScheduler();
    }

    /**
     * Create a new server state reader. Please note that the scheduler might get
     * blocked for a short period of time in case of a connection failure!
     *
     * @param server    the server to check
     * @param scheduler the scheduler to use
     */
    public ServerStateReader(final Server server, final ScheduledExecutorService scheduler) {
        super();
        this._server = server;
        this._scheduler = scheduler;
    }

    public synchronized void start() {
        if (this._job != null) {
            return;
        }

        this._job = this._scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                once();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        this._job.cancel(false);
        this._job = null;
    }

    protected void once() {
        _log.debug("Reading server state");

        final OpcServerStatus state = this._server.getServerState();

        for (final ServerStateListener listener : new ArrayList<ServerStateListener>(this._listeners)) {
            listener.stateUpdate(state);
        }
    }

    public void addListener(final ServerStateListener listener) {
        this._listeners.add(listener);
    }

    public void removeListener(final ServerStateListener listener) {
        this._listeners.remove(listener);
    }
}
