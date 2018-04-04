package cn.com.sgcc.gdt.opc.client;

import cn.com.sgcc.gdt.opc.client.bean.DataItem;
import cn.com.sgcc.gdt.opc.client.utils.JiVariantUtil;
import cn.com.sgcc.gdt.opc.lib.common.AlreadyConnectedException;
import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;
import cn.com.sgcc.gdt.opc.lib.common.NotConnectedException;
import cn.com.sgcc.gdt.opc.lib.da.*;
import cn.com.sgcc.gdt.opc.lib.da.browser.Access;
import cn.com.sgcc.gdt.opc.lib.da.browser.Branch;
import cn.com.sgcc.gdt.opc.lib.da.browser.Leaf;
import cn.com.sgcc.gdt.opc.lib.da.browser.TreeBrowser;
import cn.com.sgcc.gdt.opc.lib.da.exception.AddFailedException;
import cn.com.sgcc.gdt.opc.lib.da.exception.DuplicateGroupException;
import com.alibaba.fastjson.JSONArray;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author ck.yang
 */
public class DemoTest {

    Server server;
    ScheduledExecutorService pool;
    AutoReconnectController autoReconnCtrl;

    @Before
    public void init() throws AlreadyConnectedException, JIException, UnknownHostException, InterruptedException {
        pool = Executors.newScheduledThreadPool(1, (r) -> {
            Thread thread = new Thread(r);
            thread.setName("客户端-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
        ConnectionInformation connInfo = new ConnectionInformation();
        connInfo.setHost("192.168.2.254");
        connInfo.setUser("Administrator");
        connInfo.setPassword("GDTvm6.5");
        connInfo.setClsId("7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729");//kepware
//        connInfo.setClsid("B57C679B-665D-4BB0-9848-C5F2C4A6A280");//ks.demo
//        connInfo.setClsid("A879768C-7387-11D4-B0D8-009027242C59");//simulator

//        connInfo.setProgId("ICONICS.SimulatorOPCDA.2");//simulator
        server = new Server(connInfo, pool);
        server.connect();
//        autoReconnCtrl = new AutoReconnectController(server);
//        autoReconnCtrl.addListener(new AutoReconnectListener() {
//            @Override
//            public void stateChanged(AutoReconnectState state) {
//                System.out.println("======================== 状态变化！========================");
//                //DISABLED、CONNECTED
//                System.out.format("\r\n当前状态：%s\r\n\r\n",state.name());
//            }
//        });
//        autoReconnCtrl.connect();


//        server.connect();
    }

    @Test
    public void testAsyn() throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException, AddFailedException, InterruptedException, ExecutionException {
        //加载节点列表
        Collection<String> nodeIds = server.getFlatBrowser().browse();
        Group group = server.addGroup();
        group.setActive(true);
        Map<String, Item> items = group.addItems(nodeIds.toArray(new String[0]));
        pool.scheduleAtFixedRate(()->{
            long start = System.currentTimeMillis();

            List<DataItem> dataList = new ArrayList<>();
            try {
                items.forEach((itemId,item)-> {
                    try {
                        ItemState read = item.read(false);
                        //转换格式并添加到结果
                        dataList.add(JiVariantUtil.parseValue(itemId, read));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            long runTime = System.currentTimeMillis() - start;

            System.out.println("读取结果：");
            System.out.println("时间："+runTime);
            System.out.println("结果:"+ JSONArray.toJSONString(dataList));

        }, 1, 1, TimeUnit.SECONDS).get();


//        TimeUnit.SECONDS.sleep(10);
        group.setActive(false);
        group.clear();
        group.remove();
    }

    @Test
    public void testSync2() throws Exception {
        //加载节点列表
        Collection<String> nodeIds = server.getFlatBrowser().browse();

//        SyncAccess access = new SyncAccess(server, 1);
        Group group = server.addGroup();
        Map<String, Item> itemMap = group.addItems(nodeIds.toArray(new String[0]));
        List<DataItem> result = new ArrayList<>();
        for(Map.Entry<String, Item> entry: itemMap.entrySet()){
            Item item = entry.getValue();
            ItemState itemState = item.read(true);
            DataItem dataItem = JiVariantUtil.parseValue(item.getId(), itemState);
            result.add(dataItem);
        }
//        group.setActive(true);
        group.clear();
        server.removeGroup(group,false);
        System.out.println(result);
        TimeUnit.SECONDS.sleep(10);
//        group.setActive(false);

        group.clear();
    }

    /**
     * 同步读取
     * @throws Exception
     */
    @Test
    public void testSync() throws Exception {
        //加载节点列表
        Collection<String> nodeIds = server.getFlatBrowser().browse();

        SyncAccess access = new SyncAccess(server, 1);

        Map<String, DataItem> data = new HashMap<>();
        DataCallback dataCallback = (item, itemState)->{
            try {
                DataItem dataItem = JiVariantUtil.parseValue(item.getId(), itemState);
                data.put(item.getId(), dataItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for(String nodeId: nodeIds){
            access.addItem(nodeId, dataCallback);
        }
        access.bind();
        pool.scheduleAtFixedRate(()->{
            System.out.println("结果数据：" + data.values());
        }, 1, 3, TimeUnit.SECONDS).get();
    }

    @Test
    public void testGetNodes() throws Exception {

        Collection<String> sys = server.getFlatBrowser().browse();

        List<String> collect = sys.stream().filter(value -> value.startsWith("") && !value.contains("")).collect(Collectors.toList());
        for (String id : collect){
                System.out.println(id);
        }

    }

    /**
     * 订阅
     * @throws Exception
     */
    @Test
    public void testSub() throws Exception {

    }

    @After
    public void destroy() throws InterruptedException {
        pool.awaitTermination(5, TimeUnit.SECONDS);
        server.dispose();
//        autoReconnCtrl.disconnect();
//        server.disconnect();
//        server.getScheduler().shutdownNow();
    }

    @Test
    public void testWrite() throws Throwable{

        Group group = server.addGroup();
        Item item1 = group.addItem("channel1.device1.group1.tag1");//org.jinterop.dcom.core.JIUnsignedShort
        Item item2 = group.addItem("channel1.device1.group1.tag2");//org.jinterop.dcom.core.JIString
        Item item3 = group.addItem("testChannel1.device1.tag1");//java.lang.Double
        Item item4 = group.addItem("testChannel1.device1.tag2");//org.jinterop.dcom.core.JIUnsignedShort

//        ItemState data1 = item1.read(true);
//        ItemState data2 = item2.read(true);
//        ItemState data3 = item3.read(true);
//        ItemState data4 = item4.read(true);

//        System.out.println(data1.getValue().getObject().getClass().getName());
//        System.out.println(data2.getValue().getObject().getClass().getName());
//        System.out.println(data3.getValue().getObject().getClass().getName());
//        System.out.println(data4.getValue().getObject().getClass().getName());


//        JICallBuilder builder = new JICallBuilder();
//        builder.addInParamAsString("testfdas", 0);
//        JIVariant variant = builder.getResultAsVariantAt(0);
//        item1.write(new JIVariant(new JIString("tewstsfdaf")));
        item2.write(new JIVariant("1111"));

//        JIString jiString = new JIString("zhong");
//        JIVariant jiVariant = new JIVariant(jiString, true);
//        Integer write = item.write(jiVariant);
//        TimeUnit.SECONDS.sleep(5);
//        System.out.println("添加效果：" + write);
//
//        ItemState read = item.read(true);
//
//        Object res = read.getValue().getObject();
//        System.out.println("添加后："+res);

    }


    private void getJiVariant(JIVariant jiVariant, Object value) throws JIException {
        JIVariant result;
        Object oldValue = jiVariant.getObject();
        String typeName = oldValue.getClass().getTypeName();
        if(typeName.startsWith("java.lang") || typeName.startsWith("java.util")){
//            new JIVariant()
        }else if(oldValue instanceof JIArray){

        }else if(oldValue instanceof IJIUnsigned){

        }else if(oldValue instanceof IJIComObject){

        }else if(oldValue instanceof JIString){

        }else if(oldValue instanceof JIVariant){

        }else{
        }

    }
}