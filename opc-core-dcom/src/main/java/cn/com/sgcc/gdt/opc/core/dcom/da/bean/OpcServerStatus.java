package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.FILETIME;
import lombok.Data;
import lombok.ToString;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;

/**
 * OPC服务状态
 * @author ck.yang
 */
@Data
@ToString
public class OpcServerStatus {
    private FILETIME startTime = null;

    private FILETIME currentTime = null;

    private FILETIME lastUpdateTime = null;

    private OpcServerState serverState = null;

    private int groupCount = -1;

    private int bandWidth = -1;

    private short majorVersion = -1;

    private short minorVersion = -1;

    private short buildNumber = -1;

    private short reserved = 0;

    private String vendorInfo = null;


    /**
     * 转换为JIStruct
     * @return
     * @throws JIException
     */
    public static JIStruct getStruct() throws JIException {
        JIStruct struct = new JIStruct();

        struct.addMember(FILETIME.getStruct());
        struct.addMember(FILETIME.getStruct());
        struct.addMember(FILETIME.getStruct());
        struct.addMember(Short.class); // enum: OpcServerState
        struct.addMember(Integer.class);
        struct.addMember(Integer.class);
        struct.addMember(Short.class);
        struct.addMember(Short.class);
        struct.addMember(Short.class);
        struct.addMember(Short.class);
        struct.addMember(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)));

        return struct;
    }

    /**
     * 从JIStruct中取值
     * @param struct
     * @return
     */
    public static OpcServerStatus fromStruct(final JIStruct struct) {
        OpcServerStatus status = new OpcServerStatus();

        status.startTime = FILETIME.fromStruct((JIStruct) struct.getMember(0));
        status.currentTime = FILETIME.fromStruct((JIStruct) struct.getMember(1));
        status.lastUpdateTime = FILETIME.fromStruct((JIStruct) struct.getMember(2));

        status.serverState = OpcServerState.fromID((Short) struct.getMember(3));
        status.groupCount = (Integer) struct.getMember(4);
        status.bandWidth = (Integer) struct.getMember(5);
        status.majorVersion = (Short) struct.getMember(6);
        status.minorVersion = (Short) struct.getMember(7);
        status.buildNumber = (Short) struct.getMember(8);
        status.reserved = (Short) struct.getMember(9);
        status.vendorInfo = ((JIString) ((JIPointer) struct.getMember(10)).getReferent()).getString();

        return status;
    }
}
