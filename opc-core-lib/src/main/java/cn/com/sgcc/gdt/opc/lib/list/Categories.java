
package cn.com.sgcc.gdt.opc.lib.list;

public interface Categories {
    /**
     * Category of the OPC DA 1.0 Servers
     */
    public final static Category OPCDAServer10 = new Category(cn.com.sgcc.gdt.opc.core.dcom.common.Categories.OPCDAServer10);

    /**
     * Category of the OPC DA 2.0 Servers
     */
    public final static Category OPCDAServer20 = new Category(cn.com.sgcc.gdt.opc.core.dcom.common.Categories.OPCDAServer20);

    /**
     * Category of the OPC DA 3.0 Servers
     */
    public final static Category OPCDAServer30 = new Category(cn.com.sgcc.gdt.opc.core.dcom.common.Categories.OPCDAServer30);

    /**
     * Category of the XML DA 1.0 Servers
     */
    public final static Category XMLDAServer10 = new Category(cn.com.sgcc.gdt.opc.core.dcom.common.Categories.XMLDAServer10);
}
