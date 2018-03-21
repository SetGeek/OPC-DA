package cn.com.sgcc.gdt.opc.core.dcom.common.impl;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.Constants;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;

/**
 * OPC公共组件
 * @author ck.yang
 */
public class OPCCommon extends BaseCOMObject {
    public OPCCommon(final IJIComObject opcObject) throws IllegalArgumentException, UnknownHostException, JIException {
        super(opcObject.queryInterface(Constants.IOPCCommon_IID));
    }

    /**
     * 设置本地id
     * @param localeID
     * @throws JIException
     */
    public void setLocaleID(final int localeID) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        callObject.addInParamAsInt(localeID, JIFlags.FLAG_NULL);

        getCOMObject().call(callObject);
    }

    /**
     * 获取本地id
     * @return
     * @throws JIException
     */
    public int getLocaleID() throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);

        callObject.addOutParamAsObject(Integer.class, JIFlags.FLAG_NULL);

        Object[] result = getCOMObject().call(callObject);
        return (Integer) result[0];
    }

    /**
     * 获取错误信息
     * @param errorCode
     * @param localeID
     * @return
     * @throws JIException
     */
    public String getErrorString(final int errorCode, final int localeID) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);

        callObject.addInParamAsInt(errorCode, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(localeID, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);

        Object[] result = getCOMObject().call(callObject);
        return ((JIString) ((JIPointer) result[0]).getReferent()).getString();
    }

    /**
     * 设置客户端名称
     * @param clientName
     * @throws JIException
     */
    public void setClientName(final String clientName) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);

        callObject.addInParamAsString(clientName, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);

        getCOMObject().call(callObject);
    }

    /**
     * 查询可用的本地id
     * @return
     * @throws JIException
     */
    public Collection<Integer> queryAvailableLocaleIDs() throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        Object[] result = getCOMObject().call(callObject);

        JIArray resultArray = (JIArray) ((JIPointer) result[1]).getReferent();
        Integer[] intArray = (Integer[]) resultArray.getArrayInstance();

        return Arrays.asList(intArray);
    }

}
