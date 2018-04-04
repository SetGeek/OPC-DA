package cn.com.sgcc.gdt.opc.console;

import cn.com.sgcc.gdt.opc.client.Browser;
import cn.com.sgcc.gdt.opc.client.Connecter;
import cn.com.sgcc.gdt.opc.client.bean.DataItem;
import cn.com.sgcc.gdt.opc.console.bean.ConnectInfo;
import cn.com.sgcc.gdt.opc.console.config.ClientConfig;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    KafkaTemplate kafkaTemplate;

//    @PostConstruct
    public void init() throws Throwable {
        for(String runId : clientConfig.getRunServerId()){
            this.connect(runId);
//            this.read(runId);
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
        Server server = connecter.get().getServer().get();
        ConnectInfo serverInfo = this.getServerInfo(serverId);
        String topic = serverInfo.getTopic();
        Browser.readAsyn(server, scheduledPool, serverInfo.getHeartbeat(), (List<DataItem> dataItems)->{
            String data = JSONArray.toJSON(dataItems).toString();
            kafkaTemplate.send(topic, data).get();
            log.info("主题'{}'已发送！,详情：{}", topic, data);
        });

    }

    @Scheduled(cron = "0 0,15,30,45 * ? * *")
    public void regularSend(){

        try {

            for(ConcurrentMap.Entry<String, Connecter> entry: connecterMap.entrySet()){
                String serverId = entry.getKey();
                Connecter connecter = entry.getValue();
                Server server = connecter.getServer().get();
                ConnectInfo serverInfo = this.getServerInfo(serverId);
                String topic = serverInfo.getTopic();
                //过滤数据
                List<String> itemIds = Browser.browseItemIds(server).stream().filter(item -> {
                    String profix = clientConfig.getItems().getOrDefault("profix", "");
                    String exclusion = clientConfig.getItems().get("exclusion");
                    if(StringUtils.isEmpty(exclusion)){
                        return item.startsWith(profix);
                    }else{
                        return item.startsWith(profix)&&!item.contains(exclusion);
                    }
                }).collect(Collectors.toList());

                List<DataItem> dataItems = Browser.readSync(server, itemIds);
                if(dataItems!=null && dataItems.size()>0){
                    String data = JSONArray.toJSON(dataItems).toString();
                    kafkaTemplate.send(topic, data);
                    log.info("主题'{}'已发送{}条！", topic,dataItems.size());
                } else {
                    log.warn("主题'{}'无数据！", topic);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
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
