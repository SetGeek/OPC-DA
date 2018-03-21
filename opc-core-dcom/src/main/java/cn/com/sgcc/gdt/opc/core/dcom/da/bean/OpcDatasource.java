package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

/**
 * OPC数据源
 * @author ck.yang
 */
public enum OpcDatasource {
    /**
     * 缓存数据源
     */
    OPC_DS_CACHE(1),
    /**
     * 设备/驱动数据源
     */
    OPC_DS_DEVICE(2),
    /**
     * 未知数据源
     */
    OPC_DS_UNKNOWN(0);

    private int _id;

    private OpcDatasource(final int id) {
        this._id = id;
    }

    public int id() {
        return this._id;
    }

    /**
     * 根据编号获取数据类型
     * @param id
     * @return
     */
    public static OpcDatasource fromID(final int id) {
        switch (id){
            case 1: return OPC_DS_CACHE;
            case 2: return OPC_DS_DEVICE;
            default: return OPC_DS_UNKNOWN;
        }
    }
}
