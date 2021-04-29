package com.srobber.third.apple.oauth;

import com.srobber.common.util.HttpsClient;
import com.srobber.common.util.JsonUtil;
import com.srobber.common.util.Pair;
import com.srobber.third.apple.oauth.model.*;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 苹果帐号授权登陆管理器
 * https://developer.apple.com/documentation/signinwithapplerestapi/fetch_apple_s_public_key_for_verifying_token_signature
 *
 * @author chensenlai
 * 2020-09-18 上午9:52
 */
@Slf4j
@Data
public class AppleOAuthThirdImpl implements AppleOAuthThird {

    /**
     * 获取苹果公钥
     */
    private static final String AUTH_KEYS_URL = "https://appleid.apple.com/auth/keys";

    @Override
    public UserIdentity getUserIdentity(String identityTokenJwt) {
        try {
            Pair<IdentityTokenHeader, IdentityTokenPayload> tokenPair = parserIdentityToken(identityTokenJwt);
            IdentityTokenHeader header = tokenPair.getA();
            IdentityTokenPayload payload = tokenPair.getB();
            AppleKey authKey = getMatchAuthKeys(header);
            PublicKey publicKey = buildPublicKey(authKey.getKty(), authKey.getN(), authKey.getE());
            boolean ok = verify(publicKey, identityTokenJwt, payload.getAud(), payload.getSub());
            if(!ok) {
                log.warn("apple jwt verify fail {}", identityTokenJwt);
                return null;
            }
            UserIdentity identity = new UserIdentity();
            identity.setSub(payload.getSub());
            return identity;
        } catch (Exception e) {
            log.error("apple identity error", e);
            return null;
        }
    }

    /**
     * 获取和jwt算法匹配上的authKey
     * @param header
     * @return
     */
    private AppleKey getMatchAuthKeys(IdentityTokenHeader header) {
        List<AppleKey> authKeys = getAuthKeys();
        Optional<AppleKey> appleKeyOpt = authKeys.stream().filter((authKey)->{
            if(Objects.equals(authKey.getKid(), header.getKid())
                    && Objects.equals(authKey.getAlg(), header.getAlg())) {
                return true;
            }
            return false;
        }).findFirst();
        if(!appleKeyOpt.isPresent()) {
            log.error("apple authKeys no match, except {} but {}", JsonUtil.toStr(header), JsonUtil.toStr(authKeys));
            return null;
        }
        return appleKeyOpt.get();
    }

    /**
     * 获取苹果公钥
     *
     * @return 公钥
     */
    private List<AppleKey> getAuthKeys() {
        String jsonStr = HttpsClient.get(AUTH_KEYS_URL);
        try {
            AppleKeys appleKeys = JsonUtil.toObject(jsonStr, AppleKeys.class);
            return appleKeys.getKeys();
        } catch (Exception e) {
            log.error("apple authKeys error", e);
            return Collections.emptyList();
        }

    }

    /**
     * 对前端传来的JWT字符串identityToken的第二部分进行解码
     * 主要获取其中的aud和sub，aud大概对应ios前端的包名，sub大概对应当前用户的授权的openID
     *
     * @param identityToken 身份token
     * @return {"aud":"com.xkj.****","sub":"000***.8da764d3f9e34d2183e8da08a1057***.0***","c_hash":"UsKAuEoI-****","email_verified":"true","auth_time":1574673481,"iss":"https://appleid.apple.com","exp":1574674081,"iat":1574673481,"email":"****@qq.com"}
     */
    private Pair<IdentityTokenHeader, IdentityTokenPayload> parserIdentityToken(String identityToken) {
        String[] arr = identityToken.split("\\.");
        String headerStr = new String(Base64.decodeBase64(arr[0]));
        String payloadStr = new String(Base64.decodeBase64(arr[1]));
        IdentityTokenHeader header = JsonUtil.toObject(headerStr, IdentityTokenHeader.class);
        IdentityTokenPayload payload = JsonUtil.toObject(payloadStr, IdentityTokenPayload.class);
        return new Pair<>(header, payload);
    }


    /**
     * 构建公钥
     * @param kty authKey kty
     * @param n authKey n
     * @param e authKey e
     * @return 公钥
     */
    public PublicKey buildPublicKey(String kty, String n, String e) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger modulus = new BigInteger(1, Base64.decodeBase64(n));
        BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(e));
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
        KeyFactory kf = KeyFactory.getInstance(kty);
        return kf.generatePublic(spec);
    }


        /**
         * 验证 JWT token有效性
         * @param key
         * @param jwt
         * @param aud
         * @param sub
         * @return
         */
    public boolean verify(PublicKey key, String jwt, String aud, String sub) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(key);
        jwtParser.requireIssuer("https://appleid.apple.com");
        jwtParser.requireAudience(aud);
        jwtParser.requireSubject(sub);
        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(jwt);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                return true;
            }
            return false;
        } catch (ExpiredJwtException e) {
            log.error("apple identityToken expired", e);
            return false;
        } catch (Exception e) {
            log.error("apple identityToken illegal", e);
            return false;
        }

    }
}