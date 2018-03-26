package cn.com.sgcc.gdt.opc.lib.da;

import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcServerStatus;

public interface ServerStateListener {
    public void stateUpdate(OpcServerStatus state);
}
