package cn.com.sgcc.gdt.opc.console;

import cn.com.sgcc.gdt.opc.client.Browser;
import cn.com.sgcc.gdt.opc.console.config.BeanTools;
import cn.com.sgcc.gdt.opc.console.config.ClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 程序入口
 * @author ck
 */
@SpringBootApplication
@EnableScheduling
public class OpcDaApplication {

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(OpcDaApplication.class, args);
		//列出目标主机上的所有服务
		ClientConfig clientConfig = BeanTools.getBean(ClientConfig.class);
		Browser.listServer(clientConfig.getServers().get(0).getHost(), clientConfig.getServers().get(0).getHost(), clientConfig.getServers().get(0).getUser(), clientConfig.getServers().get(0).getPassword());
		ClientRunner runner = BeanTools.getBean(ClientRunner.class);
		runner.init();
		Browser.browserServer();
		TimeUnit.DAYS.sleep(999999999L);
	}
}
