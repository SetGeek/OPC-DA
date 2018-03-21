package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import lombok.Data;
import lombok.ToString;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 * OPC数据项结果
 * @author ck.yang
 */
@Data
@ToString
public class OpcItemResult {
    /** 服务处理器编号 */
    private int serverHandle = 0;
    /** 规范的数据类型 */
    private short canonicalDataType = JIVariant.VT_EMPTY;
    /** 保留 */
    private short reserved = 0;
    /** 访问权限 */
    private int accessRights = 0;

    /**
     * 格式化为JIStruct
     * @return
     * @throws JIException
     */
    public static JIStruct getStruct() throws JIException {
        JIStruct struct = new JIStruct();

        struct.addMember(Integer.class); // Server handle
        struct.addMember(Short.class); // data type
        struct.addMember(Short.class); // reserved
        struct.addMember(Integer.class); // access rights
        struct.addMember(Integer.class); // blob size
        // grab the normally unused byte array
        struct.addMember(new JIPointer(new JIArray(Byte.class, null, 1, true, false)));

        return struct;
    }

    /**
     * 将JIStruct转换为OpcItemResult
     * @param struct
     * @return
     */
    public static OpcItemResult fromStruct(final JIStruct struct) {
        OpcItemResult result = new OpcItemResult();

        result.setServerHandle(new Integer((Integer) struct.getMember(0)));
        result.setCanonicalDataType(new Short((Short) struct.getMember(1)));
        result.setReserved(new Short((Short) struct.getMember(2)));
        result.setAccessRights(new Integer((Integer) struct.getMember(3)));

        return result;
    }
}
