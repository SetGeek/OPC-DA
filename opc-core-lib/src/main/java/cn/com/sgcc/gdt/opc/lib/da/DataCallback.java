package cn.com.sgcc.gdt.opc.lib.da;

public interface DataCallback {
    void changed(Item item, ItemState itemState);
}
