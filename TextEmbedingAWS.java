import java.nio.file.Paths;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

public class TextEmbedingAWS {
    public static void main(String[] args) {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.builder()
            .profileFile(ProfileFile.builder().content(Paths.get("D:/JavaTest/credentials")).type(ProfileFile.Type.CREDENTIALS).build())
            .build();

        BedrockRuntimeClient bedrockClient = BedrockRuntimeClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(credentialsProvider)
            .build();

        String modelId = "amazon.titan-embed-text-v2:0";

        String inputText = "Hello my friends";
        int dimensions = 256;
        boolean normalize = true;

        var nativeRequest = """
                {
                    "inputText": "%s",
                    "dimensions": %d,
                    "normalize": %b
                }
                """.formatted(inputText, dimensions, normalize);

        var response = bedrockClient.invokeModel(request -> {
            request.modelId(modelId);
            request.body(SdkBytes.fromUtf8String(nativeRequest));
        });

        String modelResponse = response.body().asUtf8String();

        System.out.println(modelResponse);
    }
}
