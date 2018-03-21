package cn.com.sgcc.gdt.opc.core.dcom.da.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.EnumString;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.Helper;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.OPCCommon;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcServerStatus;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.Constants;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcEnumScope;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;

import java.net.UnknownHostException;

/**
 * OPC服务
 * @author ck.yang
 */
public class OPCServer extends BaseCOMObject {
    public OPCServer(final IJIComObject opcServer) throws IllegalArgumentException, UnknownHostException, JIException {
        super(opcServer.queryInterface(Constants.IOPCServer_IID));
    }

    /**
     * Retrieve the current server status
     *
     * @return the current server status
     * @throws JIException
     */
    public OpcServerStatus getStatus() throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);

        callObject.addOutParamAsObject(new JIPointer(OpcServerStatus.getStruct()), JIFlags.FLAG_NULL);

        Object[] result = getCOMObject().call(callObject);

        return OpcServerStatus.fromStruct((JIStruct) ((JIPointer) result[0]).getReferent());
    }

    /**
     * 添加组
     * @param name
     * @param active 激活
     * @param updateRate 更新频率
     * @param clientHandle 客户端处理器
     * @param timeBias 时间偏差
     * @param percentDeadband 死区的百分比 FIXME 不理解
     * @param localeID 本地id
     * @return
     * @throws JIException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     */
    public OPCGroupStateMgt addGroup(final String name, final boolean active, final int updateRate, final int clientHandle, final Integer timeBias, final Float percentDeadband, final int localeID) throws JIException, IllegalArgumentException, UnknownHostException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsInt(active ? 1 : 0, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(updateRate, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(clientHandle, JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(timeBias), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(percentDeadband), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(localeID, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addInParamAsUUID(Constants.IOPCGroupStateMgt_IID, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);

        Object[] result = getCOMObject().call(callObject);

        return new OPCGroupStateMgt((IJIComObject) result[2]);
    }

    /**
     * 移除组
     * @param serverHandle
     * @param force
     * @throws JIException
     */
    public void removeGroup(final int serverHandle, final boolean force) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);

        callObject.addInParamAsInt(serverHandle, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(force ? 1 : 0, JIFlags.FLAG_NULL);

        getCOMObject().call(callObject);
    }

    /**
     * 移除组
     * @param group
     * @param force
     * @throws JIException
     */
    public void removeGroup(final OPCGroupStateMgt group, final boolean force) throws JIException {
        removeGroup(group.getState().getServerHandle(), force);
    }

    /**
     * 根据组名获取组管理器
     * @param name
     * @return
     * @throws JIException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     */
    public OPCGroupStateMgt getGroupByName(final String name) throws JIException, IllegalArgumentException, UnknownHostException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsUUID(Constants.IOPCGroupStateMgt_IID, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);

        Object[] result = getCOMObject().call(callObject);

        return new OPCGroupStateMgt((IJIComObject) result[0]);
    }

    /**
     * 获取所有组
     * @param scope 数据项类型范围
     * @return
     * @throws JIException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     */
    public EnumString getGroups(final OpcEnumScope scope) throws JIException, IllegalArgumentException, UnknownHostException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(5);

        callObject.addInParamAsShort((short) scope.id(), JIFlags.FLAG_NULL);
        callObject.addInParamAsUUID(cn.com.sgcc.gdt.opc.core.dcom.common.bean.Constants.IEnumString_IID, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);

        Object[] result = Helper.callRespectSFALSE(getCOMObject(), callObject);

        return new EnumString((IJIComObject) result[0]);
    }

    public OPCItemProperties getItemPropertiesService() {
        try {
            return new OPCItemProperties(getCOMObject());
        } catch (Exception e) {
            return null;
        }
    }

    public OPCItemIO getItemIOService() {
        try {
            return new OPCItemIO(getCOMObject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the browser object (<code>IOPCBrowseServerAddressSpace</code>) from the server instance
     *
     * @return the browser object
     */
    public OPCBrowseServerAddressSpace getBrowser() {
        try {
            return new OPCBrowseServerAddressSpace(getCOMObject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the common interface if supported
     *
     * @return the common interface or <code>null</code> if it is not supported
     */
    public OPCCommon getCommon() {
        try {
            return new OPCCommon(getCOMObject());
        } catch (Exception e) {
            return null;
        }
    }
}
