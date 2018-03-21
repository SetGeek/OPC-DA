package cn.com.sgcc.gdt.opc.lib.da;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 自动重连控制器
 * @author ck.yang
 */
@Slf4j
public class AutoReconnectController implements ServerConnectionStateListener {
    /** 默认重连的间隔时间：5000 ms */
    private static final int DEFAULT_DELAY = 5 * 1000;
    /** 重连延时 */
    private int delay;

    private final Server server;

    private final Set<AutoReconnectListener> listeners = new CopyOnWriteArraySet<AutoReconnectListener>();

    /** 原子性：状态，默认为不可用 */
    private AutoReconnectState state = AutoReconnectState.DISABLED;

    private Thread connectTask = null;

    public AutoReconnectController(final Server server) {
        this(server, DEFAULT_DELAY);
    }

    public AutoReconnectController(final Server server, final int delay) {
        super();
        setDelay(delay);

        this.server = server;
        this.server.addStateListener(this);
    }

    /**
     * 添加监听
     * @param listener
     */
    public void addListener(final AutoReconnectListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
            listener.stateChanged(this.state);
        }
    }

    /**
     * 移除监听
     * @param listener
     */
    public void removeListener(final AutoReconnectListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * 通知客户端改变状态
     * @param state
     */
    protected void notifyStateChange(final AutoReconnectState state) {
        this.state = state;
        for (AutoReconnectListener listener : this.listeners) {
            listener.stateChanged(state);
        }
    }

    /**
     * 获取延迟
     * @return
     */
    public int getDelay() {
        return this.delay;
    }

    /**
     * 如果延迟时间小于0，则使用默认的延迟时间
     *
     * @param delay The delay to use
     */
    public void setDelay(int delay) {
        if (delay <= 0) {
            delay = DEFAULT_DELAY;
        }
        this.delay = delay;
    }

    /**
     * 连接服务器
     */
    public synchronized void connect() {
        if (isRequested()) {
            return;
        }
        log.debug("Requesting connection");
        notifyStateChange(AutoReconnectState.DISCONNECTED);

        triggerReconnect(false);
    }

    /**
     * 断开服务器
     */
    public synchronized void disconnect() {
        if (!isRequested()) {
            return;
        }

        log.debug("Un-Requesting connection");

        notifyStateChange(AutoReconnectState.DISABLED);
        this.server.disconnect();
    }

    /**
     * 判断状态是否可用
     * @return
     */
    public boolean isRequested() {
        return this.state != AutoReconnectState.DISABLED;
    }

    @Override
    public synchronized void connectionStateChanged(final boolean connected) {
        log.info("连接状态改变: {}", connected);

        if (!connected) {
            if (isRequested()) {
                notifyStateChange(AutoReconnectState.DISCONNECTED);
                triggerReconnect(true);
            }
        } else {
            if (!isRequested()) {
                this.server.disconnect();
            } else {
                notifyStateChange(AutoReconnectState.CONNECTED);
            }
        }
    }

    /**
     * 触发重连
     * @param wait
     */
    private synchronized void triggerReconnect(final boolean wait) {
        if (this.connectTask != null) {
            log.info("服务器已经连接，无需重连！");
            return;
        }

        log.info("触发服务重连");
        this.connectTask = new Thread(new Runnable() {

            @Override
            public void run() {
                boolean result = false;
                try {
                    result = performReconnect(wait);
                } finally {
                    AutoReconnectController.this.connectTask = null;
                    log.info("已完成服务重连:{}", result);
                    if (!result) {
                        triggerReconnect(true);
                    }
                }
            }
        }, "客户端监听-OPC自动重连");
        this.connectTask.setDaemon(true);
        this.connectTask.start();
    }

    /**
     * 执行重连
     * @param wait
     * @return
     */
    private boolean performReconnect(final boolean wait) {
        try {
            if (wait) {
                notifyStateChange(AutoReconnectState.WAITING);
                log.info("推迟 {}...", this.delay);
                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
        }

        if (!isRequested()) {
            log.debug("Request canceled during delay");
            return true;
        }

        try {
            log.debug("连接到服务器");
            notifyStateChange(AutoReconnectState.CONNECTING);
            synchronized (this) {
                this.server.connect();
                return true;
            }
            // CONNECTED state will be set by server callback
        } catch (Throwable e) {
            log.error("服务重连失败！", e);
            notifyStateChange(AutoReconnectState.DISCONNECTED);
            return false;
        }
    }

}
