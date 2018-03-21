package cn.com.sgcc.gdt.opc.lib.da;

/**
 * 通道状态监听器
 * @author: ck.yang
 */
public interface AccessStateListener {
    public abstract void stateChanged(boolean state);

    public abstract void errorOccured(Throwable t);
}
