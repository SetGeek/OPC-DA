package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import lombok.Data;
import lombok.ToString;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 * OPC数据项定义
 * @author ck.yang
 */
@Data
@ToString
public class OpcItemDef {

    /** 通道路径 */
    private String accessPath = "";
    /** 数据项编号 */
    private String itemID = "";
    /** 激活状态 */
    private boolean active = true;
    /** 客户端操作编号 */
    private int clientHandle;
    /** 请求数据类型 */
    private short requestedDataType = JIVariant.VT_EMPTY;
    /** 保留 */
    private short reserved;


    /**
     * 格式化为J-Interop的JIStruct类型
     *
     * @return the j-interop structe
     * @throws JIException
     */
    public JIStruct toStruct() throws JIException {
        final JIStruct struct = new JIStruct();
        struct.addMember(new JIString(getAccessPath(), JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
        struct.addMember(new JIString(getItemID(), JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
        struct.addMember(new Integer(isActive() ? 1 : 0));
        struct.addMember(Integer.valueOf(getClientHandle()));

        struct.addMember(Integer.valueOf(0)); // blob size
        struct.addMember(new JIPointer(null)); // blob

        struct.addMember(Short.valueOf(getRequestedDataType()));
        struct.addMember(Short.valueOf(getReserved()));
        return struct;
    }
}
