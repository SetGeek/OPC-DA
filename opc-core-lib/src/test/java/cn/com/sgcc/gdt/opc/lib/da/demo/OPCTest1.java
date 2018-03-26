package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.Group;
import cn.com.sgcc.gdt.opc.lib.da.Item;
import cn.com.sgcc.gdt.opc.lib.da.ItemState;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import cn.com.sgcc.gdt.opc.lib.da.browser.Branch;
import cn.com.sgcc.gdt.opc.lib.da.browser.Leaf;
import org.jinterop.dcom.common.JIException;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.Executors;

public class OPCTest1 {
    public static void dumpItemState(final Item item, final ItemState state) {
        System.out.println(String.format("Item: %s, Value: %s, Timestamp: %tc, Quality: %d", item.getId(), state.getValue(), state.getTimestamp(), state.getQuality()));
    }

    public static void dumpTree(final Branch branch, final int level) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        final String indent = sb.toString();

        for (final Leaf leaf : branch.getLeaves()) {
            System.out.println(indent + "Leaf: " + leaf.getName() + " [" + leaf.getItemId() + "]");
        }
        for (final Branch subBranch : branch.getBranches()) {
            System.out.println(indent + "Branch: " + subBranch.getName());
            dumpTree(subBranch, level + 1);
        }
    }

    @Test
    public void testRead() throws Exception {
        ConnectionInformation connInfo = BaseInfo.getConnInfo();

        // create a new server
        final Server server = new Server(connInfo, Executors.newSingleThreadScheduledExecutor());
        try {
            // connect to server
            server.connect();

            // browse
            dumpTree(server.getTreeBrowser().browse(), 0);

            // add sync reader

            // Add a new group
            Group group = server.addGroup("test");
            // group is initially active ... just for demonstration
            group.setActive(true);

            // We already have our group ... just for demonstration
            group = server.findGroup("test");

            // Add a new item to the group
            final Item item = group.addItem("testChannel1.device1.tag1");
            // Items are initially active ... just for demonstration
            item.setActive(true);

            // Add some more items ... including one that is already existing
            final Map<String, Item> items = group.addItems("testChannel1.device1.tag1", "testChannel1.device1.tag2");

            // sync-read some values
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                dumpItemState(item, item.read(false));
            }
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }

}
