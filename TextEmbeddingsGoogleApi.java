import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.aiplatform.v1.AiplatformScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

public class TextEmbeddingsGoogleApi {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new FileInputStream("d:/comet-vv-ff5c459f08f6.json")
        ).createScoped(AiplatformScopes.CLOUD_PLATFORM_READ_ONLY);

        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

        PredictionServiceSettings predictionServiceSettings =
            PredictionServiceSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .setEndpoint("europe-west6-aiplatform.googleapis.com:443")
                .build();

        PredictionServiceClient predictionServiceClient = PredictionServiceClient.create(predictionServiceSettings);

        EndpointName endpointName = EndpointName.ofProjectLocationPublisherModelName("comet-vv", "europe-west6", "google", "text-multilingual-embedding-002");
        List<Value> instances = new ArrayList<>();
        Struct instance = Struct.newBuilder()
            .putFields("content", Value.newBuilder().setStringValue("Hello world").build())
            .build();
        instances.add(Value.newBuilder().setStructValue(instance).build());
        Value parameters = Value.newBuilder().build();
        PredictResponse response = predictionServiceClient.predict(endpointName, instances, parameters);

        Value prediction = response.getPredictionsList().get(0);
        Struct embeddings = prediction.getStructValue().getFieldsOrThrow("embeddings").getStructValue();

        List<Value> values = embeddings.getFieldsOrThrow("values").getListValue().getValuesList();
        values.forEach(v -> {
            System.out.print(v.getNumberValue() + ", ");
        });
        System.out.println();

        Struct statistics = embeddings.getFieldsOrThrow("statistics").getStructValue();
        boolean truncated = statistics.getFieldsOrThrow("truncated").getBoolValue();
        double tokenCount = statistics.getFieldsOrThrow("token_count").getNumberValue();
        System.out.println("Truncated: " + truncated);
        System.out.println("Token count: " + (int) tokenCount);
    }
}
