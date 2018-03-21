package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

/**
 * OPC浏览方向
 * @author ck.yang
 */
public enum OpcBrowseDirection {
    /** 向上 */
    OPC_BROWSE_UP(1),
    /** 向下 */
    OPC_BROWSE_DOWN(2),
    /**  */
    OPC_BROWSE_TO(3),
    /** 未知 */
    OPC_BROWSE_UNKNOWN(0);

    private int _id;

    private OpcBrowseDirection(final int id) {
        this._id = id;
    }

    public int id() {
        return this._id;
    }

    public static OpcBrowseDirection fromID(final int id) {
        switch (id) {
            case 1:
                return OPC_BROWSE_UP;
            case 2:
                return OPC_BROWSE_DOWN;
            case 3:
                return OPC_BROWSE_TO;
            default:
                return OPC_BROWSE_UNKNOWN;
        }
    }
}
