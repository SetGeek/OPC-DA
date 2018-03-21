package cn.com.sgcc.gdt.opc.core.dcom.da.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResult;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.Result;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.ResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.Helper;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.Constants;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcDatasource;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcItemState;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.WriteRequest;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;

/**
 * OPC同步访问通道
 * @author ck.yang
 */
public class OPCSyncIO extends BaseCOMObject {
    public OPCSyncIO(final IJIComObject opcSyncIO) throws JIException {
        super(opcSyncIO.queryInterface(Constants.IOPCSyncIO_IID));
    }

    /**
     * 读取
     * @param source
     * @param serverHandles
     * @return
     * @throws JIException
     */
    public KeyedResultSet<Integer, OpcItemState> read(final OpcDatasource source, final Integer... serverHandles) throws JIException {
        if (serverHandles == null || serverHandles.length == 0) {
            return new KeyedResultSet<Integer, OpcItemState>();
        }

        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        callObject.addInParamAsShort((short) source.id(), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);

        callObject.addOutParamAsObject(new JIPointer(new JIArray(OpcItemState.getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        Object result[] = Helper.callRespectSFALSE(getCOMObject(), callObject);

        KeyedResultSet<Integer, OpcItemState> results = new KeyedResultSet<Integer, OpcItemState>();
        JIStruct[] states = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();

        for (int i = 0; i < serverHandles.length; i++) {
            results.add(new KeyedResult<Integer, OpcItemState>(serverHandles[i], OpcItemState.fromStruct(states[i]), errorCodes[i]));
        }

        return results;
    }

    /**
     * 写入
     * @param requests
     * @return
     * @throws JIException
     */
    public ResultSet<WriteRequest> write(final WriteRequest... requests) throws JIException {
        if (requests.length == 0) {
            return new ResultSet<>();
        }

        Integer[] items = new Integer[requests.length];
        JIVariant[] values = new JIVariant[requests.length];
        for (int i = 0; i < requests.length; i++) {
            items[i] = requests[i].getServerHandle();
            values[i] = Helper.fixVariant(requests[i].getValue());
        }

        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);

        callObject.addInParamAsInt(requests.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(items, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(values, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        Object result[] = Helper.callRespectSFALSE(getCOMObject(), callObject);

        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();

        ResultSet<WriteRequest> results = new ResultSet<>();
        for (int i = 0; i < requests.length; i++) {
            results.add(new Result<>(requests[i], errorCodes[i]));
        }
        return results;
    }
}
