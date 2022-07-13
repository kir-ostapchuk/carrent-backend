package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.UploadRequestDto;
import com.ostapchuk.car.rent.entity.Document;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.repository.DocumentRepository;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.ostapchuk.car.rent.entity.DocumentType.USER;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final FileService fileService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public void save(final MultipartFile multipartFile, final UploadRequestDto requestDto) {
        final User user = userService.findById(requestDto.userId());
        fileService.save(multipartFile).thenAccept(r -> r.ifPresent(link -> {
            final Document document = switch (requestDto.imgNumber()) {
                case 1 -> {
                    if (user.getDocument() == null) {
                        yield Document.builder()
                                .imgLink1(link)
                                .type(USER)
                                .build();
                    } else {
                        final Document tempDoc = user.getDocument();
                        tempDoc.setImgLink1(link);
                        yield tempDoc;
                    }
                }
                case 2 -> {
                    final Document doc = user.getDocument();
                    doc.setImgLink2(link);
                    yield doc;
                }
                default -> throw new RuntimeException("No such img number exception");
            };
            user.setDocument(documentRepository.save(document));
            userRepository.save(user);
        }));
    }
}
