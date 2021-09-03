package tech.blockchainers.safe.web.rest.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.blockchainers.safe.domain.User;
import tech.blockchainers.safe.service.UserService;
import tech.blockchainers.safe.web.rest.SafeTransactionResource;

import java.util.Optional;

@Component
public class UserPermissionCheck {

    private final Logger log = LoggerFactory.getLogger(SafeTransactionResource.class);

    private final UserService userService;

    public UserPermissionCheck(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentlyLoggedInUser() {
        final Optional<User> isUser = userService.getUserWithAuthorities();
        if(!isUser.isPresent()) {
            log.error("User is not logged in");
            throw new IllegalAccessError("User is not logged in");
        }
        return isUser.get();
    }
}
