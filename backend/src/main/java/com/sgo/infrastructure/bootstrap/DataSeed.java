package com.sgo.infrastructure.bootstrap;

import com.sgo.domain.model.PerfilUsuario;
import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.entity.LocalEntity;
import com.sgo.infrastructure.persistence.entity.PaisEntity;
import com.sgo.infrastructure.persistence.entity.UsuarioEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import com.sgo.infrastructure.persistence.repository.LocalRepository;
import com.sgo.infrastructure.persistence.repository.PaisRepository;
import com.sgo.infrastructure.persistence.repository.UsuarioRepository;
import com.sgo.infrastructure.security.PasswordHasher;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Singleton
public class DataSeed implements ApplicationEventListener<StartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DataSeed.class);

    public static final UUID ID_PAIS_BR = UUID.fromString("11111111-1111-4111-8111-111111111101");
    public static final UUID ID_PAIS_US = UUID.fromString("11111111-1111-4111-8111-111111111102");
    public static final UUID ID_LOCAL_PRINCIPAL = UUID.fromString("22222222-2222-4222-8222-222222222201");
    public static final UUID ID_ATLETA_1 = UUID.fromString("33333333-3333-4333-8333-333333333301");
    public static final UUID ID_ATLETA_2 = UUID.fromString("33333333-3333-4333-8333-333333333302");

    private final UsuarioRepository usuarioRepository;
    private final PaisRepository paisRepository;
    private final LocalRepository localRepository;
    private final AtletaRepository atletaRepository;
    private final PasswordHasher passwordHasher;
    private final boolean seedDemoAccounts;
    private final String seedAdminPassword;
    private final String seedUsuarioPassword;

    public DataSeed(
            UsuarioRepository usuarioRepository,
            PaisRepository paisRepository,
            LocalRepository localRepository,
            AtletaRepository atletaRepository,
            PasswordHasher passwordHasher,
            @Value("${sgo.seed.demo-accounts:true}") boolean seedDemoAccounts,
            @Value("${sgo.seed.admin-password:Admin@123}") String seedAdminPassword,
            @Value("${sgo.seed.usuario-password:Usuario@123}") String seedUsuarioPassword
    ) {
        this.usuarioRepository = usuarioRepository;
        this.paisRepository = paisRepository;
        this.localRepository = localRepository;
        this.atletaRepository = atletaRepository;
        this.passwordHasher = passwordHasher;
        this.seedDemoAccounts = seedDemoAccounts;
        this.seedAdminPassword = seedAdminPassword;
        this.seedUsuarioPassword = seedUsuarioPassword;
    }

    @Override
    @Transactional
    public void onApplicationEvent(StartupEvent event) {
        if (usuarioRepository.count() == 0) {
            if (seedDemoAccounts) {
                if (notUsableForSeed(seedAdminPassword) || notUsableForSeed(seedUsuarioPassword)) {
                    LOG.warn("Seed de utilizadores ignorado: sgo.seed.admin-password / sgo.seed.usuario-password inexistentes, vazias ou com menos de 8 carateres (use SGO_SEED_*).");
                } else {
                    UsuarioEntity admin = new UsuarioEntity();
                    admin.setId(UUID.randomUUID());
                    admin.setEmail("admin@sgo.local");
                    admin.setSenhaHash(passwordHasher.hash(seedAdminPassword));
                    admin.setPerfil(PerfilUsuario.ADMIN);

                    UsuarioEntity usuario = new UsuarioEntity();
                    usuario.setId(UUID.randomUUID());
                    usuario.setEmail("usuario@sgo.local");
                    usuario.setSenhaHash(passwordHasher.hash(seedUsuarioPassword));
                    usuario.setPerfil(PerfilUsuario.USUARIO);

                    usuarioRepository.save(admin);
                    usuarioRepository.save(usuario);
                }
            } else {
                LOG.info("Banco sem utilizadores e sgo.seed.demo-accounts (SGO_SEED_DEMO_ACCOUNTS) desativado. Crie a primeira conta por processo controlado (migração, outro serviço).");
            }
        }

        if (paisRepository.count() == 0) {
            PaisEntity br = new PaisEntity();
            br.setId(ID_PAIS_BR);
            br.setNome("Brasil");
            br.setCodigoIso("BRA");

            PaisEntity us = new PaisEntity();
            us.setId(ID_PAIS_US);
            us.setNome("Estados Unidos");
            us.setCodigoIso("USA");

            paisRepository.save(br);
            paisRepository.save(us);
        }

        if (localRepository.count() == 0) {
            LocalEntity local = new LocalEntity();
            local.setId(ID_LOCAL_PRINCIPAL);
            local.setNome("Arena Central");
            local.setCidade("Cidade Olímpica");
            local.setCapacidade(50000);
            localRepository.save(local);
        }

        if (atletaRepository.count() == 0) {
            PaisEntity br = paisRepository.findById(ID_PAIS_BR).orElseThrow();
            PaisEntity us = paisRepository.findById(ID_PAIS_US).orElseThrow();

            AtletaEntity a1 = new AtletaEntity();
            a1.setId(ID_ATLETA_1);
            a1.setNome("Atleta Brasil");
            a1.setPais(br);

            AtletaEntity a2 = new AtletaEntity();
            a2.setId(ID_ATLETA_2);
            a2.setNome("Atleta EUA");
            a2.setPais(us);

            atletaRepository.save(a1);
            atletaRepository.save(a2);
        }
    }

    private static boolean notUsableForSeed(String s) {
        return s == null || s.isBlank() || s.length() < 8;
    }
}
