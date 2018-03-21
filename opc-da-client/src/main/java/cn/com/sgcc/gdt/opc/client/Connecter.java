package cn.com.sgcc.gdt.opc.client;

import cn.com.sgcc.gdt.opc.core.dcom.da.OPCSERVERSTATE;
import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.AutoReconnectController;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import cn.com.sgcc.gdt.opc.lib.da.ServerConnectionStateListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务器连接
 *
 * @author ck.yang
 */
@Slf4j
public class Connecter {

    public final String name;

    private final Server server;

    private AutoReconnectController controller;

    private ServerConnectionStateListener listener;

    public Connecter(ConnectionInformation connInfo, ScheduledExecutorService scheduledExecutorService) {
        //TODO 需要添加名称，以便区别不同的服务器
        name = "";
        server = new Server(connInfo, scheduledExecutorService);
        listener = connected -> {
            if(!connected){
                try {
                    server.connect();
                } catch (Exception e) {
                    log.error("监听到连接断开，尝试重连失败!");
                }
            }
        };
    }

    public Server connect() throws Throwable {
        server.connect();
        long connectTime = 10;

        while (connectTime-- >0){
            if(server!=null && server.getServerState()!=null && server.getServerState().getServerState()!=null){
                server.addStateListener(listener);
                return server;
            }else{
                TimeUnit.SECONDS.sleep(2);
                log.warn("尝试重新连接！");
            }
        }
        throw new NullPointerException("服务器连接失败!");
    }

    public Server getServer() {
        return server;
    }

    public void disconnect(){
        server.removeStateListener(listener);
        if(controller!=null){
            controller.disconnect();
        }
        if(server!=null){
            server.dispose();
        }
    }

    public boolean isConnect(){
        OPCSERVERSTATE serverState = server.getServerState().getServerState();
       return OPCSERVERSTATE.OPC_STATUS_RUNNING.equals(serverState) ? true : false;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.disconnect();
        System.out.println("已销毁连接！");
    }
}
