package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

/**
 * OPC命名空间类型
 * @author ck.yang
 */
public enum OpcNamespaceType {
    /** 分级 */
    OPC_NS_HIERARCHIAL(1),
    /** 平级 */
    OPC_NS_FLAT(2),
    /** 未知 */
    OPC_NS_UNKNOWN(0);

    private int _id;

    private OpcNamespaceType(final int id) {
        this._id = id;
    }

    public int id() {
        return this._id;
    }

    public static OpcNamespaceType fromID(final int id) {
        switch (id) {
            case 1:
                return OPC_NS_HIERARCHIAL;
            case 2:
                return OPC_NS_FLAT;
            default:
                return OPC_NS_UNKNOWN;
        }
    }
}
