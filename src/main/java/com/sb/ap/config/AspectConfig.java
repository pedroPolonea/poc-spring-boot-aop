package com.sb.ap.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class AspectConfig {

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void springRestResource() {}

    @Before("springRestResource()")
    public void beforeRestResourceAdvice(final JoinPoint joinPoint){
        MDC.put("uuid", UUID.randomUUID().toString());
        logActionInput(joinPoint);
    }

    @AfterReturning(pointcut="springRestResource()", returning="retVal")
    public void afterReturningRestResourceAdviceAdvice(final JoinPoint joinPoint, final Object retVal){
        logActionOutput(joinPoint, retVal);
        MDC.clear();
    }

    @AfterThrowing(pointcut="springRestResource()", throwing = "ex")
    public void afterThrowingRestResourceAdviceAdvice(final JoinPoint joinPoint, final Exception ex){
        logActionOutput(joinPoint, ex.getMessage());
        MDC.clear();
    }

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void springService() {}

    @Before("springService()")
    public void beforeSpringServiceAdvice(final JoinPoint joinPoint){
        logActionInput(joinPoint);
    }

    @AfterReturning(pointcut ="springService()", returning="retVal")
    public void afterReturningSpringServiceAdvice(final JoinPoint joinPoint, final Object retVal){
        logActionOutput(joinPoint, retVal);
    }

    private void logActionInput(final JoinPoint joinPoint){
        log.info("M={}, action={}, uuid={}, attributes={}",
                joinPoint.getSignature().getName(),
                ActionEnum.INPUT,
                MDC.get("uuid"),
                getMapAttributes(joinPoint)
        );
    }

    private void logActionOutput(final JoinPoint joinPoint, final Object retVal){
        log.info("M={}, action={}, uuid={}, return={} ", joinPoint.getSignature().getName(), ActionEnum.OUTPUT, MDC.get("uuid"), retVal);
    }

    private Map<Object, Object> getMapAttributes(final JoinPoint joinPoint){
        AtomicReference<Map<Object, Object>> paramsInfo = new AtomicReference<Map<Object, Object>>();
        final CodeSignature names = (CodeSignature) joinPoint.getSignature();

        try {
            // TODO
            // Verificar erro na recuperacao dos atributos
            IntStream.range(0, joinPoint.getArgs().length)
                    .forEach(index -> {
                        final Object name = names.getParameterNames()[index];
                        final Object value = joinPoint.getArgs()[index];
                        paramsInfo.get().put(name, value);
                    });
        } catch (final Exception e){
            log.warn("M=getMapAttributes, I=Nao foi possivel montar a lista de atributos do metodo., ex={}", e.getMessage());
        }


        return paramsInfo.get();
    }



    /*TODO
    * Não esta funcionando.
    * */
    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void springRepository() {}

    @Before("springRepository()")
    public void beforeSpringRepositoryAdvice(){
        log.info("M=beforeSpringRepositoryAdvice ");
    }

    @AfterReturning(pointcut ="springRepository()", returning="retVal")
    public void afterReturningSpringRepositoryAdvice(final Object retVal){
        log.info("M=afterReturningSpringServiceAdvice, retVal={}", retVal);
    }

    enum ActionEnum {
        INPUT,
        OUTPUT,
        THROWS
    }

}
