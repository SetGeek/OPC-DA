package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

/**
 * OPC枚举范围
 * @author ck.yang
 */
public enum OpcEnumScope {
    /** 私有连接 */
    OPC_ENUM_PRIVATE_CONNECTIONS(1),
    /** 公开连接 */
    OPC_ENUM_PUBLIC_CONNECTIONS(2),
    /** 全部连接 */
    OPC_ENUM_ALL_CONNECTIONS(3),
    /** 私有 */
    OPC_ENUM_PRIVATE(4),
    /** 公开 */
    OPC_ENUM_PUBLIC(5),
    /** 全部 */
    OPC_ENUM_ALL(6),
    /** 未知 */
    OPC_ENUM_UNKNOWN(0);

    private int _id;

    private OpcEnumScope(final int id) {
        this._id = id;
    }

    public int id() {
        return this._id;
    }

    public static OpcEnumScope fromID(final int id) {
        switch (id) {
            case 1:
                return OPC_ENUM_PRIVATE_CONNECTIONS;
            case 2:
                return OPC_ENUM_PUBLIC_CONNECTIONS;
            case 3:
                return OPC_ENUM_ALL_CONNECTIONS;
            case 4:
                return OPC_ENUM_PRIVATE;
            case 5:
                return OPC_ENUM_PUBLIC;
            case 6:
                return OPC_ENUM_ALL;
            default:
                return OPC_ENUM_UNKNOWN;
        }
    }
}
