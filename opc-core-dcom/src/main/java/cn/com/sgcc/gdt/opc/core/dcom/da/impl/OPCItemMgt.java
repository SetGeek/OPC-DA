package cn.com.sgcc.gdt.opc.core.dcom.da.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResult;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.Result;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.ResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.Helper;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcItemResult;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.Constants;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcItemDef;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;

/**
 * OPC数据项管理
 * @author ck.yang
 */
public class OPCItemMgt extends BaseCOMObject {
    public OPCItemMgt(final IJIComObject opcGroup) throws JIException {
        super(opcGroup.queryInterface(Constants.IOPCItemMgt_IID));
    }

    /**
     * 校验数据项
     * @param items
     * @return
     * @throws JIException
     */
    public KeyedResultSet<OpcItemDef, OpcItemResult> validate(final OpcItemDef... items) throws JIException {
        if (items.length == 0) {
            return new KeyedResultSet<OpcItemDef, OpcItemResult>();
        }

        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);

        final JIStruct struct[] = new JIStruct[items.length];
        for (int i = 0; i < items.length; i++) {
            struct[i] = items[i].toStruct();
        }
        final JIArray itemArray = new JIArray(struct, true);

        callObject.addInParamAsInt(items.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(itemArray, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(0, JIFlags.FLAG_NULL); // don't update blobs
        callObject.addOutParamAsObject(new JIPointer(new JIArray(OpcItemResult.getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        final Object result[] = Helper.callRespectSFALSE(getCOMObject(), callObject);

        final JIStruct[] results = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        final Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();

        final KeyedResultSet<OpcItemDef, OpcItemResult> resultList = new KeyedResultSet<OpcItemDef, OpcItemResult>(items.length);
        for (int i = 0; i < items.length; i++) {
            final OpcItemResult itemResult = OpcItemResult.fromStruct(results[i]);
            final KeyedResult<OpcItemDef, OpcItemResult> resultEntry = new KeyedResult<OpcItemDef, OpcItemResult>(items[i], itemResult, errorCodes[i]);
            resultList.add(resultEntry);
        }

        return resultList;
    }

    /**
     * 添加数据项
     * @param items
     * @return
     * @throws JIException
     */
    public KeyedResultSet<OpcItemDef, OpcItemResult> add(final OpcItemDef... items) throws JIException {
        if (items.length == 0) {
            return new KeyedResultSet<OpcItemDef, OpcItemResult>();
        }

        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        final JIStruct struct[] = new JIStruct[items.length];
        for (int i = 0; i < items.length; i++) {
            struct[i] = items[i].toStruct();
        }
        final JIArray itemArray = new JIArray(struct, true);

        callObject.addInParamAsInt(items.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(itemArray, JIFlags.FLAG_NULL);

        /*
        callObject.addOutParamAsObject ( new JIPointer ( new JIArray ( OpcItemResult.getStruct (), null, 1, true ) ),
                JIFlags.FLAG_NULL );
        callObject.addOutParamAsObject ( new JIPointer ( new JIArray ( Integer.class, null, 1, true ) ),
                JIFlags.FLAG_NULL );
                */
        callObject.addOutParamAsObject(new JIPointer(new JIArray(OpcItemResult.getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        final Object result[] = Helper.callRespectSFALSE(getCOMObject(), callObject);

        final JIStruct[] results = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        final Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();

        final KeyedResultSet<OpcItemDef, OpcItemResult> resultList = new KeyedResultSet<OpcItemDef, OpcItemResult>(items.length);
        for (int i = 0; i < items.length; i++) {
            final OpcItemResult itemResult = OpcItemResult.fromStruct(results[i]);
            final KeyedResult<OpcItemDef, OpcItemResult> resultEntry = new KeyedResult<OpcItemDef, OpcItemResult>(items[i], itemResult, errorCodes[i]);
            resultList.add(resultEntry);
        }

        return resultList;
    }

    /**
     * 移除服务处理
     * @param serverHandles
     * @return
     * @throws JIException
     */
    public ResultSet<Integer> remove(final Integer... serverHandles) throws JIException {
        if (serverHandles.length == 0) {
            return new ResultSet<Integer>();
        }

        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        final Object result[] = Helper.callRespectSFALSE(getCOMObject(), callObject);

        final Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        final ResultSet<Integer> results = new ResultSet<Integer>(serverHandles.length);
        for (int i = 0; i < serverHandles.length; i++) {
            results.add(new Result<Integer>(serverHandles[i], errorCodes[i]));
        }
        return results;
    }

    /**
     * 设置激活状态
     * @param state
     * @param items
     * @return
     * @throws JIException
     */
    public ResultSet<Integer> setActiveState(final boolean state, final Integer... items) throws JIException {
        if (items.length == 0) {
            return new ResultSet<Integer>();
        }

        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);

        callObject.addInParamAsInt(items.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(items, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(state ? 1 : 0, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        final Object[] result = Helper.callRespectSFALSE(getCOMObject(), callObject);

        final Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        final ResultSet<Integer> results = new ResultSet<Integer>(items.length);
        for (int i = 0; i < items.length; i++) {
            results.add(new Result<Integer>(items[i], errorCodes[i]));
        }
        return results;
    }

    /**
     * 设置客户端处理
     * @param serverHandles
     * @param clientHandles
     * @return
     * @throws JIException
     */
    public ResultSet<Integer> setClientHandles(final Integer[] serverHandles, final Integer[] clientHandles) throws JIException {
        if (serverHandles.length != clientHandles.length) {
            throw new JIException(0, "数组大小不匹配，即服务处理器与客户端处理器的个数不一致！");
        }
        if (serverHandles.length == 0) {
            return new ResultSet<>();
        }

        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);

        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(clientHandles, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        final Object[] result = Helper.callRespectSFALSE(getCOMObject(), callObject);

        final Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        final ResultSet<Integer> results = new ResultSet<Integer>(serverHandles.length);
        for (int i = 0; i < serverHandles.length; i++) {
            results.add(new Result<>(serverHandles[i], errorCodes[i]));
        }
        return results;
    }

}
