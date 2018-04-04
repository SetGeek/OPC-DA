package cn.com.sgcc.gdt.opc.client;

import cn.com.sgcc.gdt.opc.client.bean.ServerInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * TODO
 *
 * @author ck
 */
public class BrowserTest {
    @Test
    public void readAsyn() throws Exception {
    }

    @Test
    public void subscibe() throws Exception {
    }

    @Test
    public void browseItemIds() throws Exception {
    }

    @Test
    public void listServer() throws Throwable {
        List<ServerInfo> infos = Browser.listServer("192.168.2.254", "192.168.2.254", "Administrator", "GDTvm6.5");
//        for (ServerInfo info : infos) {
//            System.out.println(info);
//        }
        Assert.assertNotNull(infos);
    }

    @Test
    public void browserServer() throws Exception {
    }

}