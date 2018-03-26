package cn.com.sgcc.gdt.opc.lib.da.demo;

import cn.com.sgcc.gdt.opc.lib.da.DataCallback;
import cn.com.sgcc.gdt.opc.lib.da.Item;
import cn.com.sgcc.gdt.opc.lib.da.ItemState;
import org.jinterop.dcom.common.JIException;

public class DataCallbackDumper implements DataCallback {

    public void changed(final Item item, final ItemState itemState) {
        System.out.println(String.format("Item: %s, Value: %s, Timestamp: %tc, Quality: %d", item.getId(), itemState.getValue(), itemState.getTimestamp(), itemState.getQuality()));

        try {
            VariantDumper.dumpValue("\t", itemState.getValue());
        } catch (final JIException e) {
            e.printStackTrace();
        }

    }

}
