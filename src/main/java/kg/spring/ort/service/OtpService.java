package kg.spring.ort.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long OTP_TTL_MINUTES = 5;

    public String generateOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 digits
        redisTemplate.opsForValue().set(getKey(email), otp, OTP_TTL_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String cachedOtp = (String) redisTemplate.opsForValue().get(getKey(email));
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            redisTemplate.delete(getKey(email));
            return true;
        }
        return false;
    }

    private String getKey(String email) {
        return "otp:" + email;
    }

    public void saveOtp(String key, String value) {
        redisTemplate.opsForValue().set(key, value, 15, TimeUnit.MINUTES);
    }

    public String getOtp(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteOtp(String key) {
        redisTemplate.delete(key);
    }
}
