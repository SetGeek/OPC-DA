package cn.com.sgcc.gdt.opc.core.dcom.da.impl;

import cn.com.sgcc.gdt.opc.core.dcom.common.impl.BaseCOMObject;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.Constants;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;

import java.net.UnknownHostException;

/**
 * OPC浏览
 * @author ck.yang
 */
public class OPCBrowse extends BaseCOMObject {
    public OPCBrowse(final IJIComObject opcServer) throws IllegalArgumentException, UnknownHostException, JIException {
        super(opcServer.queryInterface(Constants.IOPCBrowse_IID));
    }
}
