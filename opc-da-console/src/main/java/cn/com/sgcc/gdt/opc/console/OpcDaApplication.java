package cn.com.sgcc.gdt.opc.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class OpcDaApplication {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpringApplication.run(OpcDaApplication.class, args);
		TimeUnit.DAYS.sleep(999999999L);
	}
}
