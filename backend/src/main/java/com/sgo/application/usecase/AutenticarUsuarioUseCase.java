package com.sgo.application.usecase;

import com.sgo.domain.exception.UnauthorizedException;
import com.sgo.domain.model.PerfilUsuario;
import com.sgo.infrastructure.persistence.entity.UsuarioEntity;
import com.sgo.infrastructure.persistence.repository.UsuarioRepository;
import com.sgo.infrastructure.security.JwtTokenService;
import com.sgo.infrastructure.security.PasswordHasher;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class AutenticarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final JwtTokenService jwtTokenService;

    public AutenticarUsuarioUseCase(
            UsuarioRepository usuarioRepository,
            PasswordHasher passwordHasher,
            JwtTokenService jwtTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public LoginResult execute(String email, String senha) {

        // Esse método poderia validar se o email e a senha vieram nulos ou vazios antes de buscar o usuário.
        // Isso evita uma consulta desnecessária no banco e deixa mais claro quais dados são obrigatórios para o login.
        // Uma opção seria verificar esses campos logo no começo e lançar UnauthorizedException caso estejam inválidos.

        UsuarioEntity user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        // A mensagem genérica "Credenciais inválidas" está bem usada aqui.
        // Ela evita informar se o problema foi no email ou na senha, o que é uma boa prática em autenticação,
        // porque dificulta a descoberta de usuários cadastrados no sistema.

        if (!passwordHasher.matches(senha, user.getSenhaHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        // Esse trecho está simples e direto, mas a geração do token está ligada diretamente ao caso de uso.
        // Em projetos maiores, pode ser interessante deixar a criação da resposta de login em um método separado,
        // para o execute ficar focado apenas no fluxo principal da autenticação.

        String token = jwtTokenService.generate(user.getId(), user.getEmail(), user.getPerfil());

        // Como esse método só faz leitura do usuário e geração de token, talvez o @Transactional não seja necessário.
        // Caso não exista alteração no banco durante o login, remover a transação pode deixar a intenção do método mais clara.

        return new LoginResult(token, "Bearer", user.getPerfil(), jwtTokenService.expiresInSeconds());
    }

    // O record LoginResult deixa o retorno do login bem organizado e evita criar uma classe maior sem necessidade.
    // Mesmo assim, se esse retorno for usado em outras partes da aplicação, pode ser melhor mover esse record
    // para um arquivo separado, deixando o caso de uso mais limpo.

    public record LoginResult(String accessToken, String tokenType, PerfilUsuario perfil, long expiresIn) {
    }
}
