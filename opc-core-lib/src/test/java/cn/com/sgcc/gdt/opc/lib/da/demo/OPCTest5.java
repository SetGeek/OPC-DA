package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.Async20Access;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import org.jinterop.dcom.common.JIException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

public class OPCTest5 {
    public static void main(final String[] args) throws Throwable {
        // create connection information
        final ConnectionInformation baseInfo = new ConnectionInformation();
        baseInfo.setHost(args[0]);
        baseInfo.setDomain(args[1]);
        baseInfo.setUser(args[2]);
        baseInfo.setPassword(args[3]);

        final List<OPCTestInfo> testInfo = new LinkedList<OPCTestInfo>();
        int i = 0;

        try {

            while (args.length > i * 2 + 4) {
                final ConnectionInformation ci = new ConnectionInformation(baseInfo);
                ci.setClsId(args[i * 2 + 4]);
                final OPCTestInfo ti = new OPCTestInfo();
                ti._info = ci;
                ti._itemId = args[i * 2 + 5];
                ti._server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

                ti._server.connect();
                ti._access = new Async20Access(ti._server, 100, false);
                ti._access.addItem(ti._itemId, new DataCallbackDumper());
                ti._access.bind();

                testInfo.add(ti);
                i++;
            }

            // wait a little bit
            Thread.sleep(10 * 1000);
        } catch (final JIException e) {
            System.out.println(String.format("%08X", e.getErrorCode()));
        }
    }
}
