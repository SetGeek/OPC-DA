package cn.com.sgcc.gdt.opc.client;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author ck.yang
 */
@Slf4j
public class ConnecterTest {
    ScheduledExecutorService threadPool;
    ConnectionInformation connInfo;
    Connecter connecter;
    @Before
    public void before() throws Exception {
        threadPool = new ScheduledThreadPoolExecutor(5, r->{
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("客户端-任务-" + thread.getId());
            thread.setUncaughtExceptionHandler((Thread t, Throwable e)->{
                log.error("'{}'发生异常！", t.getName(), e);
            });
            return thread;
        });

        connInfo = new ConnectionInformation();
        connInfo.setHost("192.168.2.254");
        connInfo.setUser("Administrator");
        connInfo.setPassword("GDTvm6.5");

        //kepware
        connInfo.setClsid("7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729");
        //ks.demo
//        connInfo.setClsid("B57C679B-665D-4BB0-9848-C5F2C4A6A280");
        //simulator
//        connInfo.setClsid("A879768C-7387-11D4-B0D8-009027242C59");

//        connInfo.setProgId("ICONICS.SimulatorOPCDA.2");//simulator
    }

    @After
    public void after() throws Exception {
        connecter.disconnect();
        threadPool.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void testRead() throws Exception {
        threadPool.submit(()->{
            try {
                connecter = new Connecter(connInfo, threadPool);
                Server server = connecter.connect();

                Browser.readAsyn(server, threadPool, 3000L, dataItems ->{
                    System.out.println("读取结果:"+ dataItems);
                });
            } catch (Throwable t){
                log.error("客户端运行失败！", t);
            }
        }).get();
    }
}