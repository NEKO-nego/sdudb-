package ds.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component

public class AliPayConfig {



    private String appId = "9021000140602871";


    private String appPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDCErNBlHP3GuZAYTWvSUm8kxoqO0HD+M86cGfxAWHyuiuLAomjzPTxyFsxIpoHDred5JCd32bhxct6WT449JenfzkAX9dI+r+vKQqSU4a84NthnuYOgJsQ4SccNNMRHlWiyJVmtnoBXVZH+Wkplmc8RwlDUI0g0YjUTRbLMmOW6Pl3CITzunSHvZYCaqahYkNwdtmsGxh2FnYEcq4DESQzuR/8Q00uHl57H/JARtbtGAhPzVczLnyPtohdB32uVAs3kIlP/tEE4x7wnZLQnwbFPtKzM4AnlzcuRdeGYbBZhg+54cizrXDR2MwlK6F+LTVVncfApApF4mfZDY0pDXJ3AgMBAAECggEBAJNJsfXRfvlI1vqBTEcN7gJJ+g+XDWB30n5dlOUZ0YZvuGA1utQv1q4xCtSNmCxHBR+1ufO2+tPaU4U1eb2sE/Sw30fjdPYUJ6n+EeHftmBenuDFwX+8JvN2jUIg7RcO2JtEc+6SCqJCfhr9tMw1M4BRwTvvRExl+dx17Ril02NRdJ/bH9jiMOQ/KdEitSN9YEnHt91jceoDIWAfrLuLLs+UXpC2RoY+iL0ZXZBHBQihFjhkwoWZRyQ2Dj9KQVO4YJhsToc3mEuaB1SUgsCKa/OQ9WpDr7JCKHCw1MPsaupW4obRXL1n4kLa0igV13WzsvV+QV2OfjSNNzO5PDfgH/ECgYEA+UbrgxL+HnRrI9qJWN3uhEu7lf3huh3cqweKat6BxzfjutFNGTTuwdGGlvDzAPFxCP4CKMFnqQKfTHIELNWw6YY7FETefjHddQxvJcM91lFwMKxfMQLXzdfzOYkfNKMGeAHAipylSUYiggNzDqEGkIfiLCgecyaWJ0GrG3FO3C0CgYEAx06i2cXL1w5pXCKwcoMHrphdAS0PLWEQGGb12Ir0SyFOx9RlSfLB0nCfHi0OUw5i/b9JB8nx/s8UPHTocgi2IfIpEJj/KLIB/oPCQr0fPmaVxkFLEg9BNER/U5dSsR/OczfuAGecDzzqq5RtJv77LemstUJx08Ir65mIopEr27MCgYAvC/yNG3uMoepKtQcxF4nZvDVmRKliqBoqyVyNePtDbz/y1WfYtw3M1M+zEzy509fCm7YqnP4EfvMzyV0SXS5rlKL321ums6CZY8RieyvjlDE5bn700BWMyxIjrMaP96jT43jEcHYdkzgWE7KWczu68Xp1XQkKMKEsPY+euU7p7QKBgAc5A0/PCjcN76WGIEzWEMY0AVLUljM3zESGHqytRHMgjuFRAiau5HVC3ZFTYqyB3faCIxxkJh/s0zfpqK7RQAB07SYL1VtdMBNQJQ8UGXjCcPURUq4h3WQOg2q9FiMTGwEz6q0bFB29QGfdZiM+pE2ipT6gXM2mwL61/qydhDBfAoGAM3i9rpMh0itydx+iqmmokuv4cqWd634+5Sgi1EClZeMXn2IfPRn4oPpGJfIQGAhC5xm5qBQGsgaMoWPFcJX2RU/M3czYCt+sPT/iQeyWmutVgR7/hmq5AQhqasVbFJETN/Is3+l26z3iDhaDoqCkjbMeDnt1ZWRVBx8A/w4PQP4=\n";


    private String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAufwbHOzcxNxDlvpFKBAtl0SpHujUOU4OFmbB5WlzyzsMJD7psqqZX734BGPm0kaWpXbyTMJcrfnJKif3nhDP4BuHVtAEp9M5gpgKBQnkjVWQbpRtE8cvlKpE2nOCubQ6jS9cZAKRmntm+jDLxaEsHKAq6KBmctwS/3TxMjcvHW+wmpB/jToR/nSL3OgJdaSfKSW2RYc9Aj3q76I8YY/yoGVuwLwqFb3C4spwV1bmfHvv5TkCt00kVyw9ZuezOvbNsM7v7drYCp7SgPIImpow5T25tmvEjx2SSET1F4VrcslC+MpjxdNAl1la/x6/iihd3QuBaSPD58ESfeB80jw1uQIDAQAB";

    private String notifyUrl = "http://aw7bgs.natappfree.cc/alipay/notify";

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppPrivateKey() {
        return appPrivateKey;
    }

    public void setAppPrivateKey(String appPrivateKey) {
        this.appPrivateKey = appPrivateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}

