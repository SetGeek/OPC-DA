package cn.com.sgcc.gdt.opc.core.dcom.da.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 属性描述
 * @author ck.yang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDescription {

    private int id = -1;

    private String description = "";

    private short varType = 0;

}
