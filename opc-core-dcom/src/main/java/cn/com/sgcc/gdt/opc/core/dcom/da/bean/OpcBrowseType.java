package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

/**
 * OPC浏览类型
 * @author ck.yang
 */
public enum OpcBrowseType {
    /** 分支 */
    OPC_BRANCH(1),
    /** 叶子 */
    OPC_LEAF(2),
    /** 平行 */
    OPC_FLAT(3),
    /** 未知类型 */
    OPC_UNKNOWN(0);

    private int _id;

    private OpcBrowseType(final int id) {
        this._id = id;
    }

    public int id() {
        return this._id;
    }

    /**
     * 根据编号获取类型
     * @param id
     * @return
     */
    public static OpcBrowseType fromID(final int id) {
        switch (id) {
            case 1: return OPC_BRANCH;
            case 2: return OPC_LEAF;
            case 3: return OPC_FLAT;
            default: return OPC_UNKNOWN;
        }
    }
}
