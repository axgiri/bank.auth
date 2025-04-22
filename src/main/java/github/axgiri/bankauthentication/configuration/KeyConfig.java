package github.axgiri.bankauthentication.configuration;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class KeyConfig {
    
  @Bean
  public RSAPrivateKey jwtPrivateKey() throws Exception {
    Resource resource = new ClassPathResource("keys/private-jwt.pem");
    try (var is = resource.getInputStream()) {
      var pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      return (RSAPrivateKey) PemConfig.readPrivateKeyFromString(pem, "RSA");
    }
  }

  @Bean
  public RSAPublicKey jwtPublicKey() throws Exception {
    Resource r = new ClassPathResource("keys/public-jwt.pem");
    try (var is = r.getInputStream()) {
      var pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      return (RSAPublicKey) PemConfig.readPublicKeyFromString(pem, "RSA");
    }
  }
}

