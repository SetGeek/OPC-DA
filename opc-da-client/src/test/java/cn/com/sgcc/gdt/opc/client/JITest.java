package cn.com.sgcc.gdt.opc.client;

import cn.com.sgcc.gdt.opc.core.dcom.common.impl.EnumString;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcEnumScope;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCGroupStateMgt;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCServer;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JISession;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author ck
 */
public class JITest {

    @Test
    public void testConnect() throws Exception {
        JISystem.setAutoRegisteration(true);

        /**
         * Session获取
         */
        JISession session = JISession.createSession("192.168.2.254", "Administrator","GDTvm6.5");

//        final JIComServer comServer = new JIComServer(JIClsid.valueOf("7bc0cc8e-482c-47ca-abdc-0fe7f9c6e729"), "192.168.2.254",session);
        final JIComServer comServer = new JIComServer(JIClsid.valueOf("b57c679b-665d-4bb0-9848-c5f2c4a6a280"), "192.168.2.254",session);
//        final JIComServer comServer = new JIComServer(JIClsid.valueOf("a879768a-7387-11d4-b0d8-009027242c59"), "192.168.2.254",session);

        final IJIComObject serverObject = comServer.createInstance();

        OPCServer server = new OPCServer(serverObject);

        /**
         * 添加一个Group的信息
         */
        OPCGroupStateMgt group = server.addGroup("test", true, 100, 1234, 60,
                0.0f, 1033);

        EnumString groups = server.getGroups(OpcEnumScope.OPC_ENUM_ALL);
        for (final String s : groups.asCollection()) {
            System.out.println("Group: " + s);
        }

        TimeUnit.SECONDS.sleep(3);

        // clean up
        server.removeGroup(group, true);
        JISession.destroySession(session);
        TimeUnit.SECONDS.sleep(3);
        group=null;
        server=null;
        session = null;
        System.gc();


    }

    @Test
    public void TestWMI() throws Exception {
    }

    /**
     * 通过Scope查找并遍历Groups的信息
     *
     * @param server
     * @param scope
     */
//    public static void enumerateGroups(OPCServer server,
//                                       OPCENUMSCOPE scope) throws Throwable{
//        System.out.println("Enum Groups: " + scope.toString());
//
//        for (final String group : server.getGroups(scope).asCollection()) {
//            System.out.println("Group: " + group);
//        }
//    }
}
