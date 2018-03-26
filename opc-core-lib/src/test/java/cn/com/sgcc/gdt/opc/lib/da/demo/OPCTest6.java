package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.AccessBase;
import cn.com.sgcc.gdt.opc.lib.da.AutoReconnectController;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import cn.com.sgcc.gdt.opc.lib.da.SyncAccess;
import org.jinterop.dcom.common.JIException;

import java.util.concurrent.Executors;

public class OPCTest6 {
    public static void main(final String[] args) throws Throwable {
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(args[0]);
        ci.setDomain(args[1]);
        ci.setUser(args[2]);
        ci.setPassword(args[3]);
        ci.setClsId(args[4]);

        String itemId = "Saw-toothed Waves.Int2";
        if (args.length >= 6) {
            itemId = args[5];
        }

        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        final AutoReconnectController autoReconnectController = new AutoReconnectController(server);
        try {
            // connect to server
            autoReconnectController.connect();

            // add sync access

            final AccessBase access = new SyncAccess(server, 100);
            access.addItem(itemId, new DataCallbackDumper());

            // start reading
            access.bind();

            // run forever
            final boolean running = true;
            while (running) {
                Thread.sleep(10 * 1000);
            }

            /*
            // stop reading
            access.unbind ();

            // disconnect
            autoReconnectController.disconnect ();
            */
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }
}
