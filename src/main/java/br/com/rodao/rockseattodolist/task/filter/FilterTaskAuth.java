package br.com.rodao.rockseattodolist.task.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.rodao.rockseattodolist.user.UserRepository;
import br.com.rodao.rockseattodolist.user.dto.UserLombokModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Pegar a autenticação (usuario e senha)
        String authBase64 = request.getHeader("Authorization");
        String authEncoded = authBase64.substring("Basic".length()).trim();

        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

        String authString = new String(authDecoded);

        String[] credentials = authString.split(":");

        String user = credentials[0];
        String password = credentials[1];

        // validar usuario
        // validar senha
        validateUserAndPassword(user, password, response);

        // Sucesso -> avança!
        doFilter(request, response, filterChain);
    }

    private void validateUserAndPassword(String username, String password, HttpServletResponse response) throws IOException {
        UserLombokModel userOnData = userRepository.findByUsername(username);
        if (userOnData == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        } else {
            BCrypt.Result passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), userOnData.getPassword());
            if (!passwordVerify.verified) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            }

        }
    }

}
