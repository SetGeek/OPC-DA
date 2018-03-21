package cn.com.sgcc.gdt.opc.core.dcom.da.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.Result;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.ResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.Constants;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcDatasource;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;

import java.net.UnknownHostException;

/**
 * OPC异步访问通道
 * @author ck.yang
 */
public class OPCAsyncIO2 extends BaseCOMObject {

    /**
     * 异步结果集
     * @author ck.yang
     */
    public class AsyncResult {
        /** 结果集 */
        private final ResultSet<Integer> result;
        /** 取消id */
        private final Integer cancelId;

        public AsyncResult() {
            super();
            this.result = new ResultSet<Integer>();
            this.cancelId = null;
        }

        public AsyncResult(final ResultSet<Integer> result, final Integer cancelId) {
            super();
            this.result = result;
            this.cancelId = cancelId;
        }

        public Integer getCancelId() {
            return this.cancelId;
        }

        public ResultSet<Integer> getResult() {
            return this.result;
        }
    }

    public OPCAsyncIO2(final IJIComObject opcAsyncIO2) throws IllegalArgumentException, UnknownHostException, JIException {
        super(opcAsyncIO2.queryInterface(Constants.IOPCAsyncIO2_IID));
    }

    public void setEnable(final boolean state) throws JIException {
        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);

        callObject.addInParamAsInt(state ? 1 : 0, JIFlags.FLAG_NULL);

        getCOMObject().call(callObject);
    }

    public int refresh(final OpcDatasource dataSource, final int transactionID) throws JIException {
        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        callObject.addInParamAsShort((short) dataSource.id(), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(transactionID, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);

        final Object result[] = getCOMObject().call(callObject);

        return (Integer) result[0];
    }

    public void cancel(final int cancelId) throws JIException {
        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);

        callObject.addInParamAsInt(cancelId, JIFlags.FLAG_NULL);

        getCOMObject().call(callObject);
    }

    public AsyncResult read(final int transactionId, final Integer... serverHandles) throws JIException {
        if (serverHandles == null || serverHandles.length == 0) {
            return new AsyncResult();
        }

        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(transactionId, JIFlags.FLAG_NULL);

        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        final Object[] result = getCOMObject().call(callObject);

        final Integer cancelId = (Integer) result[0];
        final Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();

        final ResultSet<Integer> resultSet = new ResultSet<Integer>();

        for (int i = 0; i < serverHandles.length; i++) {
            resultSet.add(new Result<Integer>(serverHandles[i], errorCodes[i]));
        }

        return new AsyncResult(resultSet, cancelId);
    }
}
