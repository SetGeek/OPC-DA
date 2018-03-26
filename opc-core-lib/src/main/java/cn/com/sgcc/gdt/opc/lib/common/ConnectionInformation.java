package cn.com.sgcc.gdt.opc.lib.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionInformation {
    private String host = "localhost";

    private String domain = "localhost";

    private String user = "";

    private String password = "";

    private String clsId = null;

    private String progId = null;

    public ConnectionInformation(final String user, final String password) {
        super();
        this.user = user;
        this.password = password;
    }

    public ConnectionInformation(String host, String user, String password, String clsId){
        super();
        this.host = host;
        this.user = user;
        this.password = password;
        this.clsId = clsId;
    }

    public ConnectionInformation(final ConnectionInformation arg0) {
        super();
        this.user = arg0.user;
        this.password = arg0.password;
        this.domain = arg0.domain;
        this.host = arg0.host;
        this.progId = arg0.progId;
        this.clsId = arg0.clsId;
    }

    public String getClsOrProgId() {
        if (this.clsId != null) {
            return this.clsId;
        } else if (this.progId != null) {
            return this.progId;
        } else {
            return null;
        }
    }
}
