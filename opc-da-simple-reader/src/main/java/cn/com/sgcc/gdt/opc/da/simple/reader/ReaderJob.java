package cn.com.sgcc.gdt.opc.da.simple.reader;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 读取数据的任务
 * @author ck
 */
@Slf4j
public class ReaderJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("执行定时任务！");
    }
}
