package com.sgo.infrastructure.bootstrap;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Avisa se o segredo JWT ou a senha de seed ainda têm aparência de valor de demonstração
 * (não aborta; em produção deve-se configurar variáveis e desativar seed de demonstração).
 */
@Singleton
public class InsecureDefaultSecretWarningListener implements ApplicationEventListener<StartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(InsecureDefaultSecretWarningListener.class);

    private static final String DEV_SECRET_HINT = "changeMeDev";

    @Value("${micronaut.security.token.jwt.signatures.secret.generator.secret}")
    private String jwtGeneratorSecret;

    @Value("${sgo.seed.admin-password:}")
    private String seedAdminPassword;

    @Value("${sgo.seed.demo-accounts:true}")
    private boolean seedDemoAccounts;

    @Override
    public void onApplicationEvent(StartupEvent event) {
        if (jwtGeneratorSecret == null) {
            return;
        }
        if (jwtGeneratorSecret.length() < 32 || jwtGeneratorSecret.contains(DEV_SECRET_HINT)) {
            LOG.warn(
                    "JWT_SECRET está com valor curto ou de demonstração. "
                            + "Em produção, defina uma chave aleatória de pelo menos 32 carateres (variável de ambiente JWT_SECRET)."
            );
        }
        if (seedDemoAccounts && (seedAdminPassword == null
                || seedAdminPassword.isBlank()
                || seedAdminPassword.length() < 8
                || "Admin@123".equals(seedAdminPassword))) {
            LOG.warn(
                    "Contas de demonstração (seed) com credenciais previsíveis ou fracas. "
                            + "Em produção: SGO_SEED_DEMO_ACCOUNTS=false, e defina SGO_SEED_ADMIN_PASSWORD / SGO_SEED_USUARIO_PASSWORD fortes, "
                            + "ou crie utilizadores fora do seed."
            );
        }
    }
}
