package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.RegisterUserDto;
import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.dto.RolesDto;
import com.ostapchuk.car.rent.dto.StatusesDto;
import com.ostapchuk.car.rent.dto.UserDto;
import com.ostapchuk.car.rent.dto.UsersDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.BalanceException;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.exception.UserUnverifiedException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.ostapchuk.car.rent.entity.Role.USER;
import static com.ostapchuk.car.rent.entity.UserStatus.ACTIVE;
import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${spring.kafka.template.email-topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User findById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find car with id: " + id));
    }

    public void verifyUser(final User user) {
        if (!user.isVerified()) {
            throw new UserUnverifiedException("You are not verified. Please, wait for the verification");
        }
        if (user.getBalance().compareTo(ZERO) < 0) {
            throw new BalanceException("The balance is negative, please, pay the debt");
        }
    }

    public void deleteById(final Long id) {
        final User user = findById(id);
        userRepository.delete(user);
    }

    public UsersDto findAll() {
        final List<UserDto> userDtos = userRepository.findAllByOrderById().stream()
                .map(userMapper::toDto)
                .toList();
        return new UsersDto(userDtos);
    }

    public ResultDto create(final RegisterUserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            return new ResultDto("Please, provide another email", false);
        }
        final User user = User.builder()
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .phone(userDto.phone())
                .balance(ZERO)
                .password(passwordEncoder.encode(userDto.password()))
                .role(USER)
                .status(ACTIVE)
                .verified(false)
                .build();
        userRepository.save(user);
        kafkaTemplate.send(topic, "");
        return new ResultDto("Successfully created your account, thank you!", true);
    }

    public ResultDto update(final UserDto userDto) {
        final User user = User.builder()
                .id(userDto.id())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .phone(userDto.phone())
                .role(Role.valueOf(userDto.role()))
                .status(UserStatus.valueOf(userDto.status()))
                .balance(userDto.balance())
                .verified(userDto.verified())
                .build();
        if (userDto.id() != null) {
            final User foundUser = userRepository.findById(userDto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Not found"));
            user.setDocument(foundUser.getDocument());
            user.setPassword(foundUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }
        userRepository.save(user);
        return new ResultDto("Successfully created your account, thank you!", true);
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public BigDecimal findBalanceById(final Long id) {
        return userRepository.findById(id).map(User::getBalance)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public StatusesDto findAllStatuses() {
        return new StatusesDto(Set.of(UserStatus.values()));
    }

    public RolesDto findAllRoles() {
        return new RolesDto(Set.of(Role.values()));
    }

    public void payDebt(final Long userId) {
        final User user = findById(userId);
        user.setBalance(ZERO);
        userRepository.save(user);
    }

    public BigDecimal findDept(final Long userId) {
        final BigDecimal total = findById(userId).getBalance();
        if (total.compareTo(BigDecimal.ZERO) >= 0) {
            throw new BalanceException("The balance is positive. Nothing to pay");
        }
        return total.negate();
    }
}
