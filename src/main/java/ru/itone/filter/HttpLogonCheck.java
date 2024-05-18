package ru.itone.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.itone.exception.http.HttpHeaderNotFoundException;
import ru.itone.exception.user.UserByIdNotFoundException;
import ru.itone.exception.user.UserLoginHasBeenNotCompletedException;
import ru.itone.model.user.User;
import ru.itone.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HttpLogonCheck implements HandlerInterceptor {
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String id = request.getHeader("X-User-Id");

        if (id == null) {
            throw new HttpHeaderNotFoundException("X-User-Id");
        }

        UUID userId = UUID.fromString(id);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException(userId));

        if (!user.getLogon()) {
            throw new UserLoginHasBeenNotCompletedException();
        }

        return true;
    }
}
