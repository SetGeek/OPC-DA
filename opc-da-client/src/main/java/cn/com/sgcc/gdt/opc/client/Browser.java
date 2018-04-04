package cn.com.sgcc.gdt.opc.client;

import cn.com.sgcc.gdt.opc.client.bean.DataItem;
import cn.com.sgcc.gdt.opc.client.bean.ServerInfo;
import cn.com.sgcc.gdt.opc.client.utils.DateUtil;
import cn.com.sgcc.gdt.opc.client.utils.JiVariantUtil;
import cn.com.sgcc.gdt.opc.core.dcom.list.ClassDetails;
import cn.com.sgcc.gdt.opc.lib.common.NotConnectedException;
import cn.com.sgcc.gdt.opc.lib.da.Group;
import cn.com.sgcc.gdt.opc.lib.da.Item;
import cn.com.sgcc.gdt.opc.lib.da.ItemState;
import cn.com.sgcc.gdt.opc.lib.da.Server;
import cn.com.sgcc.gdt.opc.lib.da.exception.AddFailedException;
import cn.com.sgcc.gdt.opc.lib.da.exception.DuplicateGroupException;
import cn.com.sgcc.gdt.opc.lib.list.Categories;
import cn.com.sgcc.gdt.opc.lib.list.Category;
import cn.com.sgcc.gdt.opc.lib.list.ServerList;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端浏览
 *
 * @author ck.yang
 */
@Slf4j
public class Browser {

    private Browser(){}

    public static List<DataItem> readSync(Server server, Collection<String> itemIds){
        //TODO 同步读取数据
        try {
            Group group = server.addGroup();
            Map<String, Item> itemMap = group.addItems(itemIds.toArray(new String[0]));
            List<DataItem> result = new ArrayList<>();
            for(Map.Entry<String, Item> entry: itemMap.entrySet()){
                Item item = entry.getValue();
                ItemState itemState = item.read(false);
                DataItem dataItem = JiVariantUtil.parseValue(item.getId(), itemState);
                result.add(dataItem);
            }
//            group.clear();
//            server.removeGroup(group,false);
            return result;
        } catch (Exception e) {
            log.error("同步读取失败！", e);
            return null;
        }
    }

    public static List<DataItem> readSync(Server server){
        try {
            return readSync(server, browseItemIds(server));
        } catch (Throwable throwable) {
            log.error("同步读取失败！", throwable);
            return null;
        }
    }
    /**
     * 异步读取数据（可指定节点）
     * @param server OPC服务
     * @param itemIds 节点编号集合
     * @param threadPool 线程池
     * @param heartBeat 心跳时间：小于0表示只接收一次，大于0则表示循环接收
     * @param dataCallback 数据接收后的回调处理
     * @throws Throwable
     */
    public static void readAsyn(Server server, Collection<String> itemIds, ScheduledExecutorService threadPool, long heartBeat,DataCallback dataCallback) throws Throwable {
        Group group = server.addGroup();
        Map<String, Item> items = group.addItems(itemIds.toArray(new String[0]));

        Runnable runnable = ()->{
            try {
                List<DataItem> dataList = new ArrayList<>();
                for(Map.Entry<String, Item> entry: items.entrySet()){
                    ItemState read = entry.getValue().read(false);
                    //转换格式并添加到结果
                    dataList.add(JiVariantUtil.parseValue(entry.getKey(), read));
                }
                //数据处理器回调
                dataCallback.process(dataList);
            } catch (Throwable e) {
                log.error("读取数据时发生异常！", e);
            }

        };
        //如果心跳时间为 -1，则表示不循环查询
        if(heartBeat <= 0L){
            threadPool.submit(runnable);
        }else{
            threadPool.scheduleAtFixedRate(runnable, 1,heartBeat, TimeUnit.MILLISECONDS);
        }

    }

    /**
     * 异步读取数据（查询所有节点,重复查询）<br>
     * 该方法仅调用 {@link Browser#readAsyn(Server, Collection, ScheduledExecutorService, long, DataCallback)}
     * @param server OPC服务
     * @param threadPool 线程池
     * @param heartBeat 重复查询的心跳时间
     * @param dataCallback 接收到数据后的回调处理
     * @throws Throwable
     */
    public static void readAsyn(Server server, ScheduledExecutorService threadPool,long heartBeat, DataCallback dataCallback) throws Throwable {
        readAsyn(server, browseItemIds(server), threadPool, heartBeat, dataCallback);
    }

    /**
     * 异步读取数据（查询所有节点,只查询一次）<br>
     * 该方法仅调用 {@link Browser#readAsyn(Server, Collection, ScheduledExecutorService, long, DataCallback)}
     * @param server OPC服务
     * @param threadPool 线程池
     * @param dataCallback 接收到数据后的回调处理
     * @throws Throwable
     */
    public static void readAsyn(Server server, ScheduledExecutorService threadPool, DataCallback dataCallback) throws Throwable {
        readAsyn(server, browseItemIds(server), threadPool, -1L, dataCallback);
    }

    public static void subscibe(Server server){
        //TODO 订阅指定数据，只有数据改变才会触发

    }

    /**
     * 获取所有节点的编号
     * @param server
     * @return
     * @throws Throwable
     */
    public static Collection<String> browseItemIds(Server server) throws Throwable{
        Collection<String> nodeIds = server.getFlatBrowser().browse();
        return nodeIds;
    }

    /**
     * 罗列出目标主机上的OPC服务器软件
     * @param host
     * @param domain
     * @param userName
     * @param password
     * @return
     * @throws Throwable
     */
    public static List<ServerInfo> listServer(String host, String domain, String userName, String password) throws Throwable {
        ServerList serverList = new ServerList(host, userName, password, domain);
        Collection<ClassDetails> classDetails = serverList.listServersWithDetails(new Category[]{Categories.OPCDAServer20}, new Category[]{});
        List<ServerInfo> serverInfos = new ArrayList<>();
        System.out.println("在目标主机上发现如下OPC服务器：");
        for(ClassDetails details: classDetails){
            serverInfos.add(new ServerInfo(details.getProgId(), details.getClsId(), details.getDescription()));
            System.out.format("\tprogId: '%s' \r\n\tclsId：'%s' \r\n\tdescription:'%s' \r\n\r\n", details.getProgId(), details.getClsId(), details.getClsId());
        }
        return serverInfos;
    }

    public static void browserServer(){
        //TODO 获取服务器的基本信息
    }

    /**
     * 处理结果数据的回调
     */
    public interface DataCallback{
        /**
         * 数据处理
         * @param dataItems
         * @throws Throwable
         */
        void process(List<DataItem> dataItems) throws Throwable;
    }
}
