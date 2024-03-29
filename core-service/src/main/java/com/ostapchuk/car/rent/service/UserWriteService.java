package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.GeneralResponse;
import com.ostapchuk.car.rent.dto.UpdateUserRequest;
import com.ostapchuk.car.rent.dto.user.RegisterUserDto;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.lang.Boolean.TRUE;

@Service
@Transactional
@RequiredArgsConstructor
public class UserWriteService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final UserMapper userMapper;

    public GeneralResponse create(final RegisterUserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            throw new EntityNotFoundException("User already exists");
        }
        final User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.password()));
        userRepository.save(user);
        return new GeneralResponse("Successfully created your account, thank you!", TRUE);
    }

    public void payDebt(final Long userId) {
        userRepository.resetBalance(userId);
    }

    public void deleteById(final Long id) {
        userRepository.deleteById(id);
    }

    public CompletableFuture<GeneralResponse> updatePassportDocument(final MultipartFile file, final Long userId) {
        return updateDocument(file, url -> userRepository.updatePassportUrl(userId, url));
    }

    public CompletableFuture<GeneralResponse> updateDrivingLicenseDocument(final MultipartFile file,
                                                                           final Long userId) {
        return updateDocument(file, url -> userRepository.updateDrivingLicenseUrl(userId, url));
    }

    public void save(final User user) {
        userRepository.save(user);
    }

    private CompletableFuture<GeneralResponse> updateDocument(final MultipartFile file,
                                                              final Consumer<String> updateImgUrl) {
        return fileService.upload(file)
                .thenApply(url -> {
                    url.ifPresent(updateImgUrl);
                    return new GeneralResponse("Successfully uploaded the file", TRUE);
                });
    }

    public GeneralResponse updateById(final UpdateUserRequest userRequest, final Long userId) {
        final User user = userRepository.findById(userId).map(u -> {
            u.setStatus(userRequest.status());
            u.setRole(userRequest.role());
            u.setVerified(userRequest.verified());
            return u;
        }).orElseThrow(() -> new EntityNotFoundException("No user with such id: " + userId));
        userRepository.save(user);
        return new GeneralResponse("Successfully updated the user!", true);
    }
}
