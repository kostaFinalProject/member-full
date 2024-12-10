//package com.example.soccer.aop;
//
//import com.example.soccer.security.CustomUserDetails;
//import com.example.soccer.service.EntityValidationService;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//@Order(1)
//public class SecurityAspect {
//
//    private final EntityValidationService entityValidationService;
//    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();
//
//    @Around("execution(* kosta.gansikshop.controller.*.*(..)) && " +
//            "!@annotation(kosta.gansikshop.aop.PublicApi) && " +
//            "!@annotation(kosta.gansikshop.aop.MailApi) && " +
//            "!@annotation(kosta.gansikshop.aop.TokenApi)")
//    public Object injectUserId(ProceedingJoinPoint joinPoint) throws Throwable {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new AccessDeniedException("인증이 필요합니다.");
//        }
//
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        String userEmail = userDetails.getUsername();
//
//        String userId = entityValidationService.validateMemberByUserId(userEmail).getId();
//
//        userIdHolder.set(userId);
//
//        try {
//            return joinPoint.proceed();
//        } finally {
//            userIdHolder.remove();
//        }
//    }
//
//    public static Long getCurrentUserId() {
//        return userIdHolder.get();
//    }
//}
