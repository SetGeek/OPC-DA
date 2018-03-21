package cn.com.sgcc.gdt.opc.console;

import cn.com.sgcc.gdt.opc.client.Browser;
import cn.com.sgcc.gdt.opc.client.Connecter;
import cn.com.sgcc.gdt.opc.client.bean.DataItem;
import cn.com.sgcc.gdt.opc.console.bean.ConnectInfo;
import cn.com.sgcc.gdt.opc.console.config.ClientConfig;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * TODO
 *
 * @author ck.yang
 */
@Component
@Slf4j
public class ClientRunner {
    /** 客户端所有操作的线程池 */
    @Autowired
    ScheduledExecutorService scheduledPool;

    /** 配置文件中的信息 */
    @Autowired
    ClientConfig clientConfig;

    /** 并发的Map，key为连接的编号（即类ServerInfo中的id），存储客户端连接，以便逐个连接与释放 */
    ConcurrentMap<String, Connecter> connecterMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Throwable {
        for(String runId : clientConfig.getRunServerId()){
            this.connect(runId);
        }
    }

    /**
     * 获取指定的服务器连接
     * @param serverId 服务器唯一标识
     * @return
     */
    private ConnectInfo getServerInfo(String serverId){
        for(ConnectInfo info: clientConfig.getServers()){
            if(info.getId().equals(serverId)){
                return info;
            }
        }
        return new ConnectInfo();
    }

    /**
     * 连接服务器
     * @param serverId 服务器唯一标识
     * @throws Throwable
     */
    public void connect(String serverId) throws Throwable {
        if(connecterMap.containsKey(serverId) && connecterMap.get(serverId).isConnect()){
            log.info("服务器似乎正常运行，无需重连！");
            return;
        }else {
            connecterMap.remove(serverId);
        }
        Optional<ConnectInfo> serverInfo = Optional.of(getServerInfo(serverId));
        Connecter connecter = new Connecter(serverInfo.get(), scheduledPool);
        connecter.connect();
        connecterMap.put(serverId, connecter);
        log.info("服务器已连接！");
    }

    /**
     * 断开指定的服务器
     * @param serverId 服务器唯一标识
     */
    public void disconnect(String serverId){
        Optional<Connecter> connecter = Optional.of(connecterMap.get(serverId));
        connecter.get().disconnect();
        connecterMap.remove(serverId);
        log.info("开始断开连接...");
    }

    /**
     * 到指定的服务器上读取节点数据
     * @param serverId 服务器唯一标识
     * @throws Throwable
     */
    public void read(String serverId) throws Throwable {
        Optional<Connecter> connecter = Optional.of(connecterMap.get(serverId));
        Server server = connecter.get().getServer();
        ConnectInfo serverInfo = this.getServerInfo(serverId);
        if(serverInfo.getHeartbeat()!=null){
            Browser.readAsyn(server, scheduledPool, serverInfo.getHeartbeat(), (List<DataItem> dataItems)->{
                System.err.println("查询结果"+dataItems);
            });
        } else{
            Browser.readAsyn(server, scheduledPool, (List<DataItem> dataItems)->{
                System.err.println("查询结果"+dataItems);
            });
        }
    }

    /**
     * 关闭连接
     */
    @PreDestroy
    public void preDestory() {
        if(!connecterMap.isEmpty()){
            connecterMap.forEach((id, connecter)->{
                connecter.disconnect();
            });
        }
    }
}
