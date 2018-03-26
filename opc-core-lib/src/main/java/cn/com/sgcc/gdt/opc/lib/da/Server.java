/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package cn.com.sgcc.gdt.opc.lib.da;

import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcNamespaceType;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcServerStatus;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCBrowseServerAddressSpace;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCGroupStateMgt;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCServer;
import cn.com.sgcc.gdt.opc.lib.common.AlreadyConnectedException;
import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.common.NotConnectedException;
import cn.com.sgcc.gdt.opc.lib.da.browser.FlatBrowser;
import cn.com.sgcc.gdt.opc.lib.da.browser.TreeBrowser;
import cn.com.sgcc.gdt.opc.lib.da.exception.DuplicateGroupException;
import cn.com.sgcc.gdt.opc.lib.da.exception.UnknownGroupException;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 服务
 * @author: ck.yang
 */
@Slf4j
@Data
@ToString
public class Server {

    private final ConnectionInformation connectionInformation;

    private JISession session;

    private JIComServer comServer;

    private OPCServer server;

    private boolean defaultActive = true;

    private int defaultUpdateRate = 1000;

    private Integer defaultTimeBias;

    private Float defaultPercentDeadband;

    private int defaultLocaleID = 0;

    private ErrorMessageResolver errorMessageResolver;

    private final Map<Integer, Group> groups = new HashMap<>();

    private final List<ServerConnectionStateListener> stateListeners = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService scheduler;

    public Server(final ConnectionInformation connectionInformation, final ScheduledExecutorService scheduler) {
        this.connectionInformation = connectionInformation;
        this.scheduler = scheduler;
        Thread.currentThread().setName("客户端任务-与服务器交互"+Thread.currentThread().getId());
    }

    /**
     * 从Server中获取任务调取器（即线程池），当服务发生故障的时候，调度器可能会阻塞，
     * 所以该调度器最好不要用于关键的操作
     * @return the scheduler for the server
     */
    public ScheduledExecutorService getScheduler() {
        return this.scheduler;
    }

    /**
     * 判断连接状态: true为连接，false为断开
     * @return
     */
    protected synchronized boolean isConnected() {
        return this.session != null;
    }

    /**
     * 连接服务
     * @throws IllegalArgumentException 非法参数异常
     * @throws UnknownHostException 未知主机异常
     * @throws JIException 连接服务异常
     * @throws AlreadyConnectedException 已经连接异常
     */
    public synchronized void connect() throws IllegalArgumentException, UnknownHostException, JIException, AlreadyConnectedException {
        if (isConnected()) {
            throw new AlreadyConnectedException();
        }

        final int socketTimeout = Integer.getInteger("rpc.socketTimeout", 0);
        log.info("Socket超时为：{}", socketTimeout);

        try {
            if (this.connectionInformation.getClsId() != null) {
                this.session = JISession.createSession(
                        this.connectionInformation.getDomain(),
                        this.connectionInformation.getUser(),
                        this.connectionInformation.getPassword());
                this.session.setGlobalSocketTimeout(socketTimeout);
                this.comServer = new JIComServer(
                        JIClsid.valueOf(this.connectionInformation.getClsId()),
                        this.connectionInformation.getHost(), this.session);
            } else if (this.connectionInformation.getProgId() != null) {
                this.session = JISession.createSession(
                        this.connectionInformation.getDomain(),
                        this.connectionInformation.getUser(),
                        this.connectionInformation.getPassword());
                this.session.setGlobalSocketTimeout(socketTimeout);
                this.comServer = new JIComServer(
                        JIProgId.valueOf(this.connectionInformation.getProgId()),
                        this.connectionInformation.getHost(), this.session);
            } else {
                throw new IllegalArgumentException("请配置OPC服务器软件的 clsId(proId)！如果配置了clsId，progId可以为空");
            }

            this.server = new OPCServer(this.comServer.createInstance());
            this.errorMessageResolver = new ErrorMessageResolver(this.server.getCommon(), this.defaultLocaleID);
        } catch (final UnknownHostException e) {
            log.info("连接未知的服务器，发生异常！", e);
            cleanup();
            throw e;
        } catch (final JIException e) {
            log.info("无法连接服务器！", e);
            cleanup();
            throw e;
        } catch (final Throwable e) {
            log.warn("未知错误！", e);
            cleanup();
            throw new RuntimeException(e);
        }
        notifyConnectionStateChange(true);
    }

