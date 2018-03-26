package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.AccessBase;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import cn.com.sgcc.gdt.opc.lib.da.SyncAccess;
import org.jinterop.dcom.common.JIException;
import org.junit.Test;

import java.util.concurrent.Executors;

public class OPCTest2 {

    /**
     * 同步读取测试
     * @throws Exception
     */
    @Test
    public void testSync() throws Exception {
// create connection information
        ConnectionInformation connInfo = BaseInfo.getConnInfo();
        String itemId = "testChannel1.device1.tag1";

        // create a new server
        final Server server = new Server(connInfo, Executors.newSingleThreadScheduledExecutor());
        try {
            // connect to server
            server.connect();

            // add sync access

            final AccessBase access = new SyncAccess(server, 100);
            access.addItem(itemId, new DataCallbackDumper());

            // start reading
            access.bind();

            // wait a little bit
            Thread.sleep(10 * 1000);

            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }
}
