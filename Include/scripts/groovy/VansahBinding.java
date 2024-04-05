import internal.GlobalVariable;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.util.regex.Pattern;

/**
 * Class to send results to Vansah.
 */
public class VansahBinding {

	/**
     * The base URL of the Vansah API.
     */
    private String apiURL = GlobalVariable.Vansah_URL.toString();

    /**
     * The version of the Vansah API.
     */
    private String apiVersion = "v1";

    /**
     * The endpoint URL for adding a test run.
     * This URL is constructed by appending the specific API operation path to the base Vansah URL and API version,
     * facilitating the creation of new test runs in the Vansah system.
     */
    private String addTestRun = apiURL + "/api/" + apiVersion + "/run";

    /**
     * The name of the sprint.
     */
    private String sprintName = GlobalVariable.SprintName.toString();

    /**
     * The name of the release.
     */
    private String releaseName = GlobalVariable.ReleaseName.toString();

    /**
     * The name of the environment.
     */
    private String environmentName = GlobalVariable.EnvironmentName.toString();

    /**
     * The HTTP client used to execute HTTP requests.
     */
    private CloseableHttpClient httpClient = null;

    /**
     * The HTTP POST request used to send data to the Vansah API.
     */
    private HttpPost httpPost = null;

    /**
     * Sends results to Vansah.
     *
     * @param testCaseKey The key of the test case.
     * @param assetKey    The key of the asset (issue key or folder identifier).
     * @param result      The result of the test run.
     */
    public void sendResultstoVansah(String testCaseKey, String assetKey, String result) {
        httpClient = HttpClientBuilder.create().build();
        httpPost = new HttpPost(addTestRun);
        httpPost.addHeader("Authorization", System.getenv("VANSAH_TOKEN"));
        httpPost.addHeader("Content-Type", "application/json");

        try {
            String requestBody = requestBodyJSON(testCaseKey, assetKey, result).toString();
            StringEntity entity = new StringEntity(requestBody);
            httpPost.setEntity(entity);

            // Execute the request and get the response
            HttpResponse response = httpClient.execute(httpPost);

            // Get the response status code
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Vansah Response status code: " + statusCode);

            // Get the response body
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);
            System.out.println("Vansah Response Message: " + new JSONObject(responseBody).getString("message"));

            // Close the response
            EntityUtils.consume(responseEntity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the HttpClient
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructs the JSON request body.
     *
     * @param testCaseKey The key of the test case.
     * @param asset       The key of the asset (issue key or folder identifier).
     * @param result      The result of the test run.
     * @return The JSON request body.
     */
    public JSONObject requestBodyJSON(String testCaseKey, String asset, String result) {
        JSONObject requestBody = new JSONObject();
        JSONObject caseObject = new JSONObject();
        JSONObject assetObject = new JSONObject();
        JSONObject resultObject = new JSONObject();
        JSONObject propertiesObject = new JSONObject();
        JSONObject environmentObject = new JSONObject();
        JSONObject releaseObject = new JSONObject();
        JSONObject sprintObject = new JSONObject();

        // Create caseObject
        caseObject.put("key", testCaseKey);
        requestBody.put("case", caseObject);

        // Create assetObject
        if (isIssueKey(asset)) {
            assetObject.put("type", "issue");
            assetObject.put("key", asset);
        } else {
            assetObject.put("type", "folder");
            assetObject.put("identifier", asset);
        }
        requestBody.put("asset", assetObject);

        // Create resultObject
        resultObject.put("name", result);
        requestBody.put("result", resultObject);

        // Create Test Run Properties Object
        if (sprintName.trim().length() != 0) {
            sprintObject.put("name", sprintName);
            propertiesObject.put("sprint", sprintObject);
        }
        if (releaseName.trim().length() != 0) {
            releaseObject.put("name", releaseName);
            propertiesObject.put("release", releaseObject);
        }
        if (environmentName.trim().length() != 0) {
            environmentObject.put("name", environmentName);
            propertiesObject.put("environment", environmentObject);
        }
        requestBody.put("properties", propertiesObject);

        return requestBody;
    }

    /**
     * Checks if the provided key is an issue key.
     *
     * @param key The key to check.
     * @return {@code true} if the key is an issue key, {@code false} otherwise.
     */
    public boolean isIssueKey(String key) {
        // Check if the key matches the pattern of an Issue Key (e.g., "ABC-123")
        return Pattern.matches("[A-Z]+-[0-9]+", key);
    }

}
