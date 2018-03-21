package cn.com.sgcc.gdt.opc.core.dcom.da.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.EventHandler;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.Constants;
import cn.com.sgcc.gdt.opc.core.dcom.da.IOPCDataCallback;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcGroupState;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;
import org.jinterop.dcom.impls.JIObjectFactory;

import java.net.UnknownHostException;

/**
 * OPC组状态管理器
 * @author ck.yang
 */
public class OPCGroupStateMgt extends BaseCOMObject {
    public OPCGroupStateMgt(final IJIComObject opcGroup) throws IllegalArgumentException, UnknownHostException, JIException {
        super(opcGroup.queryInterface(Constants.IOPCGroupStateMgt_IID));
    }

    /**
     * 获取组状态
     * @return
     * @throws JIException
     */
    public OpcGroupState getState() throws JIException {
        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Boolean.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Float.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);

        final Object result[] = getCOMObject().call(callObject);

        final OpcGroupState state = new OpcGroupState();
        state.setUpdateRate((Integer) result[0]);
        state.setActive((Boolean) result[1]);
        state.setName(((JIString) ((JIPointer) result[2]).getReferent()).getString());
        state.setTimeBias((Integer) result[3]);
        state.setPercentDeadband((Float) result[4]);
        state.setLocaleID((Integer) result[5]);
        state.setClientHandle((Integer) result[6]);
        state.setServerHandle((Integer) result[7]);

        return state;
    }

    /**
     * 设置组状态，如果为空，将保持原有的状态
     * @param requestedUpdateRate
     * @param active
     * @param timeBias
     * @param percentDeadband
     * @param localeID
     * @param clientHandle
     * @return
     * @throws JIException
     */
    public int setState(final Integer requestedUpdateRate, final Boolean active, final Integer timeBias, final Float percentDeadband, final Integer localeID, final Integer clientHandle) throws JIException {
        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);

        callObject.addInParamAsPointer(new JIPointer(requestedUpdateRate), JIFlags.FLAG_NULL);
        if (active != null) {
            callObject.addInParamAsPointer(new JIPointer(Integer.valueOf(active.booleanValue() ? 1 : 0)), JIFlags.FLAG_NULL);
        } else {
            callObject.addInParamAsPointer(new JIPointer(null), JIFlags.FLAG_NULL);
        }
        callObject.addInParamAsPointer(new JIPointer(timeBias), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(percentDeadband), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(localeID), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(clientHandle), JIFlags.FLAG_NULL);

        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);

        final Object[] result = getCOMObject().call(callObject);

        return (Integer) result[0];
    }

    public OPCItemMgt getItemManagement() throws JIException {
        return new OPCItemMgt(getCOMObject());
    }

    /**
     * 设置组名
     * @param name
     * @throws JIException
     */
    public void setName(final String name) throws JIException {
        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);

        getCOMObject().call(callObject);
    }

    /**
     * 复制组
     * @param name
     * @return
     * @throws JIException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     */
    public OPCGroupStateMgt clone(final String name) throws JIException, IllegalArgumentException, UnknownHostException {
        final JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);

        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsUUID(Constants.IOPCGroupStateMgt_IID, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);

        final Object[] result = getCOMObject().call(callObject);
        return new OPCGroupStateMgt((IJIComObject) result[0]);
    }

    /**
     * 向组添加回调函数
     * @param callback
     * @return
     * @throws JIException
     */
    public EventHandler attach(final IOPCDataCallback callback) throws JIException {
        final OPCDataCallback callbackObject = new OPCDataCallback();

        callbackObject.setCallback(callback);

        // sync the callback object so that no calls get through the callback
        // until the callback information is set
        // If happens in some cases that the callback is triggered before
        // the method attachEventHandler returns.
        synchronized (callbackObject) {
            final String id = JIFrameworkHelper.attachEventHandler(getCOMObject(), Constants.IOPCDataCallback_IID, JIObjectFactory.buildObject(getCOMObject().getAssociatedSession(), callbackObject.getCoClass()));

            callbackObject.setInfo(getCOMObject(), id);
        }
        return callbackObject;
    }

    /**
     * 获取异步访问通道
     * @return
     */
    public OPCAsyncIO2 getAsyncIO2() {
        try {
            return new OPCAsyncIO2(getCOMObject());
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * 获取同步访问通道
     * @return
     */
    public OPCSyncIO getSyncIO() {
        try {
            return new OPCSyncIO(getCOMObject());
        } catch (final Exception e) {
            return null;
        }
    }
}