    /**
     * 连接关闭后的清理工作
     */
    protected void cleanup() {
        log.info("销毁DCOM会话...");
        final JISession destructSession = this.session;
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("客户端任务-销毁DCOM会话-" + thread.getId());
            //thread.setDaemon(true) ;
            return thread;
        });

        executor.submit(()->{
            long ts = System.currentTimeMillis();
            try {
                log.debug("开始销毁DCOM会话");
                JISession.destroySession(destructSession);
                log.info("DCOM会话已销毁！");
            } catch (final Throwable e) {
                log.warn("销毁DCOM会话失败！", e);
            } finally {
                log.info("终止会话花费时间:{} ms", System.currentTimeMillis() - ts);
            }
        });

        log.info("强制销毁DCOM会话...");
        this.errorMessageResolver = null;
        this.session = null;
        this.comServer = null;
        this.server = null;

        this.groups.clear();
    }

    /**
     * 断开与服务器的连接
     */
    public synchronized void disconnect() {
        if(isConnected()){
            try {
                notifyConnectionStateChange(false);
            } catch (final Throwable t) {
                log.warn("切换连接状态发生异常！", t);
            } finally {
                cleanup();
            }
        }
    }

    /**
     * 发生错误时断开连接
     */
    public void dispose() {
        disconnect();
    }

    /**
     * 获取组
     * @param groupMgt
     * @return
     * @throws JIException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     */
    protected synchronized Group getGroup(final OPCGroupStateMgt groupMgt)
            throws JIException, IllegalArgumentException, UnknownHostException {
        final Integer serverHandle = groupMgt.getState().getServerHandle();
        if (this.groups.containsKey(serverHandle)) {
            return this.groups.get(serverHandle);
        } else {
            final Group group = new Group(this, serverHandle, groupMgt);
            this.groups.put(serverHandle, group);
            return group;
        }
    }

    /**
     * 添加一个组到服务器
     * @param name 组名
     * @return
     * @throws NotConnectedException 如果服务未连接，需要先调用{@link Server#connect()}
     * @throws IllegalArgumentException 错误的参数异常
     * @throws UnknownHostException 未知主机异常
     * @throws JIException  连接异常
     * @throws DuplicateGroupException 组名重复异常
     */
    public synchronized Group addGroup(final String name)
            throws NotConnectedException, IllegalArgumentException,
            UnknownHostException, JIException, DuplicateGroupException {
        if (!isConnected()) {
            throw new NotConnectedException();
        }

        try {
            final OPCGroupStateMgt groupMgt = this.server.addGroup(name,
                    this.defaultActive, this.defaultUpdateRate, 0,
                    this.defaultTimeBias, this.defaultPercentDeadband,
                    this.defaultLocaleID);
            return getGroup(groupMgt);
        } catch (final JIException e) {
            switch (e.getErrorCode()) {
                case 0xC004000C:
                    throw new DuplicateGroupException("重复的Group异常");
                default:
                    throw e;
            }
        }
    }

    /**
     * 添加一个组，组名由服务器自动生成
     * <p>
     * 这个方法只是调用了 {@link Server#addGroup(String)}
     * @return
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     * @throws NotConnectedException
     * @throws JIException
     * @throws DuplicateGroupException
     */
    public Group addGroup() throws IllegalArgumentException,
            UnknownHostException, NotConnectedException, JIException,
            DuplicateGroupException {
        return addGroup(null);
    }

    /**
     * 根据名称查找组
     * @param name
     * @return
     * @throws IllegalArgumentException 参数错误异常
     * @throws UnknownHostException 未知主机异常
     * @throws JIException 连接异常
     * @throws UnknownGroupException 找不到组的异常
     * @throws NotConnectedException 无连接异常
     */
    public Group findGroup(final String name) throws IllegalArgumentException,
            UnknownHostException, JIException, UnknownGroupException,
            NotConnectedException {
        if (!isConnected()) {
            throw new NotConnectedException();
        }

        try {
            final OPCGroupStateMgt groupMgt = this.server.getGroupByName(name);
            return getGroup(groupMgt);
        } catch (final JIException e) {
            switch (e.getErrorCode()) {
                case 0x80070057:
                    throw new UnknownGroupException(name);
                default:
                    throw e;
            }
        }
    }

    /**
     * 获取平级的浏览器
     * @return
     */
    public FlatBrowser getFlatBrowser() {
        final OPCBrowseServerAddressSpace browser = this.server.getBrowser();
        if (browser == null) {
            return null;
        }

        return new FlatBrowser(browser);
    }

    /**
     * 获取树形结构的分级浏览器
     * @return
     * @throws JIException
     */
    public TreeBrowser getTreeBrowser() throws JIException {
        final OPCBrowseServerAddressSpace browser = this.server.getBrowser();
        if (browser == null) {
            return null;
        }

        if (browser.queryOrganization() != OpcNamespaceType.OPC_NS_HIERARCHIAL) {
            return null;
        }

        return new TreeBrowser(browser);
    }

    /**
     * 获取错误信息
     * @param errorCode
     * @return
     */
    public synchronized String getErrorMessage(final int errorCode) {
        if (this.errorMessageResolver == null) {
            return String.format("Unknown error (%08X)", errorCode);
        }

        // resolve message
        final String message = this.errorMessageResolver.getMessage(errorCode);

        // and return if successfull
        if (message != null) {
            return message;
        }

        // return default message
        return String.format("未知错误：(%08X)", errorCode);
    }

    /**
     * 添加服务状态监听器
     * @param listener
     */
    public synchronized void addStateListener(
            final ServerConnectionStateListener listener) {
        this.stateListeners.add(listener);
        listener.connectionStateChanged(isConnected());
    }

    /**
     * 移除服务状态监听器
     * @param listener
     */
    public synchronized void removeStateListener(
            final ServerConnectionStateListener listener) {
        this.stateListeners.remove(listener);
    }

    /**
     * 通知连接状态改变
     * @param connected
     */
    protected void notifyConnectionStateChange(final boolean connected) {
        final List<ServerConnectionStateListener> list = new ArrayList<>(this.stateListeners);
        for (final ServerConnectionStateListener listener : list) {
            listener.connectionStateChanged(connected);
        }
    }

    /**
     * 获取服务状态：包括
     * @param timeout
     * @return
     * @throws Throwable
     */
    public OpcServerStatus getServerState(final int timeout) throws Throwable {
        return new ServerStateOperation(this.server).getServerState(timeout);
    }

    /**
     * 获取服务状态
     * @return
     */
    public OpcServerStatus getServerState() {
        try {
            return getServerState(2500);
        } catch (final Throwable e) {
            log.info("Server connection failed", e);
            dispose();
            return null;
        }
    }

    /**
     * 移除组
     * @param group
     * @param force
     * @throws JIException
     */
    public void removeGroup(final Group group, final boolean force)
            throws JIException {
        if (this.groups.containsKey(group.getServerHandle())) {
            this.server.removeGroup(group.getServerHandle(), force);
            this.groups.remove(group.getServerHandle());
        }
    }

    /**
     * 释放资源，避免出现异常
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.disconnect();
    }
}
