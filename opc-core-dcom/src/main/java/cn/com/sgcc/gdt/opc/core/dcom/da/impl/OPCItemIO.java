package cn.com.sgcc.gdt.opc.core.dcom.da.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.bean.FILETIME;
import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.Constants;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.IORequest;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;

import java.net.UnknownHostException;

/**
 * OPC数据项传输
 * @author ck.yang
 */
public class OPCItemIO extends BaseCOMObject {

    public OPCItemIO(final IJIComObject opcItemIO) throws IllegalArgumentException, UnknownHostException, JIException {
        super(opcItemIO.queryInterface(Constants.IOPCItemIO_IID));
    }

    /**
     * 读取数据
     * @param requests
     * @throws JIException
     */
    public void read(final IORequest[] requests) throws JIException {
        if (requests.length == 0) {
            return;
        }

        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);

        JIString itemIDs[] = new JIString[requests.length];
        Integer maxAges[] = new Integer[requests.length];
        for (int i = 0; i < requests.length; i++) {
            itemIDs[i] = new JIString(requests[i].getItemID(), JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
            maxAges[i] = new Integer(requests[i].getMaxAge());
        }

        callObject.addInParamAsInt(requests.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(itemIDs, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(maxAges, true), JIFlags.FLAG_NULL);

        callObject.addOutParamAsObject(new JIPointer(new JIArray(JIVariant.class, null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(FILETIME.getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        getCOMObject().call(callObject);
    }
}
