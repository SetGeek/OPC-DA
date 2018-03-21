package cn.com.sgcc.gdt.opc.lib.da;

import lombok.*;
import org.jinterop.dcom.core.JIVariant;

import java.util.Calendar;

/**
 * 数据项状态信息
 * @author: ck.yang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemState {
    /** 错误码 */
    private int errorCode = 0;
    /** 值 */
    private JIVariant value = null;
    /** 时间 */
    private Calendar timestamp = null;
    /** 质量 */
    private Short quality = null;

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.errorCode;
        result = PRIME * result + (this.quality == null ? 0 : this.quality.hashCode());
        result = PRIME * result + (this.timestamp == null ? 0 : this.timestamp.hashCode());
        result = PRIME * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemState other = (ItemState) obj;
        if (this.errorCode != other.errorCode) {
            return false;
        }
        if (this.quality == null) {
            if (other.quality != null) {
                return false;
            }
        } else if (!this.quality.equals(other.quality)) {
            return false;
        }
        if (this.timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!this.timestamp.equals(other.timestamp)) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
