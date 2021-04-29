package com.srobber.common.spring.mvc.advice;

import com.srobber.common.exeption.*;
import com.srobber.common.result.JsonResult;
import com.srobber.common.status.BaseStatus;
import com.srobber.common.status.CommonStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 异常统一处理
 * 把客户端UserLoginInfo和UserAgentInfo打印出来
 * 方便发现出现问题的客户端
 *
 * @author chensenlai
 **/
@Slf4j
@ControllerAdvice
public class ExceptionAdviceHandler {
    
    /**
     * 处理状态持有异常
     */
    @ExceptionHandler(StatusHolderException.class)
    @ResponseBody
    public JsonResult<Void> handleStatusHolderException(StatusHolderException e, HttpServletRequest request) {
        BaseStatus status = e.getStatus();
        String message = e.getMessage();
        log.error("statusException:", e);
        return JsonResult.resp(status, message);
    }

    /**
     * 处理参数异常
     **/
    @ExceptionHandler({ParamException.class, BindException.class,
            MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseBody
    public JsonResult<Void> handleParamException(Exception e, HttpServletRequest request) {
        String message = e.getMessage();
        BindingResult bindingResult = null;
        if(e instanceof BindException) {
            bindingResult = ((BindException)e).getBindingResult();
        }
        if(e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();
        }
        if(bindingResult != null && bindingResult.hasErrors()) {
            message = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(","));
        }
        log.error("paramException:", e);
        return JsonResult.resp(CommonStatus.ParamInvalid, message);
    }

    /**
     * 处理业务异常
     **/
    @ExceptionHandler({BusinessException.class})
    @ResponseBody
    public JsonResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        String message = e.getMessage();
        log.error("businessException:", e);
        return JsonResult.fail(message);
    }

    /**
     * 处理静默业务异常
     */
    @ExceptionHandler({SilentBusinessException.class})
    @ResponseBody
    public JsonResult<Void> handleSilentBusinessException(SilentBusinessException e, ServletWebRequest webRequest) {
        String message = e.getMessage();
        return JsonResult.fail(message);
    }

    /**
     * 处理包装异常
     **/
    @ExceptionHandler(WrapException.class)
    @ResponseBody
    public JsonResult<Void> handleWrapException(WrapException e, HttpServletRequest request) {
        Throwable cause = e.getCause();
        String message = cause.getMessage();
        log.error("wrapException:", cause);
        return JsonResult.fail(message);
    }

    /**
     * 处理其他异常
     **/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonResult<Void> handleOtherException(Throwable e, HttpServletRequest request) {
        String message = e.getMessage();
        log.error("otherException:", e);
        return JsonResult.fail(message);
    }
}
