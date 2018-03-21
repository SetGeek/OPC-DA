package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

/**
 * OPC服务状态
 * @author ck.yang
 */
public enum OpcServerState {
    /** 运行 */
    OPC_STATUS_RUNNING(1),
    /** 失败 */
    OPC_STATUS_FAILED(2),
    /** 未配置 */
    OPC_STATUS_NOCONFIG(3),
    /** 暂停 */
    OPC_STATUS_SUSPENDED(4),
    /** 测试 */
    OPC_STATUS_TEST(5),
    /** 提交失败 */
    OPC_STATUS_COMM_FAULT(6),
    /** 未知 */
    OPC_STATUS_UNKNOWN(0);


    private int _id;

    private OpcServerState(final int id) {
        this._id = id;
    }

    public int id() {
        return this._id;
    }

    public static OpcServerState fromID(final int id) {
        switch (id) {
            case 1:
                return OPC_STATUS_RUNNING;
            case 2:
                return OPC_STATUS_FAILED;
            case 3:
                return OPC_STATUS_NOCONFIG;
            case 4:
                return OPC_STATUS_SUSPENDED;
            case 5:
                return OPC_STATUS_TEST;
            case 6:
                return OPC_STATUS_COMM_FAULT;
            default:
                return OPC_STATUS_UNKNOWN;
        }
    }
}
