package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import cn.com.sgcc.gdt.opc.lib.da.browser.BaseBrowser;
import cn.com.sgcc.gdt.opc.lib.da.browser.Branch;
import cn.com.sgcc.gdt.opc.lib.da.browser.Leaf;
import cn.com.sgcc.gdt.opc.lib.da.browser.TreeBrowser;
import org.jinterop.dcom.common.JIException;

import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class OPCTest3 {

    private static void dumpLeaf(final String prefix, final Leaf leaf) {
        System.out.println(prefix + "Leaf: " + leaf.getName() + " [" + leaf.getItemId() + "]");
    }

    private static void dumpBranch(final String prefix, final Branch branch) {
        System.out.println(prefix + "Branch: " + branch.getName());
    }

    public static void dumpTree(final Branch branch, final int level) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        final String indent = sb.toString();

        for (final Leaf leaf : branch.getLeaves()) {
            dumpLeaf(indent, leaf);
        }
        for (final Branch subBranch : branch.getBranches()) {
            dumpBranch(indent, subBranch);
            dumpTree(subBranch, level + 1);
        }
    }

    public static void main(final String[] args) throws Throwable {
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(args[0]);
        ci.setDomain(args[1]);
        ci.setUser(args[2]);
        ci.setPassword(args[3]);
        ci.setClsId(args[4]);

        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        try {
            // connect to server
            server.connect();

            // browse flat
            final BaseBrowser flatBrowser = server.getFlatBrowser();
            if (flatBrowser != null) {
                for (final String item : server.getFlatBrowser().browse("")) {
                    System.out.println(item);
                }
            }

            // browse tree
            final TreeBrowser treeBrowser = server.getTreeBrowser();
            if (treeBrowser != null) {
                dumpTree(treeBrowser.browse(), 0);
            }

            // browse tree manually
            browseTree("", treeBrowser, new Branch());
        } catch (final JIException e) {
            e.printStackTrace();
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }

    private static void browseTree(final String prefix, final TreeBrowser treeBrowser, final Branch branch) throws IllegalArgumentException, UnknownHostException, JIException {
        treeBrowser.fillLeaves(branch);
        treeBrowser.fillBranches(branch);

        for (final Leaf leaf : branch.getLeaves()) {
            dumpLeaf("M - " + prefix + " ", leaf);
        }
        for (final Branch subBranch : branch.getBranches()) {
            dumpBranch("M - " + prefix + " ", subBranch);
            browseTree(prefix + " ", treeBrowser, subBranch);
        }
    }
}
