package io.github.mocanjie.base.mymvc.aspect;

import io.github.mocanjie.base.mycommon.exception.BusinessException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import jakarta.validation.Path;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



/**
 * spring mvc 请求参数验证拦截
 * 拦截所有带有@org.springframework.validation.annotation.Validated注解的controller
 * @author mo
 *
 */
@Aspect
public class RequestParamValidAspect {

    @Pointcut("@annotation(org.springframework.validation.annotation.Validated) ")
    public void controllerBefore(){};

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExecutableValidator executableValidator;

    public RequestParamValidAspect(Validator validator) {
        this.executableValidator = validator.forExecutables();
    }

    @Before("controllerBefore()")
    public void before(JoinPoint point) throws NoSuchMethodException, SecurityException{
        //  获得切入目标对象
        Object target = point.getThis();
        // 获得切入方法参数
        Object [] args = point.getArgs();
        // 获得切入的方法
        Method method = ((MethodSignature)point.getSignature()).getMethod();
        // 执行校验，获得校验结果
        Set<ConstraintViolation<Object>> validResult = validMethodParams(target, method, args);
        if (!validResult.isEmpty()) {
            String [] parameterNames = parameterNameDiscoverer.getParameterNames(method); // 获得方法的参数名称
            List<String> errors = validResult.stream().map(constraintViolation -> {
                Path propertyPath = constraintViolation.getPropertyPath();  // 获得校验的参数路径信息
                Path.Node leafNode = null;
                for (Path.Node node : propertyPath) {
                    leafNode = node;  // 获取最后一个节点
                }
                int paramIndex = leafNode.as(Path.ParameterNode.class).getParameterIndex(); // 获得校验的参数位置
                String paramName = parameterNames[paramIndex];  // 获得校验的参数名称
                return paramName + " " + constraintViolation.getMessage();
            }).collect(Collectors.toList());
            throw new BusinessException(errors.toString());  // 我个人的处理方式，抛出异常，交给上层处理
        }
    }

    private <T> Set<ConstraintViolation<T>> validMethodParams(T obj, Method method, Object [] params){
        return executableValidator.validateParameters(obj, method, params);
    }
}
