package com.learning.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    private final Map<String, CodeInfo> codeStore = new ConcurrentHashMap<>();

    private static class CodeInfo {
        String code;
        long createTime;
        int verifyCount;

        CodeInfo(String code) {
            this.code = code;
            this.createTime = System.currentTimeMillis();
            this.verifyCount = 0;
        }
    }

    public String generateCode(String phone) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        codeStore.put(phone, new CodeInfo(code));
        return code;
    }

    public boolean verifyCode(String phone, String code) {
        CodeInfo codeInfo = codeStore.get(phone);
        if (codeInfo == null) {
            return false;
        }

        if (System.currentTimeMillis() - codeInfo.createTime > TimeUnit.MINUTES.toMillis(5)) {
            codeStore.remove(phone);
            return false;
        }

        if (codeInfo.verifyCount >= 3) {
            codeStore.remove(phone);
            return false;
        }

        codeInfo.verifyCount++;
        return codeInfo.code.equals(code);
    }

    public boolean hasPhoneForUser(String phone) {
        return codeStore.containsKey(phone);
    }
}