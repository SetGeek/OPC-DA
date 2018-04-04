package cn.com.sgcc.gdt.opc.lib.da.browser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Leaf {
    private Branch parent = null;

    private String name = "";

    private String itemId = null;

    public Leaf(final Branch parent, final String name) {
        this.parent = parent;
        this.name = name;
    }
}
