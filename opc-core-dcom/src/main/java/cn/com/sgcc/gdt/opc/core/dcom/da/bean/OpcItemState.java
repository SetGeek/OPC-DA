package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.FILETIME;
import lombok.Data;
import lombok.ToString;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 * OPC数据项状态
 * @author ck.yang
 */
@Data
@ToString
public class OpcItemState {
    private int clientHandle = 0;

    private FILETIME timestamp = null;

    private short quality = 0;

    private short reserved = 0;

    private JIVariant value = null;


    /**
     * 转换为JIStruct
     * @return
     * @throws JIException
     */
    public static JIStruct getStruct() throws JIException {
        JIStruct struct = new JIStruct();

        struct.addMember(Integer.class);
        struct.addMember(FILETIME.getStruct());
        struct.addMember(Short.class);
        struct.addMember(Short.class);
        struct.addMember(JIVariant.class);

        return struct;
    }

    /**
     * 从JIStruct获取
     * @param struct
     * @return
     */
    public static OpcItemState fromStruct(final JIStruct struct) {
        OpcItemState itemState = new OpcItemState();

        itemState.setClientHandle((Integer) struct.getMember(0));
        itemState.setTimestamp(FILETIME.fromStruct((JIStruct) struct.getMember(1)));
        itemState.setQuality((Short) struct.getMember(2));
        itemState.setReserved((Short) struct.getMember(3));
        itemState.setValue((JIVariant) struct.getMember(4));

        return itemState;
    }
}
