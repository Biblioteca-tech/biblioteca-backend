package biblioteca.onliine.biblioteca.infrastructure.aws;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    public S3Service(
            @Value("${aws.region}") String region,
            @Value("${aws.access-key-id}") String accessKey,
            @Value("${aws.secret-access-key}") String secretKey,
            @Value("${aws.s3.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
        this.region = region;

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

        s3Client.putObject(request, RequestBody.fromFile(arquivo));

        String url;
        if ("us-east-1".equals(region)) {
            url = "https://" + bucketName + ".s3.amazonaws.com/" + caminhoNoBucket;
        } else {
            url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + caminhoNoBucket;
        }
        return url;
    }
}
