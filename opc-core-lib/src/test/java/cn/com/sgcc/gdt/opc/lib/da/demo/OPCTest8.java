package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.core.dcom.list.ClassDetails;
import cn.com.sgcc.gdt.opc.lib.list.Categories;
import cn.com.sgcc.gdt.opc.lib.list.Category;
import cn.com.sgcc.gdt.opc.lib.list.ServerList;
import org.jinterop.dcom.common.JIException;

import java.util.Collection;

public class OPCTest8 {
    protected static void showDetails(final ServerList serverList, final String clsid) throws JIException {
        final ClassDetails cd = serverList.getDetails(clsid);
        if (cd != null) {
            System.out.println(cd.getProgId() + " = " + cd.getDescription());
        } else {
            System.out.println("unknown");
        }
    }

    public static void main(final String[] args) throws Throwable {
        final ServerList serverList = new ServerList(args[0], args[2], args[3], args[1]);

        final String cls = serverList.getClsIdFromProgId("Matrikon.OPC.Simulation.1");
        System.out.println("Matrikon OPC Simulation Server: " + cls);
        showDetails(serverList, cls);

        final Collection<ClassDetails> detailsList = serverList.listServersWithDetails(new Category[]{Categories.OPCDAServer20}, new Category[]{});

        for (final ClassDetails details : detailsList) {
            System.out.println(String.format("Found: %s", details.getClsId()));
            System.out.println(String.format("\tProgID: %s", details.getProgId()));
            System.out.println(String.format("\tDescription: %s", details.getDescription()));
        }
    }
}
