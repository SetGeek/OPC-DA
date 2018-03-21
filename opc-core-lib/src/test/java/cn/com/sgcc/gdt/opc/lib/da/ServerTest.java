package cn.com.sgcc.gdt.opc.lib.da;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO
 *
 * @author ck.yang
 */
public class ServerTest {
    @Test
    public void test() throws Exception {
        Integer socketTimeout = Integer.getInteger("rpc.socketTimeout", 0);
        System.out.println(socketTimeout);
    }
}