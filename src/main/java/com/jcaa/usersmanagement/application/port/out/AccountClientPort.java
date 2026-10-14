package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.User;

public interface AccountClientPort {
  void createAccountForUser(User user);
}
