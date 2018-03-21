package cn.com.sgcc.gdt.opc.core.dcom.list.impl;

import java.net.UnknownHostException;

import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.EnumGUID;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.Helper;
import cn.com.sgcc.gdt.opc.core.dcom.list.ClassDetails;
import cn.com.sgcc.gdt.opc.core.dcom.list.Constants;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;

import rpc.core.UUID;

/**
 * OPC服务列表
 * @author ck.yang
 */
public class OPCServerList extends BaseCOMObject {
    /**
     * 获取服务列表
     * @param listObject
     * @throws JIException
     */
    public OPCServerList(final IJIComObject listObject) throws JIException {
        super(listObject.queryInterface(Constants.IOPCServerList_IID));
    }

    /**
     * 根据progId获取clsId
     * @param progId
     * @return
     * @throws JIException
     */
    public JIClsid getCLSIDFromProgID(final String progId) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        callObject.addInParamAsString(progId, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addOutParamAsType(UUID.class, JIFlags.FLAG_NULL);

        try {
            Object[] result = getCOMObject().call(callObject);
            return JIClsid.valueOf(((UUID) result[0]).toString());
        } catch (JIException e) {
            if (e.getErrorCode() == 0x800401F3) {
                return null;
            }
            throw e;
        }
    }

    /**
     * 返回服务器软件的信息
     *
     * @param clsId A server class
     * @throws JIException
     */
    public ClassDetails getClassDetails(final JIClsid clsId) throws JIException {
        if (clsId == null) {
            return null;
        }

        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);

        callObject.addInParamAsUUID(clsId.getCLSID(), JIFlags.FLAG_NULL);

        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);

        Object[] result = Helper.callRespectSFALSE(getCOMObject(), callObject);

        ClassDetails cd = new ClassDetails();
        cd.setClsId(clsId.getCLSID());
        cd.setProgId(((JIString) ((JIPointer) result[0]).getReferent()).getString());
        cd.setDescription(((JIString) ((JIPointer) result[1]).getReferent()).getString());

        return cd;
    }

    /*
     HRESULT EnumClassesOfCategories(
     [in]                       ULONG        cImplemented,
     [in,size_is(cImplemented)] CATID        rgcatidImpl[],
     [in]                       ULONG        cRequired,
     [in,size_is(cRequired)]    CATID        rgcatidReq[],
     [out]                      IEnumGUID ** ppenumClsid
     );
     */

    public EnumGUID enumClassesOfCategories(final String[] implemented, final String[] required) throws IllegalArgumentException, UnknownHostException, JIException {
        UUID[] u1 = new UUID[implemented.length];
        UUID[] u2 = new UUID[required.length];

        for (int i = 0; i < implemented.length; i++) {
            u1[i] = new UUID(implemented[i]);
        }

        for (int i = 0; i < required.length; i++) {
            u2[i] = new UUID(required[i]);
        }

        return enumClassesOfCategories(u1, u2);
    }

    public EnumGUID enumClassesOfCategories(final UUID[] implemented, final UUID[] required) throws IllegalArgumentException, UnknownHostException, JIException {
        // ** CALL
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        // ** IN
        callObject.addInParamAsInt(implemented.length, JIFlags.FLAG_NULL);
        if (implemented.length == 0) {
            callObject.addInParamAsPointer(new JIPointer(null), JIFlags.FLAG_NULL);
        } else {
            callObject.addInParamAsArray(new JIArray(implemented, true), JIFlags.FLAG_NULL);
        }

        callObject.addInParamAsInt(required.length, JIFlags.FLAG_NULL);
        if (required.length == 0) {
            callObject.addInParamAsPointer(new JIPointer(null), JIFlags.FLAG_NULL);
        } else {
            callObject.addInParamAsArray(new JIArray(required, true), JIFlags.FLAG_NULL);
        }

        // ** OUT
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);

        // ** RESULT
        Object result[] = Helper.callRespectSFALSE(getCOMObject(), callObject);

        return new EnumGUID((IJIComObject) result[0]);
    }
}
