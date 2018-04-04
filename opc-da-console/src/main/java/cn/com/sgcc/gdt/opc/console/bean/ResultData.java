package cn.com.sgcc.gdt.opc.console.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * TODO
 *
 * @author ck
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResultData implements Serializable {
    private String msNo;
    private String indi;
    private Double rval;
    private Date dataDate;

}
