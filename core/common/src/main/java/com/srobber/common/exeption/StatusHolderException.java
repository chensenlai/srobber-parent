package com.srobber.common.exeption;

import com.srobber.common.status.BaseStatus;
import lombok.Getter;

/**
 * 状态持有异常, 异常捕获器捕获后会拿状态响应给客户端
 *
 * @author chensenlai
 */
@Getter
public class StatusHolderException extends RuntimeException {

    private BaseStatus status;

    public StatusHolderException(BaseStatus status) {
        super(status.getCode()+":"+status.getMsg());
        this.status = status;
    }

    public StatusHolderException(BaseStatus status, String message) {
        super(status.getCode()+":"+message);
        this.status = status;
    }


    public StatusHolderException(BaseStatus status, Throwable cause) {
        super(status.getCode()+":"+status.getMsg(), cause);
        this.status = status;
    }

    public StatusHolderException(BaseStatus status, String message, Throwable cause) {
        super(status.getCode()+":"+message, cause);
        this.status = status;
    }
}
