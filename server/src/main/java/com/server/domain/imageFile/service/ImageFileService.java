package com.server.domain.imageFile.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.server.domain.imageFile.entity.ImageFile;
import com.server.domain.imageFile.repository.ImageFileRepository;
import com.server.domain.member.entity.Member;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;

    @Value("${S3_BUCKET}")
    private String s3Bucket;

    @Value("${S3_ENDPOINT}")
    private String s3EndPoint;

    private final AmazonS3Client amazonS3Client;

    public ImageFile uploadMemImg(Member member, MultipartFile multipartFile) throws IOException {

        String imageFileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        // 업로드할 파일의 사이즈
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getInputStream().available());
        // s3에 업로드
        try {
            amazonS3Client.putObject(s3Bucket + "/member", imageFileName, multipartFile.getInputStream(), objectMetadata);
            log.info("Complete Uploading to AWS S3");
        } catch (Exception e) {
            log.error("Error uploading to AWS S3, Exception: {}",e.getMessage());
        }

        String imgUrl = s3EndPoint + "/member/" + imageFileName;

        ImageFile imageFile = ImageFile.builder()
                .imageFileName(imageFileName)
                .imageFileUrl(imgUrl)
                .member(member)
                .build();

        log.info("Uploading Member's Image in DB");

        return imageFileRepository.save(imageFile);
    }

    public ImageFile uploadBlogTitleImg(MultipartFile multipartFile, Member member) throws IOException {
        String imageFileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();


        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getInputStream().available());

        try {
            amazonS3Client.putObject(s3Bucket + "/blog", imageFileName, multipartFile.getInputStream(), objectMetadata);
            log.info("Complete Uploading to AWS S3");
        } catch (Exception e) {
            log.error("Error uploading to AWS S3, Exception: {}",e.getMessage());
        }

        String imgUrl = s3EndPoint + "/blog/" + imageFileName;

        ImageFile imageFile = ImageFile.builder()
                .imageFileName(imageFileName)
                .imageFileUrl(imgUrl)
                .member(member)
                .build();

        log.info("Uploading Blog's Title Image in DB");

        return imageFileRepository.save(imageFile);
    }

//     기본 멤버 프로필 이미지 가져오기
    public String getDefaultMemImgUrl() {
        String imageFileName = "default-member";
        String imageFileUrl = s3EndPoint + "/member/" + imageFileName;

        return imageFileUrl;
    }

//     기본 썸네일 이미지 가져오기
    public String getDefaultTitleImgUrl() {
        String imageFileName = "default-title";
        String imageFileUrl = s3EndPoint + "/blog/" + imageFileName;

        return imageFileUrl;
    }
}
