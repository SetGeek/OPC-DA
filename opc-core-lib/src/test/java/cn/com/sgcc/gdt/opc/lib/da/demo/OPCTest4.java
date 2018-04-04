package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.AccessBase;
import cn.com.sgcc.gdt.opc.lib.da.Async20Access;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import org.jinterop.dcom.common.JIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class OPCTest4 {
    private static Logger _log = LoggerFactory.getLogger(OPCTest4.class);

    public static void main(final String[] args) throws Throwable {
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(args[0]);
        ci.setDomain(args[1]);
        ci.setUser(args[2]);
        ci.setPassword(args[3]);
        ci.setClsId(args[4]);

        final Set<String> items = new HashSet<String>();
        for (int i = 5; i < args.length; i++) {
            items.add(args[i]);
        }
        if (items.isEmpty()) {
            items.add("Saw-toothed Waves.Int2");
        }

        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        try {
            // connect to server
            server.connect();

            // add sync access
            final AccessBase access = new Async20Access(server, 100, false);
            for (final String itemId : items) {
                access.addItem(itemId, new DataCallbackDumper());
            }

            // start reading
            access.bind();

            // wait a little bit
            _log.info("Sleep for some seconds to give events a chance...");
            Thread.sleep(10 * 1000);
            _log.info("Returned from sleep");

            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }
}
