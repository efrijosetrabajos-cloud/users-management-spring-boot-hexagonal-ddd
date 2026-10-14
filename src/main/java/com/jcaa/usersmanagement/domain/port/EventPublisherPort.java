package com.jcaa.usersmanagement.domain.port;

import com.jcaa.usersmanagement.domain.model.User;

public interface EventPublisherPort {
  void publishUserCreated(User user);
}
