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
        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks/")) {
            // Pegar a autenticação (usuario e senha)
            String[] credentials = getCredentialsFromHeader(request);

            String user = credentials[0];
            String password = credentials[1];

            // validar usuario e senha
            validateUserAndPassword(user, password, request, response);

        }

        // Sucesso -> avança!
        doFilter(request, response, filterChain);
    }

    private String[] getCredentialsFromHeader(HttpServletRequest request) {
        String authBase64 = request.getHeader("Authorization");
        String authEncoded = authBase64.substring("Basic".length()).trim();

        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

        String authString = new String(authDecoded);

        return authString.split(":");

    }

    private void validateUserAndPassword(String username, String password, HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        UserLombokModel userOnData = userRepository.findByUsername(username);
        if (userOnData == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        } else {
            BCrypt.Result passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), userOnData.getPassword());
            if (!passwordVerify.verified) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            } else {
                //envia o usuário para frente
                request.setAttribute("idUser", userOnData.getId());

            }

        }
    }

}
