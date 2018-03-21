package cn.com.sgcc.gdt.opc.core.dcom.da;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.ResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.ValueData;

/**
 * OPC数据回调函数
 * @author ck.yang
 */
public interface IOPCDataCallback {
    public void dataChange(int transactionId, int serverGroupHandle, int masterQuality, int masterErrorCode, KeyedResultSet<Integer, ValueData> result);

    public void readComplete(int transactionId, int serverGroupHandle, int masterQuality, int masterErrorCode, KeyedResultSet<Integer, ValueData> result);

    public void writeComplete(int transactionId, int serverGroupHandle, int masterErrorCode, ResultSet<Integer> result);

    public void cancelComplete(int transactionId, int serverGroupHandle);
}
