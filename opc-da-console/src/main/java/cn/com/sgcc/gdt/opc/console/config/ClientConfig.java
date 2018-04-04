package cn.com.sgcc.gdt.opc.console.config;

import cn.com.sgcc.gdt.opc.console.bean.ConnectInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * TODO
 *
 * @author ck.yang
 */
@Configuration
@ConfigurationProperties(prefix = "opc")
@Slf4j
@Data
public class ClientConfig {

    /** 配置文件中的服务器信息 */
    private List<ConnectInfo> servers;
    /** 开机即连接的服务 */
    private List<String> runServerId;

    private Map<String, String> items;

    public void setServers(List<ConnectInfo> servers) {
        List<String> ids = new ArrayList<>();
        servers.forEach(serverConfig -> {
            if(serverConfig.getId()==null || ids.contains(serverConfig.getId())){
                //duplicate
                throw new DuplicateKeyException("OPC配置文件中的'id'不能为空，也不可重复！");
            }else {
                ids.add(serverConfig.getId());
            }
        });
        this.servers = servers;
    }

    @Bean
    public ScheduledExecutorService getScheduledExecutorService(){

        return new ScheduledThreadPoolExecutor(5, (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("客户端任务-" + thread.getId());
            thread.setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                log.error("'{}'发生异常！", t.getName(), e);
            });
            return thread;
        });
    }
}
