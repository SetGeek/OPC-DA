package cn.com.sgcc.gdt.opc.lib.da;

import java.util.HashMap;
import java.util.Map;

import cn.com.sgcc.gdt.opc.core.dcom.common.impl.OPCCommon;
import org.jinterop.dcom.common.JIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An error message resolver that will lookup the error code using the
 * server interface and will cache the result locally.
 *
 * @author Jens Reimann
 */
public class ErrorMessageResolver {
    private static Logger _log = LoggerFactory.getLogger(ErrorMessageResolver.class);

    private OPCCommon _opcCommon = null;

    private final Map<Integer, String> _messageCache = new HashMap<Integer, String>();

    private int _localeId = 0;

    public ErrorMessageResolver(final OPCCommon opcCommon, final int localeId) {
        super();
        this._opcCommon = opcCommon;
        this._localeId = localeId;
    }

    /**
     * Get an error message from an error code
     *
     * @param errorCode The error code to look up
     * @return the error message or <code>null</code> if no message could be looked up
     */
    public synchronized String getMessage(final int errorCode) {
        String message = this._messageCache.get(Integer.valueOf(errorCode));

        if (message == null) {
            try {
                message = this._opcCommon.getErrorString(errorCode, this._localeId);
                _log.info(String.format("Resolved %08X to '%s'", errorCode, message));
            } catch (JIException e) {
                _log.warn(String.format("Failed to resolve error code for %08X", errorCode), e);
            }
            if (message != null) {
                this._messageCache.put(errorCode, message);
            }
        }
        return message;
    }
}
