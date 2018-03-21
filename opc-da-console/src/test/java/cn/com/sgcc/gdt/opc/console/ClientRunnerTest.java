package cn.com.sgcc.gdt.opc.console;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TODO
 *
 * @author ck.yang
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientRunnerTest {

    @Autowired
    ClientRunner clientRunner;
    String serverId = "2";
    @Before
    public void init() throws Throwable{
        clientRunner.connect(serverId);
    }

    @After
    public void destroy() throws Throwable{
        clientRunner.disconnect(serverId);
    }

    @Test
    public void testRead() throws Throwable{
        clientRunner.read(serverId);
    }
}