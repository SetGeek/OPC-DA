package cn.com.sgcc.gdt.opc.da.simple.reader;
import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * TODO
 *
 * @author ck
 */
public class MainApplication {

    public static void main(String[] ars) throws Throwable{


        List<ConnectionInformation> opcConfig = PropertiesUtil.getOpcConfig();


        //1.创建Scheduler的工厂
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        //2.从工厂中获取调度器实例
        Scheduler scheduler = schedulerFactory.getScheduler();
        //3.创建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(ReaderJob.class)
                .withDescription("OPC客户端读取任务")
                .withIdentity("readJob", "ReaderJobGroup").build();
        //4.创建Trigger
        //使用SimpleScheduleBuilder或者CronScheduleBuilder
        Trigger t = TriggerBuilder.newTrigger()
                .withDescription("")
                .withIdentity("readTrigger", "readerTriggerGroup")
                //.withSchedule(SimpleScheduleBuilder.simpleSchedule())
                //默认当前时间启动
                .startAt(Calendar.getInstance().getTime())
                //两秒执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?"))
                .build();
        //5.注册任务和定时器
        scheduler.scheduleJob(jobDetail, t);
        //6.启动 调度器
        scheduler.start();
    }
}
