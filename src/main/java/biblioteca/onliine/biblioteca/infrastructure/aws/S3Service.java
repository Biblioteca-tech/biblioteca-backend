package biblioteca.onliine.biblioteca.infrastructure.aws;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
public class S3Service {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(
            @Value("${aws.region}") String region,
            @Value("${aws.access-key-id}") String accessKey,
            @Value("${aws.secret-access-key}") String secretKey) {

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }
    public String uploadArquivo(String caminhoNoBucket, File arquivo) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(caminhoNoBucket)
                .build();

        s3Client.putObject(request, arquivo.toPath());

        return "https://" + bucketName + ".s3." + s3Client.serviceClientConfiguration().region().id()
                + ".amazonaws.com/" + caminhoNoBucket;
    }
}
